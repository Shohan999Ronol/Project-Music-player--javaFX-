package mp3_player_java;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private Label label;

    @FXML
    private Button nextbutton;

    @FXML
    private Button pausebutton;

    @FXML
    private Button playbutton;

    @FXML
    private Button previousbutton, file_chooser;

    @FXML
    private Button resetbutton;

    @FXML
    private ComboBox<String> speedbox;

    @FXML
    private Slider volumeslider;

    @FXML
    private Slider progress_slider;

    private ArrayList<File> songs;

    private int songNumber;
    private String[] speeds = { "0.25", "0.5", "0.5", "Normal", "1.25", "1.5", "1.75", "2" };

    private Timer timer;
    private TimerTask task;

    private boolean running;
    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<>();

        for (int i = 0; i < speeds.length; i++) {
            speedbox.getItems().add(speeds[i]);
        }

        speedbox.setOnAction(this::changeSpeed);

        volumeslider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeslider.getValue() * 0.01);
                volumeslider.getParent().requestFocus();
            }
        });

        progress_slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double progress = event.getX() / progress_slider.getWidth();
                double duration = media.getDuration().toSeconds();
                double time = progress * duration;
                mediaPlayer.seek(Duration.seconds(time));
                progress_slider.getParent().requestFocus();
            }
        });
        

    }

    @FXML
    void pause() {
        cancelTimer();
        mediaPlayer.pause();
        pausebutton.getParent().requestFocus();
    }

    @FXML
    void play() {
        if(songs.isEmpty()){
        openFileChooser();}
        else{
        beginTimer();
        mediaPlayer.setVolume(volumeslider.getValue() * 0.01);
        mediaPlayer.play();
        playbutton.getParent().requestFocus();
    }}

    @FXML
    void play_next() {
        if (songNumber < songs.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        cancelTimer();
        mediaPlayer.stop();
        playSelectedSong(songNumber);
        nextbutton.getParent().requestFocus();
    }

    @FXML
    void play_previous() {
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songs.size() - 1;
        }
        cancelTimer();
        mediaPlayer.stop();
        playSelectedSong(songNumber);
            
        previousbutton.getParent().requestFocus();
        
    }

    @FXML
    void reset_player() {
        progress_slider.setValue(0);
        mediaPlayer.seek(Duration.seconds(0));
        resetbutton.getParent().requestFocus();
        
    }

    @FXML
    public void changeSpeed(ActionEvent event) {
        if (speedbox.getValue() == null || (speedbox.getValue()=="Normal")) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Double.parseDouble(speedbox.getValue()));
        }
        speedbox.getParent().requestFocus();
    }

    public void beginTimer() {
        timer = new Timer();

        task = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                double progress = current / end;

                // Convert the progress value to the slider's range
                double sliderValue = progress * (progress_slider.getMax() - progress_slider.getMin())
                        + progress_slider.getMin();
                Platform.runLater(() -> progress_slider.setValue(sliderValue));

                if (current / end == 1) {
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);

        progress_slider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cancelTimer();
                mediaPlayer.seek(Duration.seconds(progress_slider.getValue()));
            }
        });

        progress_slider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progress_slider.getValue()));
            }
        });
    }
    
    
    @FXML
    public void skipForward() {
        Duration currentTime = mediaPlayer.getCurrentTime();
        Duration targetTime = currentTime.add(Duration.seconds(5));
        mediaPlayer.seek(targetTime);
        progress_slider.setValue(targetTime.toSeconds());
        progress_slider.getParent().requestFocus();
        
    }
    
    
    @FXML
    public void skipBackward() {
        Duration currentTime = mediaPlayer.getCurrentTime();
        Duration targetTime = currentTime.subtract(Duration.seconds(5));
        mediaPlayer.seek(targetTime);
        progress_slider.setValue(targetTime.toSeconds());
        progress_slider.getParent().requestFocus();
    }
    
    
    public void increaseVolume() {
        double currentVolume = volumeslider.getValue()*0.01;
        double newVolume = currentVolume + 0.01;
        if (newVolume <=1) {
            volumeslider.setValue(newVolume*100);
            mediaPlayer.setVolume(newVolume);
            volumeslider.getParent().requestFocus();

        }
        volumeslider.getParent().requestFocus();
    }

    public void decreaseVolume() {
        double currentVolume = volumeslider.getValue()*0.01;
        double newVolume = currentVolume - 0.01;
        if (newVolume >= 0) {
            volumeslider.setValue(newVolume*100);
            mediaPlayer.setVolume(newVolume);
            volumeslider.getParent().requestFocus();

        }
        volumeslider.getParent().requestFocus();
    }
    
    
    
    
    public void cancelTimer() {
        running = false;
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    @FXML
    void openFileChooser() {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Files");

        // Set filters for the allowed file extensions
        fileChooser.getExtensionFilters().add(new ExtensionFilter("MP3 Files", "*.mp3*", "*.m4a*"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));

        // Show the file chooser dialog
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());
           

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            
            if(!songs.isEmpty()){
                cancelTimer();
            mediaPlayer.stop();
                
            }   
            // Clear the existing songs list
            songs.clear();
            
       
            // Add the selected files to the songs list
            songs.addAll(selectedFiles);


            // Play the first selected song
            songNumber = 0;
            
            file_chooser.getParent().requestFocus();
            playSelectedSong(songNumber);
          
        }
        file_chooser.getParent().requestFocus();
    }

    private void playSelectedSong(int songNumber) {
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        
        //set label to song name
        label.setText(songs.get(songNumber).getName());
        mediaPlayer.setOnEndOfMedia(() -> {
            cancelTimer();
            mediaPlayer.stop();
            play_next();
        });
        
        play();
    }
}
