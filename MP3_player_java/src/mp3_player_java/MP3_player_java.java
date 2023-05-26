/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package mp3_player_java;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 *
 * @author Shohan
 */
public class MP3_player_java extends Application {
    private int i=0;
    
    @Override
    public void start(Stage stage) throws IOException {
        
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();	
        FXMLDocumentController controller = loader.getController();
        Scene scene = new Scene(root);
        
        stage.setTitle("Music Player by Ronol");
        Image image=new Image(getClass().getResourceAsStream("img/music.png"));
        stage.getIcons().add(image);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent event) {
                        
                        switch(event.getCode()) {    

                        case SPACE:
                            i++;
                            if(i==1){
                                controller.pause();
                                break;}
                            else if (i==2){
                                controller.play();
                                i=0;
                                break;}  
                            case N:
                                controller.play_next();
                                break;
                            case P:
                                controller.play_previous();
                                break;
                            case R:
                                controller.reset_player();
                                break;
                            case O:
                                controller.openFileChooser();
                                break;
                            case RIGHT:
                                controller.skipForward();
                                break;
                            case LEFT:
                                controller.skipBackward();
                                break;
                            case UP:
                                controller.increaseVolume();
                                break;
                            case DOWN:
                                controller.decreaseVolume();
                                break;
                                
                            default:
                                break;
                        }		
                }	
        });
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
