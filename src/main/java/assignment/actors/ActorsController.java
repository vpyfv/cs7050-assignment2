package assignment.actors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ouda
 */
public class ActorsController implements Initializable {

    @FXML
    private MenuBar mainMenu;
    @FXML
    private ImageView image;
    @FXML
    private BorderPane ActorPortal;
    @FXML
    private Label title;
    @FXML
    private Label about;
    @FXML
    private Button play;
    @FXML
    private Button pause;
    @FXML
    private ComboBox generation;
    @FXML
    private TextField name;
    Media media;
    MediaPlayer player;
    OrderedDictionary database = null;
    ActorRecord actor = null;
    int actorGeneration = 1990;

    @FXML
    public void exit() {
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        stage.close();
    }

    public void find() {
        DataKey key = new DataKey(this.name.getText(), actorGeneration);
        try {
            List<ActorRecord> searchResults = database.contains(key);
            if(!searchResults.isEmpty()){
                actor = searchResults.get(0);
                showActor();
            }
            else{
                displayAlert("no record found");
            }

        } catch (DictionaryException ex) {
            displayAlert(ex.getMessage());
        }
    }

    public void delete() {
        ActorRecord previousActor = null;
        try {
            previousActor = database.predecessor(actor.getDataKey());
        } catch (DictionaryException ex) {

        }
        ActorRecord nextActor = null;
        try {
            nextActor = database.successor(actor.getDataKey());
        } catch (DictionaryException ex) {

        }
        DataKey key = actor.getDataKey();
        try {
            database.remove(key);
        } catch (DictionaryException ex) {
            System.out.println("Error in delete "+ ex);
        }
        if (database.root==null) {
            this.ActorPortal.setVisible(false);
            displayAlert("No more actors in the database to show");
        } else {
            if (previousActor != null) {
                actor = previousActor;
                showActor();
            } else if (nextActor != null) {
                actor = nextActor;
                showActor();
            }
        }
    }

    private void showActor() {
        play.setDisable(false);
        pause.setDisable(true);
        if (player != null) {
            player.stop();
        }
        String img = actor.getImage();
        Image actorImage = new Image("file:src/main/resources/assignment/actors/images/" + img);
        image.setImage(actorImage);
        title.setText(actor.getDataKey().getName());
        about.setText(actor.getAbout());
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/assignment/actors/images/UMIcon.png"));
            stage.setTitle("Dictionary Exception");
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    public void getGeneration() {
        switch (this.generation.getValue().toString()) {
            case "1990":
                this.actorGeneration = 1990;
                break;
            case "2000":
                this.actorGeneration = 2000;
                break;
            case "2010":
                this.actorGeneration = 2010;
                break;
            case "2015":
                this.actorGeneration = 2015;
                break;
            default:
                break;
        }
    }

    public void first() {
        try{
            actor = database.smallest();
            showActor();
        }
        catch (DictionaryException e){
            System.out.println(e.getMessage());
            displayAlert(e.getMessage());
        }
    }

    public void last() {
        try{
            actor = database.largest();
            showActor();
        }
        catch (DictionaryException e){
            System.out.println(e.getMessage());
            displayAlert(e.getMessage());
        }
    }

    public void next() {
        try{
            actor = database.successor(actor.getDataKey());
            showActor();
        }
        catch (DictionaryException e){
            System.out.println(e.getMessage());
            displayAlert(e.getMessage());
        }
    }

    public void previous() {
        try{
            actor = database.predecessor(actor.getDataKey());
            showActor();
        }
        catch (DictionaryException e){
            System.out.println(e.getMessage());
            displayAlert(e.getMessage());
        }
    }

    public void play() {
        String filename = "src/main/resources/assignment/actors/sounds/" + actor.getSound();
        media = new Media(new File(filename).toURI().toString());
        player = new MediaPlayer(media);
        play.setDisable(true);
        pause.setDisable(false);
        player.play();
    }

    public void puase() {
        play.setDisable(false);
        pause.setDisable(true);
        if (player != null) {
            player.stop();
        }
    }

    public void loadDictionary() {
        Scanner input;
        int line = 0;
        try {
            String name = "";
            String description;
            int generation = 0;
            input = new Scanner(new File("teluguActorsDatabase.txt"));
            while (input.hasNext()) // read until  end of file
            {
                String data = input.nextLine();
                switch (line % 3) {
                    case 0:
                        generation = Integer.parseInt(data);
                        break;
                    case 1:
                        name = data;
                        break;
                    default:
                        description = data;
                        database.insert(new ActorRecord(new DataKey(name, generation), description, name + ".mp3", name + ".jpg"));
                        break;
                }
                line++;
            }
            System.out.println(database.toString());

        } catch (IOException e) {
            System.out.println("There was an error in reading or opening the file: teluguActorsDatabase.txt");
            System.out.println(e.getMessage());
        } catch (DictionaryException ex) {
            Logger.getLogger(ActorsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ActorPortal.setVisible(true);
        this.first();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database = new OrderedDictionary();
        generation.setItems(FXCollections.observableArrayList(
                "1990", "2000", "2010","2015"
        ));
        generation.setValue("1990");


    }

}
