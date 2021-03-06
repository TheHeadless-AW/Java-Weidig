package day4.p5.events.mouseevents;

/*
 * Copyright (c) 2008, 2016, Oracle and/or its affiliates. All rights reserved. Use is subject to
 * license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of Oracle Corporation nor the names
 * of its contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * A sample that demonstrates various mouse and scroll events and their usage. Click the circles and
 * drag them across the screen. Scroll the whole screen. All events are logged to the console.
 */
public class MouseEventApp extends Application {

  private final Dimension2D rectSize = new Dimension2D( 400.0, 200.0 );
  private final Dimension2D consoleSize = new Dimension2D( 400.0, 200.0 );
  private final Point2D smallStart = new Point2D( 50.0, 50.0 );
  private final Point2D bigStart = new Point2D( 180.0, 50.0 );
  // variables for storing initial position before drag of circle
  private double initX;
  private double initY;
  private Point2D dragAnchor;
  // create a observableArrayList of logged events listed in the console
  final ObservableList< String > consoleObservableList = FXCollections.observableArrayList();

  @Override
  public void start( Stage primaryStage ) throws Exception {
    primaryStage.setScene( new Scene( createContent() ) );
    primaryStage.show();
  }

  public static void main( String[] args ) {
    launch( args );
  }

  private Parent createContent() {
    // 1.) Rectangle
    Rectangle rect = new Rectangle( 400, 200 );
    rect.setStroke( Color.BLACK );
    rect.setFill( Color.BISQUE );

    // 2.) ListView (=Console)
    final ListView< String > console = new ListView<>();
    // set up the console
    console.setItems( consoleObservableList );
    console.setLayoutY( rectSize.getHeight() + 5 );
    console.setPrefSize( consoleSize.getWidth(), consoleSize.getHeight() );

    // 3.) Create circles
    // paramethers: name of the circle, color of the circle, radius
    final Circle circleSmall = createCircle( "Blue", Color.DODGERBLUE, 25 );
    circleSmall.setTranslateX( smallStart.getX() );
    circleSmall.setTranslateY( smallStart.getY() );

    final Circle circleBig = createCircle( "Brown", Color.BROWN, 30 );
    circleBig.setTranslateX( bigStart.getX() );
    circleBig.setTranslateY( bigStart.getY() );

    // 4.) MouseMovedEvent on the rectangle
    // rect.setOnMouseMoved((MouseEvent me) -> {
    // // log mouse move to console, method listed below
    // showOnConsole("Mouse moved, x: " + me.getX() + ", y: " + me.getY());
    // });

    // 5.) ScrollEvent in the rectangle
    rect.setOnScroll( ( ScrollEvent event ) -> {
      double translateX = event.getDeltaX();
      double translateY = event.getDeltaY();
      // reduce the deltas for the circles to stay in the screen
      for ( Circle c : new Circle[]{ circleSmall, circleBig } ) {
        if ( c.getTranslateX() + translateX + c.getRadius() > rectSize.getWidth() ) {
          translateX = rectSize.getWidth() - c.getTranslateX() - c.getRadius();
        }
        if ( c.getTranslateX() + translateX - c.getRadius() < 0 ) {
          translateX = -c.getTranslateX() + c.getRadius();
        }
        if ( c.getTranslateY() + translateY + c.getRadius() > rectSize.getHeight() ) {
          translateY = rectSize.getHeight() - c.getTranslateY() - c.getRadius();
        }
        if ( c.getTranslateY() + translateY - c.getRadius() < 0 ) {
          translateY = -c.getTranslateY() + c.getRadius();
        }
      }
      // move the circles
      for ( Circle c : new Circle[]{ circleSmall, circleBig } ) {
        c.setTranslateX( c.getTranslateX() + translateX );
        c.setTranslateY( c.getTranslateY() + translateY );
      }
      // log event
      showOnConsole( "Scrolled, deltaX: " + event.getDeltaX() + ", deltaY: " + event.getDeltaY() );
    } );

    return new Group( rect, circleBig, circleSmall, console );
  }

  /**
   * Helper method to create a circle which reacts to various mouse events
   * 
   * @param name
   * @param color
   * @param radius
   * @return
   */
  private Circle createCircle( final String name, final Color color, int radius ) {
    final Stop[] stops = new Stop[]{ new Stop( 0, Color.rgb( 250, 250, 255 ) ), new Stop( 1, color ) };
    final Circle circle = new Circle( radius, new RadialGradient( 0, 0, 0.2, 0.3, 1, true, CycleMethod.NO_CYCLE, stops ) );

    circle.setEffect( new Reflection() );
    // circle.setEffect(new InnerShadow(7, color.darker().darker()));

    // change a cursor when it is over circle
    circle.setCursor( Cursor.HAND );

    // 1.) Event for clicking a circle
    circle.setOnMouseClicked( ( MouseEvent me ) -> {
      final int count = me.getClickCount();
      final String message = String.format( "Clicked on %s %d %s", name, count, ( count > 1 ? "times" : "time" ) );
      showOnConsole( message );
      // the event will be passed only to the circle which is on front
      // me.consume();
    } );

    circle.setOnMousePressed( ( MouseEvent me ) -> {
      // when mouse is pressed, store initial position
      initX = circle.getTranslateX();
      initY = circle.getTranslateY();
      dragAnchor = new Point2D( me.getSceneX(), me.getSceneY() );
      showOnConsole( "Mouse pressed above " + name );
    } );

    circle.setOnMouseDragged( ( MouseEvent me ) -> {
      double dragX = me.getSceneX() - dragAnchor.getX();
      double dragY = me.getSceneY() - dragAnchor.getY();
      // calculate new position of the circle
      double newXPosition = initX + dragX;
      double newYPosition = initY + dragY;
      // if new position do not exceeds borders of the rectangle,
      // translate to this position
      if ( ( newXPosition >= circle.getRadius() ) && ( newXPosition <= rectSize.getWidth() - circle.getRadius() ) ) {
        circle.setTranslateX( newXPosition );
      }
      if ( ( newYPosition >= circle.getRadius() ) && ( newYPosition <= rectSize.getHeight() - circle.getRadius() ) ) {
        circle.setTranslateY( newYPosition );
      }
      showOnConsole( name + " dragged (x:" + dragX + ", y:" + dragY + ")" );
    } );
    circle.setOnMouseEntered( ( MouseEvent me ) -> {
      // change the z-coordinate of the circle
      circle.toFront();
      showOnConsole( "Mouse entered " + name );
    } );
    circle.setOnMouseExited( ( MouseEvent me ) -> {
      showOnConsole( "Mouse exited " + name );
    } );
    circle.setOnMouseReleased( ( MouseEvent me ) -> {
      showOnConsole( "Mouse released above " + name );
    } );

    return circle;
  }

  /**
   * This method adds a string to the end of the observableList of the console (to get it
   * displayed). The list may not contain more than eight elements.
   * 
   * @param text the text to be added
   */
  private void showOnConsole( String text ) {
    // if there is 8 items in list, delete first log message,
    // shift other logs and add a new one to end position
    if ( consoleObservableList.size() == 8 ) {
      consoleObservableList.remove( 0 );
    }
    consoleObservableList.add( text );
  }

}
