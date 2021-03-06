/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. DO NOT ALTER OR REMOVE
 * COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package day2.p3.animation.tree;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;

public class Util {

  public static void addChildToParent( final Group parent, final Node child ) {
    Platform.runLater( new Runnable() {

      @Override
      public void run() {
        parent.getChildren().add( child );
      }
    } );
  }
}
