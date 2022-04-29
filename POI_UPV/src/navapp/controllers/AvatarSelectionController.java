/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class AvatarSelectionController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private Button closeBtn;
    @FXML private Button maximizeButton;
    @FXML private Button minimizeBtn;
    @FXML private Button acceptBtn;
    @FXML private Button cancelBtn;
    @FXML private Button searchBtn;
    @FXML private ListView<Image> avatarListView;
    
    private double xOffset, yOffset;
    private ObservableList<Image> defaultAvatars = null;
    private ObservableList<Image> datos = null;
    
    private Image selectedImage;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        closeBtn.setOnAction((ActionEvent event) -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        cancelBtn.setOnAction((ActionEvent event) -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        acceptBtn.setOnAction((ActionEvent event) -> {
            selectedImage = avatarListView.getSelectionModel().getSelectedItem();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        minimizeBtn.setOnAction((ActionEvent event) -> {
            ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true);
        });
        
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) toolBar.getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        
        });
        
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) toolBar.getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        
        });
        
        ArrayList<Image> avatares = new ArrayList<Image>();
        
        avatares.add(new Image("/resources/avatars/default.png"));
        avatares.add(new Image("/resources/avatars/avatar1.png"));
        avatares.add(new Image("/resources/avatars/avatar2.png"));
        avatares.add(new Image("/resources/avatars/avatar3.png"));
        avatares.add(new Image("/resources/avatars/avatar4.png"));
        
        datos = FXCollections.observableArrayList(avatares);
        avatarListView.setItems(datos); // vinculación entre la vista y el modelo
        
        /*
        vistadeListafxID.setCellFactory(c -> new ListCell<String>() {
           private ImageView imageView = new ImageView();
           
           @Override
           public void updateItems(String name, boolean empty) {
               super.updateItem(name, empty);
           }
            
        });*/
        defaultAvatars = avatarListView.getItems();
        
        searchBtn.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) searchBtn.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"));
            File file = fileChooser.showOpenDialog(stage);
        
            if(file == null) {
                System.out.println("Null Image File");
            } else {
                datos.add(new Image(file.toURI().toString()));
            }
        });
        
        
    }    
    
    public Image getImage() {
        return selectedImage;
    }
            
    
}
