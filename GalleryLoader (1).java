package cs1302.gallery;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;



/**
 *GallaryLoader class extends VBox class and houses imgview objects.
 */
public class GalleryLoader extends VBox {


    HBox urlLayer;
    TextField urlField;
    // Button loadImage;

    ImageView imgView;
    protected  final String d =
        "http://cobweb.cs.uga.edu/~mec/cs1302/gui/default.png";



    /**
     *GallaryLoader constructor.
     */
    public GalleryLoader() {
        super();
        urlLayer = new HBox();
        // Load the default image with the default dimensions
        Image img = new Image(d,100,100, false, false);

        // Add the image to its container and preserve the aspect ratio if res
        imgView = new ImageView(img);
        imgView.setPreserveRatio(true);

        // Add the hbox and imageview to the containing vbox and set the vbox
        // to be the root of the scene
        this.getChildren().addAll(urlLayer,imgView);
    }

    /**
     * loads image to the image view.
     *@param s the url which gets inputted into the image.
     */
    protected void loadImage(String s) {
        try {
            Image newImg = new Image(s, 100,100, false, false);
            imgView.setImage(newImg);
        } catch (IllegalArgumentException iae) {
            System.out.println("The supplied URL is invalid");
        } // try
    } // loadImage

}
