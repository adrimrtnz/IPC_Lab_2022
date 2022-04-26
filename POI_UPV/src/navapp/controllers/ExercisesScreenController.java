/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.User;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class ExercisesScreenController implements Initializable {
    
    @FXML private ToggleGroup tools;
    @FXML private ToggleGroup opciones;
    @FXML private Spinner<Integer> strokeSize;
    @FXML private ImageView mapImg;
    @FXML private ToggleButton dragBtn;
    @FXML private ToggleButton drawLineBtn;
    @FXML private ToggleButton drawPointBtn;
    @FXML private ToggleButton drawArcBtn;
    @FXML private ToggleButton addTextBtn;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider zoomSlider;
    @FXML private ScrollPane mapScrollpane;
    
    private User loggedUser;
    private BooleanProperty dragActive;
    
    private double initialXTrans;
    private double initialYTrans;
    private double baseX;
    private double baseY;
    private Group contentGroup;
    private Group zoomGroup;
    
    private Line linePainting;  
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dragActive = new SimpleBooleanProperty();
        dragActive.bind(dragBtn.selectedProperty());
        
        strokeSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        
        // inicializamos el slider y enlazamos con el zoom
        zoomSlider.setMin(0.5);
        zoomSlider.setMax(1.5);
        zoomSlider.setValue(1.0);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(mapScrollpane.getContent());
        mapScrollpane.setContent(contentGroup);
    }


    public void initializeUser(User user) {
        loggedUser = user;
        System.out.println("Usuario: " + loggedUser.getNickName());
    }
    
    
    private void zoom(double scaleValue) {
        //===================================================
        //guardamos los valores del scroll antes del escalado
        double scrollH = mapScrollpane.getHvalue();
        double scrollV = mapScrollpane.getVvalue();
        //===================================================
        // escalamos el zoomGroup en X e Y con el valor de entrada
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        //===================================================
        // recuperamos el valor del scroll antes del escalado
        mapScrollpane.setHvalue(scrollH);
        mapScrollpane.setVvalue(scrollV);
    }
    
    @FXML void zoomIn(ActionEvent event) {
        //================================================
        // el incremento del zoom dependerá de los parametros del 
        // slider y del resultado esperado
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal += 0.1);
    }

    @FXML void zoomOut(ActionEvent event) {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + -0.1);
    }

    
    @FXML private void manageAction(MouseEvent event) {
        if (dragActive.get()) {
            initialXTrans = event.getSceneX();
            initialYTrans = event.getSceneY();
            baseX = contentGroup.getTranslateX();
            baseY = contentGroup.getTranslateY();
        }
        
        if (drawLineBtn.selectedProperty().get()) {
            linePainting = new Line(event.getX(), event.getY(), event.getX(), event.getY());
            linePainting.setStrokeWidth(strokeSize.getValue());
            linePainting.setStroke(colorPicker.getValue());
            zoomGroup.getChildren().add(linePainting);

            linePainting.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Eliminar");

                menuContext.getItems().add(deleteItem);
                deleteItem.setOnAction(ev -> {
                    zoomGroup.getChildren().remove((Node) e.getSource());
                    ev.consume();
                });
                menuContext.show(linePainting, e.getSceneX(), e.getSceneY());
                e.consume();
            });
        }
        
        if (drawPointBtn.selectedProperty().get()) {
            Circle circle = new Circle(event.getX(), event.getY(), strokeSize.getValue(), colorPicker.getValue());
            zoomGroup.getChildren().add(circle);
            
            circle.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Eliminar");

                menuContext.getItems().add(deleteItem);
                deleteItem.setOnAction(ev -> {
                    zoomGroup.getChildren().remove((Node) e.getSource());
                    ev.consume();
                });
                menuContext.show(circle, e.getSceneX(), e.getSceneY());
                e.consume();
            });
        }
        
        if (drawArcBtn.selectedProperty().get()) {
            // TODO
        }        
        
        if (addTextBtn.selectedProperty().get()) {
            // TODO
        }
        
    }
    
    @FXML private void modAction(MouseEvent event) {
        if (dragActive.get()) {
            double despX = event.getSceneX() - initialXTrans;
            double despY = event.getSceneY() - initialYTrans;
            contentGroup.setTranslateX(baseX + despX);
            contentGroup.setTranslateY(baseY + despY);
        }
        
        if (drawLineBtn.selectedProperty().get() && linePainting != null) {
            linePainting.setEndX(event.getX());
            linePainting.setEndY(event.getY());
        }
        
        event.consume();
    } 

    @FXML private void cleanMap(ActionEvent event) {

        while (zoomGroup.getChildren().size() > 1) {
            zoomGroup.getChildren().remove(zoomGroup.getChildren().size() - 1);
        }
        event.consume();
    }
}
