package db;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Implementation of the {@link DBAccess} interface as defined in the instructions file. Any information 
 * read in from the db-file is stored as an attribute. Therefore elements of for example the 
 * schema description can be provided to users of this class. <br>
 * The records which are read in are stored in {@link DBRecord} objects
 * which are connected to a long value, the locking cookie 
 * that is needed to perform any changing actions on the record (to prevent unallowed access
 * to a record which is already in use). This is necessary because these changing actions like
 * updating records or adding new records contain a possibly time consuming synchronization 
 * with the db-file and so it is quite probable that this process can be interrupted by 
 * another thread.<br>
 * Locking cookies are long values generated by specific information gained from the {@link DBRecord}
 * in combination with the current system time. A cookie value of <code>0L</code> means that the
 * record is currently not locked. Locking and unlocking can only be performed via the
 * corresponding methods.
 * 
 * @author Lars Gerhard
 * @see DBRecord
 */
public class Data implements DBAccess {

	// Buffer for reading  from DB-File
	private ByteBuffer readBuffer;
	private static File dbFile;

	// Attributes extracted from DB-File
	private int magicCookie;
	private int recordLength;
	private short recordFields;

	/**
	 * The schema description as given an the instructions is saved 
	 * in a {@link LinkedHashMap}. It contains <code>[String,Short]</code> 
	 * pairs representing the field-name and the corresponding field-length.
	 */
	private LinkedHashMap<String,Short> schemaDescription;
	/**
	 * Cache for the contents of db-file as defined in the data section in 
	 * the instructions. Single records are saved as instances of {@link DBRecord} 
	 * in the keys of the {@link HashMap}, values are representing
	 * the locking cookie which is needed to lock single records in case of 
	 * non-granted access.
	 */
	private HashMap<DBRecord,Long> dbContents;

	private Logger log;

	/**
	 * Instantiates a new Data object.
	 * 
	 * @param dbLocation the local file path is passed as a String parameter
	 */
	public Data(String dbLocation) {		
		//Logging	
		log = Logger.getLogger("db");
		log.setUseParentHandlers(true);

		// attributes init		
		schemaDescription = new LinkedHashMap<>();
		dbContents = new HashMap<>();

		dbFile = new File(dbLocation);

		try {
			RandomAccessFile file = new RandomAccessFile(dbFile,"r");
			FileChannel inChannel = file.getChannel();
			long fSize = inChannel.size();
			readBuffer = ByteBuffer.allocate((int) fSize);
			inChannel.read(readBuffer);
			readInByteBuffer();
			inChannel.close();
			file.close();			
		} catch (FileNotFoundException e) {
			log.throwing("Data", "Constructor", e);
		} catch (IOException e) {
			log.throwing("Data", "Constructor", e);
		} catch (BufferUnderflowException e) {
			log.throwing("Data", "Constructor", e);
		} 
	}


	/**
	 * This method extracts all information from a byte buffer 
	 * (read in from a db-file with the given format!)
	 * and saves it as single attributes
	 */
	private void readInByteBuffer() {
		readBuffer.flip();
		// 1.) start of file
		magicCookie = readBuffer.getInt(0);
		recordLength = readBuffer.getInt(4);
		recordFields = readBuffer.getShort(8);

		// 2.) schema description section
		readBuffer.position(10);
		for (int i=0; i<recordFields; i++) {
			short fieldNameLength = readBuffer.getShort();			
			char[] fieldNameArray = new char[fieldNameLength];
			for (int j = 0; j<fieldNameLength; j++) {
				fieldNameArray[j] =  (char)readBuffer.get();
			}		
			String fieldName = new String(fieldNameArray);
			short fieldLength = readBuffer.getShort();
			schemaDescription.put(fieldName,fieldLength);
			log.finest("Description Entry "+ i + " - name length: " + fieldNameLength + " - name: " + fieldName + " - field length: " + fieldLength);

		}

		// 3.) Data section:
		while (readBuffer.hasRemaining()) {
			boolean deleted = false;
			if (readBuffer.get() == 1) {
				deleted = true;
			}			
			DBRecord rec = new DBRecord(recordFields, deleted);			
			for (Entry<String, Short> e : schemaDescription.entrySet()) {
				short length = e.getValue();
				char[] value = new char[length];
				for (int i=0; i<length; i++) {
					value[i] = (char)readBuffer.get();					
				}				
				rec.addEntry(e.getKey(), new String(value));				
			}
			dbContents.put(rec,(long) 0);

		}
		log.finest("Reading from DB-File finished \n" + 
				"\n Header information: " + "Magic Cookie: " + magicCookie + ", Record Length: " + recordLength + ", Fields per record: " + recordFields +
				"\n Schema Description: " + schemaDescription.toString() +
				"\n " + dbContents.size() + " record entries: \n" + dbContents.toString());
		log.exiting("Data", "readInByteBuffer");

	}

	/**
	 * This method copies the contents of the internal record cache back to 
	 * the db-file on the harddisk. It should be called after updating, deleting 
	 * and creating a record.
	 */
	private void syncWithFile() {
		// 0.) ByteArrayOutputSteam and DataOutputStream to convert the record to a byte array
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		DataOutputStream dataOutStream = new DataOutputStream(arrayStream);
		try {
			// 1.) start of file
			dataOutStream.writeInt(magicCookie);
			dataOutStream.writeInt(recordLength);
			dataOutStream.writeShort(recordFields);


			// 2.) schema description
			for (Entry<String, Short> e : schemaDescription.entrySet()) {
				String fieldName = e.getKey();
				dataOutStream.writeShort(fieldName.length());
				for (char c : fieldName.toCharArray()) {
					dataOutStream.write(c);
				}
				short length = e.getValue();
				dataOutStream.writeShort(length);
			}

			// 3.) data section
			for (DBRecord rec : dbContents.keySet()) {
				if (rec.isDeleted()) {
					dataOutStream.write(1);
				} else {
					dataOutStream.write(0);
				}

				for (String val : rec.toStringArray()) {
					for (char c : val.toCharArray()) {
						dataOutStream.write(c);
					}
				}
			}
		} catch (IOException e1) {
			log.throwing("Data", "syncWithFile", e1);
		}

		// Convert the ByteArrayOutputStream to a ByteBuffer
		ByteBuffer buffer = ByteBuffer.wrap(arrayStream.toByteArray());
		// Writing the ByteBuffer back to the db-file
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(dbFile.getPath(),"rw");
			FileChannel outChannel = file.getChannel();
			outChannel.write(buffer);
			outChannel.close();
			file.close();			
		} catch (FileNotFoundException e) {
			log.throwing("Data","syncWithFile",e);
		}  catch (IOException e) {
			log.throwing("Data","syncWithFile",e);
		}
		log.finest("Synchonisation finished! \n" + 
				"\n Header information: " + "Magic Cookie: " + magicCookie + ", Record Length: " + recordLength + ", Fields per record: " + recordFields +
				dbContents.size() + " records in cache written to file: \n" + arrayStream.toString());
		log.exiting("Data","syncWithFile");
	}

	/**
	 * Looks for records which match the specified criteria. A record is a
	 * positive match if for <code>n</code> in <code>0..criteria.length</code>
	 * either <code>value[n]</code> starts with <code>criteria[n]</code> or 
	 * if <code>criteria[n]</code> is <code>null</code>. <br>
	 * All possible matches are returned by their record number in an array. 
	 * 
	 * @param criteria an array containing the search-criteria. As specified in
	 * the instructions, criteria should contain
	 * six fields: [name, location, specialties, size, rate, owner]
	 * @return array containing the matching record numbers as long values
	 */
	@Override
	public long[] findByCriteria(String[] criteria) {
		LinkedList<Long> ergList = new LinkedList<>();

		for (DBRecord rec : dbContents.keySet()){
			String[] recArr = rec.toStringArray();
			if (recArr.length == criteria.length) { // length check!
				boolean matchFound = true;
				for (int i = 0; i < recArr.length; i++) { // check every value!
					if (criteria[i] != null && !recArr[i].startsWith(criteria[i])) {
						matchFound = false;	
						break;
					} 
				}				 
				if (matchFound) {
					ergList.add((long)rec.getKey());
				}
			}
		}
		// Conversion to long[]
		long[] ergArr = new long[ergList.size()];
		int i=0;
		for (Long l : ergList.toArray(new Long[0])) {
			ergArr[i] = l.longValue();
			i++;
		}
		return ergArr;
	}

	/**
	 * Reads a record from the internal database - e.g. Record Number 0: 
	 * ["10","Buonarotti &amp; Company","Smallville","Air Conditioning, Painting, 
	 * Painting","$40.00"] 
	 * (unnecessary blanks removed for convenience)  
	 * 
	 * @param recNo the number of the record to be read
	 * @return a <code>String</code> array containing the field contents of the record 
	 * (schema description headers and deleted flag not included!)
	 * @throws RecordNotFoundException if the record number is not found.
	 */
	@Override
	public String[] readRecord(long recNo) throws RecordNotFoundException {
		String[] erg = null;

		for (DBRecord rec : dbContents.keySet()) {
			if (rec.getKey() == recNo && !rec.isDeleted()) {
				erg = rec.toStringArray(); 
			}
		}
		if (erg == null) {
			throw new RecordNotFoundException("Record no. " + recNo + " can not be read!");
		} else {
			return erg;
		}
	}

	/**
	 * Updates the record with the given number with the data provided in the array.
	 * Before the update the locking cookie is checked (to check the permission for
	 * the change).
	 * 
	 * @param recNo the number of the record to be updated
	 * @param data array containing the new values for the fields of the record. 
	 * Order of the elements should be the same as defined in the db-scheme 
	 * (name, location, specialities, size, rate, owner)
	 * @param lockCookie the locking cookie which is needed to modify the record
	 * @throws RecordNotFoundException if the length of the data param is not equal 
	 * to the number of fields, if the <code>recNo</code> is not found.
	 * @throws SecurityException if the locking cookie is not correct 
	 */
	@Override
	public void updateRecord(long recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		boolean found = false;
		for (DBRecord rec : dbContents.keySet()) {
			if (rec.getKey() == recNo && !rec.isDeleted()) {
				found = true;
				if (dbContents.get(rec) == lockCookie) {
					int i = 0;					
					if (rec.getFieldNames().size() != data.length) { // same length?
						throw new RecordNotFoundException("Record no. " + recNo + " update failed. (Wrong field length of param)");
					}
					for (String name : rec.getFieldNames()) {
						rec.addEntry(name, data[i]);  
						i++;
					}
					syncWithFile();
				} else {
					throw new SecurityException("Locking cookie " + lockCookie + " for record no. " + recNo + " not valid.");
				}
			}
		}
		if (!found) {
			throw new RecordNotFoundException("Record no. " + recNo + " can not be updated!");
		}

	}

	/**
	 * Deletes a record by setting the deleted flag. Therefore no data is actually deleted 
	 * in the cache. Physical deletion is done by synchronizing the record cache with 
	 * the file system.<br>
	 * After setting the deleted flag the locking cookie which was associated to the record will be
	 * reset to 0 (= not locked).
	 * 
	 * @param recNo the number of the record to be deleted
	 * @param lockCookie the value of the lockCooking which is checked for permission
	 * @throws RecordNotFoundException if the record number is not found or if the record
	 * is set deleted.
	 */
	@Override
	public void deleteRecord(long recNo, long lockCookie) // NO_UCD (test only)
			throws RecordNotFoundException, SecurityException {
		boolean found = false;
		for (DBRecord rec : dbContents.keySet()) {			
			if (rec.getKey() == recNo && !rec.isDeleted()) {
				found = true;
				if (dbContents.get(rec) == lockCookie) {
					rec.setDeleted(true);
					dbContents.put(rec, 0L); // reset the locking cookie
					syncWithFile();
				} else {
					throw new SecurityException("Locking cookie " + lockCookie + " for record no. " + recNo + " not valid.");
				}
			}
		}
		if (!found) {
			throw new RecordNotFoundException("Record no. " + recNo + " can not be deleted!");
		}

	}


	/**
	 * Creates a record containing the data as provided by the param-array. The records
	 * initial state is  "not deleted".
	 * 
	 * @param data <code>String[]</code> representation of the data for the new 
	 * {@link DBRecord}
	 * @return the key number of the new record
	 * @throws DuplicateKeyException if the new record is already contained in the cache (almost impossible as
	 * each new record gets a unique id which is never reused
	 */
	@Override
	public long createRecord(String[] data) throws DuplicateKeyException { // NO_UCD (test only)
		DBRecord rec = new DBRecord(data.length, false);
		String[] names = schemaDescription.keySet().toArray(new String[0]);
		for (int i = 0; i < data.length; i++) {
			rec.addEntry(names[i], data[i]);
		}
		if (dbContents.containsKey(rec)) { 
			throw new DuplicateKeyException("Record No. " + rec.getKey() + " already contained in cache.");
		} else {
			dbContents.put(rec, 0L);
			syncWithFile();
			return rec.getKey();
		}
	}

	/**
	 * Locks the record with the given number if possible. If there is not a valid record
	 * for the given number {@link RecordNotFoundException} is thrown.
	 * 
	 * @param recNo the record number to be locked
	 * @return locking cookie as a long value, 0 if the record is already locked
	 * @throws RecordNotFoundException if the record number does not match any record.
	 */
	@Override
	public long lockRecord(long recNo) throws RecordNotFoundException {
		boolean found = false;
		long lockingCookie = 0L;
		for (DBRecord rec : dbContents.keySet() ) {
			if (rec.getKey() == recNo && !rec.isDeleted()) {
				found = true;
				if (dbContents.get(rec) == 0) {
					lockingCookie = System.currentTimeMillis()+recNo;
					dbContents.put(rec, lockingCookie);
				}
			}
		}
		if (!found) {
			throw new RecordNotFoundException("Record no. " + recNo + " not found! - No locking possible!");
		}
		return lockingCookie;
	}

	/**
	 * Tries to unlock the record with the given number with the given locking cookie.
	 * If the value of the cookie does not match a {@link SecurityException} is thrown.
	 * In case the record number is not contained in the record lists the method 
	 * returns without any change.
	 * 
	 * @param recNo the number of the record to be unlocked
	 * @param cookie the value of the locking cookie
	 * @throws SecurityException if the locking cookie which has been passed is either
	 * <code>0</code> or does not match the cookie saved in the record
	 */
	@Override
	public void unlock(long recNo, long cookie) throws SecurityException {
		for (DBRecord rec : dbContents.keySet()) {
			if (rec.getKey() == recNo && !rec.isDeleted()) {
				if (cookie != 0 && dbContents.get(rec) == cookie) {
					dbContents.put(rec, 0L); // reset the cookie!
				} else {
					throw new SecurityException("Locking cookie " + cookie + " for record no. " + recNo + " not valid.");
				}
			}
		}
	}

	/**
	 * Getter for the schema description. As stated in the instructions this
	 * description consists of <code>[fieldname,length]</code> pairs.
	 * 
	 * @return the schemaDescription as a {@link LinkedHashMap}
	 */
	public LinkedHashMap<String, Short> getSchemaDescription() {
		return schemaDescription;
	}

	/**
	 * To get the actual number of records in the cache (hopefully equal to the
	 * number of records in the db-file).
	 * 
	 * @return size of the internal record-cache
	 */
	public int getCacheSize() {
		return dbContents.size();
	}

	/**
	 * Getter for all stored records in the cache not including their possible 
	 * locking cookie values.
	 * 
	 * @return a set of {@link DBRecord} objects extracted from the private cache
	 */
	public Set<DBRecord> getCache() {
		return dbContents.keySet();
	}


}
