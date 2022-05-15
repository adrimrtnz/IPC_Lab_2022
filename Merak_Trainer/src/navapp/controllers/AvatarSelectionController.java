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
import navapp.Avatar;
import navapp.AvatarListCell;

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
    @FXML private ListView<Avatar> avatarListView;
    
    private double xOffset, yOffset;
    private ObservableList<Avatar> defaultAvatars = null;
    private ObservableList<Avatar> datos = null;
    
    private Avatar selectedImage;
    

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
        
        ArrayList<Avatar> avatares = new ArrayList<Avatar>();
        
        avatares.add(new Avatar(new Image("/resources/avatars/default.png"), "default"));
        avatares.add(new Avatar(new Image("/resources/avatars/avatar1.png"), "avatar1"));
        avatares.add(new Avatar(new Image("/resources/avatars/avatar2.png"), "avatar2"));
        avatares.add(new Avatar(new Image("/resources/avatars/avatar3.png"), "avatar3"));
        avatares.add(new Avatar(new Image("/resources/avatars/avatar4.png"), "avatar4"));
        
        datos = FXCollections.observableArrayList(avatares);
        avatarListView.setItems(datos); // vinculación entre la vista y el modelo
        

        avatarListView.setCellFactory(c -> new AvatarListCell());

        
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
                datos.add(new Avatar(new Image(file.toURI().toString()),file.getName()));
            }
        });
        
        
    }    
    
    public Image getImage() throws Exception{
        return selectedImage.getImage();
    }
            
    
}
