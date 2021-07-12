package unterricht.hellojavafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloJavaFX extends Application {

  public static void main( String[] args ) {
    launch( args );
  }

  @Override
  public void start( Stage primaryStage ) throws Exception {
    primaryStage.setTitle( "Hello JavaFX" );
    primaryStage.show();

  }

}
