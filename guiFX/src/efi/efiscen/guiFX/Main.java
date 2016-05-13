/* 
 * Copyright (C) 2016 European Forest Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package efi.efiscen.guiFX;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class for GUI. The GUI is made with JavaFX.
 * when the Main method is ran it sets Locale and loads frame.fxml class and 
 * sets the size and style for the frame.
 * 
 */
public class Main extends Application {
    
    final ScrollPane sp = new ScrollPane();

    /**
     * Starts the GUI.
     * @param primaryStage Stage where GUI is set.
     */
    @Override
    public void start(Stage primaryStage) {
        Pane root = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        Locale locale = new Locale("EN","en");
        ResourceBundle bundle = ResourceBundle.getBundle("efi/"+
                                         "efiscen/localization/language", locale);
        //fxmlLoader.setResources(bundle);
        try {
            root = (Pane) fxmlLoader.load(this.getClass().getResource("frame.fxml"),bundle);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error happened when loading FXML");
            System.exit(0);
        }
        //VBox box = new VBox();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        //box.getChildren().addAll(root);
        /*VBox.setVgrow(sp, Priority.ALWAYS);
        sp.setContent(root);
        sp.setPrefSize(1136, 855);
        sp.setFitToWidth(true);*/
        scene.getStylesheets().add(FrameController.class.getResource("style.css").toExternalForm());
        //primaryStage.setMaxWidth(1136);
        primaryStage.setMinHeight(768);
        primaryStage.setMinWidth(1024);
        primaryStage.setResizable(true);
        primaryStage.setTitle("EFISCEN 4.1");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
