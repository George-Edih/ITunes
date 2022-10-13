package cs1302.gallery;

import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.util.Duration;
import java.time.LocalTime;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    Stage stage;
    Scene scene;
    VBox vbox =  new VBox();

    String sUrl;
    URL url;
    InputStreamReader reader;
    String topic = "rock";
    int count;

    MenuBar menu;
    Menu file;
    MenuItem exit;



    ToolBar tool;
    Button plause;
    Text search;
    TextField tf;
    Button update;

    TilePane tile;
    GalleryLoader[][] img;
    HBox progress;
    ProgressBar bar;
    Text courtesy;
    Timeline timeline;


    /**
     * initializes some members of the class.
     */
    @Override
    public void init() {
        topic = "rock";
        menu = new MenuBar();
        file = new Menu("File");
        plause = new Button("play");
        exit = new MenuItem("exit");
        tool = new ToolBar();
        search = new Text("Search Query");
        tf = new TextField("rock");
        update = new Button("Update Images");
        tile = new TilePane();
        bar = new ProgressBar();
        timeline = new Timeline();
        img = new GalleryLoader[4][5];
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length;j++) {
                img[i][j] = new GalleryLoader();
            }
        } //intializing grid of images
        tile.setPrefColumns(5);
        tile.setPrefRows(4);
        for (int k = 0; k < img.length; k++) {
            for ( int l = 0; l < img[0].length; l++) {
                tile.getChildren().add(img[k][l]);
            }
        } // setting rows and columns for tilepane and adding images to each spot
        progress = new HBox(3);
        courtesy = new Text("Images provided courtesy of iTunes");
        file.getItems().add(exit);
        menu.getMenus().add(file);
        tool.getItems().addAll(plause,search,tf,update);
        progress.getChildren().addAll(bar,courtesy);
        vbox.getChildren().addAll(menu,tool,tile,progress);

    }

    /**
     * Starts the gallary app.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        update.setOnAction(event -> {
            runNow(() ->  {
                topic = tf.getText();
                updateImg(topic);
            });
        });
        exit.setOnAction(e -> System.exit(0));
        updateImg(topic);
        EventHandler<ActionEvent> handler = event -> change();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        EventHandler<ActionEvent> changes = event -> {
            if (plause.getText().equals("play")) {
                plause.setText("pause");
                timeline.pause();
            } else if (plause.getText().equals("pause")) {
                plause.setText("play");
                timeline.play();
            }
        };
        plause.setOnAction(changes);
        scene = new Scene(vbox);
        stage.setMaxWidth(1280);
        stage.setMaxHeight(720);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    } // Start


    /**
     * starts a separate thread to run alongside java fx thread.
     *@param target a runnable parameter that gets inserted in other thread.
     */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    }

    /**
     *Updates all the grids with images from the search query.
     *@param s which is the text from the search query.
     */
    public void updateImg(String s) {
        try {
            sUrl = URLEncoder.encode(s,"UTF-8");
            url = new URL("https://itunes.apple.com/search?term=" + sUrl);
            reader = new InputStreamReader(url.openStream());
            JsonParser p = new JsonParser();
            JsonElement je = p.parse(reader);
            JsonObject root = je.getAsJsonObject(); // root of response
            JsonArray results = root.getAsJsonArray("results"); // "results" array
            int numResults = results.size();// "results" array size
            List<String> list = new ArrayList<String>();
            for (int z = 0; z < numResults; z++)  {
                JsonObject result = results.get(z).getAsJsonObject();
                JsonElement artworkUrl100 = result.get("artworkUrl100"); // artw \ orkUrl100 member
                if (artworkUrl100 != null) {
                    String art = artworkUrl100.getAsString();
                    list.add(art);
                }
            }
            List<String> dis = list.stream().distinct().collect(Collectors.toList());
            if (dis.size() >= 21) {
                int count = 0;
                int i = 0;
                int a = 0;
                int b = 0;
                setProgress(0.0);
                while (count != 20) {
                    for (int z = 0; z < count; z++) {
                        setProgress(1.0 * z / count);
                    }
                    img[a][b].loadImage(dis.get(count));
                    b++;
                    if (b == 5) {
                        a++;
                        b = 0;
                    }
                    count++;
                }
                setProgress(1.0);
            } else {
                System.out.println("Results of query search less than 21");
            }
        } catch (UnsupportedEncodingException u) {
            System.out.println("Named encoding is not supported");
        } catch (MalformedURLException m) {
            System.out.println("Unknown protocol is specified");
        } catch (IOException i ) {
            System.out.println("IOException caught");
        } catch (NullPointerException npe) {
            System.out.println( "Query search is null");
        }
    }

     /**
      *Updates all the grids with images from the search query.
      */
    public void change() {
        try {
            Random rand = new Random();
            sUrl = URLEncoder.encode(topic,"UTF-8");
            url = new URL("https://itunes.apple.com/search?term=" + sUrl);
            reader = new InputStreamReader(url.openStream());
            JsonParser p = new JsonParser();
            JsonElement je = p.parse(reader);
            JsonObject root = je.getAsJsonObject(); // root of response
            JsonArray results = root.getAsJsonArray("results"); // "results" array
            int i = rand.nextInt(3);
            int j = rand.nextInt(4);
            int ran = 21 + rand.nextInt((results.size() - 22));
            JsonObject result = results.get(ran).getAsJsonObject();
            JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
            if (artworkUrl100 != null) {
                String art = artworkUrl100.getAsString();
                img[i][j].loadImage(art);
            }
        } catch (UnsupportedEncodingException u) {
            System.out.println("Named encoding is not supported");
        } catch (MalformedURLException m) {
            System.out.println("Unknown protocol is specified");

        } catch (IOException i ) {
            System.out.println("IOException caught");
        } catch (NullPointerException npe) {
            System.out.println( "Query search is null");
        }
    }

    /**
     * Sets the progress bar for the pictures.
     *@param progress is a percentage of the prorgress bar.
     */
    private void setProgress(final double progress) {
        Platform.runLater(() -> bar.setProgress(progress));
    } // setProgress


} // GalleryApp
