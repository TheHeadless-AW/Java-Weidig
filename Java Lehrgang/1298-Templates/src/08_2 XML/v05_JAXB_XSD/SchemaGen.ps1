#die Pfad-Angaben zum Anpassen
#das JDK
$JAVA_PATH="C:\Program Files\Java\jdk1.7.0_55"
#der Pfad für schemagen.exe
$JAVA_BIN=$JAVA_PATH+"\bin"
#die Java-Tools
$JAVA_TOOLS=$JAVA_PATH+"\lib\tools.jar"

#Die Java-Dateien
$SOURCE="H:\eclipseProjekte\FUSIX\src"
$CLASS="Raum"
$PACKAGE="xml.v06_JAXB.v04_MitXSD"
$DESTINATION=$SOURCE+"\xml\v06_JAXB\v04_MitXSD"
$CLASSFILE=$PACKAGE+"."+$CLASS

Set-Location "H:\eclipseProjekte\FUSIX\src"
#Ausführung
&"C:\Program Files\Java\jdk1.7.0_55\bin\schemagen.exe" $CLASSFILE -cp $JAVA_TOOLS -d $DESTINATION
Rename-Item -Path ($DESTINATION+"\schema1.xsd") -NewName ($CLASS+".xsd")
Write-Host ("Datei " + $DESTINATION + "\schema1.xsd wurde in " + $CLASS+".xsd umbenannt.")
Read-Host "Beenden mit ENTER"