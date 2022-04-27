/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.crypto.SealedObject;
import model.Navegacion;
import model.User;
import model.Problem;
import model.Answer;
import model.Session;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class ExercisesScreenController implements Initializable {
    
    @FXML private ToggleGroup tools;
    @FXML private ToggleGroup opciones;
    @FXML private Spinner<Integer> strokeSize;
    @FXML private Spinner<Integer> textSize;
    @FXML private ImageView mapImg;
    @FXML private ToggleButton dragBtn;
    @FXML private ToggleButton drawLineBtn;
    @FXML private ToggleButton drawPointBtn;
    @FXML private ToggleButton drawArcBtn;
    @FXML private ToggleButton addTextBtn;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider zoomSlider;
    @FXML private ScrollPane mapScrollpane;
    @FXML private MenuItem newRandBtn;
    @FXML private Text probStatement;
    @FXML private Button submitAnsBtn;
    @FXML private Button clearBtn;
    @FXML private Button nextStatementBtn;
    @FXML private MenuItem closeSessionBtn;
    @FXML private MenuBar menuBar;
    @FXML private Pane mapPane;
    
    private User loggedUser;
    private BooleanProperty dragActive;
    
    private double initialXTrans;
    private double initialYTrans;
    private double initialXArc;
    private double baseX;
    private double baseY;
    private Group contentGroup;
    private Group zoomGroup;
    
    private Line linePainting; 
    private Circle circlePainting;
    
    private Navegacion baseDatos;
    private List<Problem> probDisp;
    private Problem activeProblem;
    
    private int hits, fails;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dragActive = new SimpleBooleanProperty();
        dragActive.bind(dragBtn.selectedProperty());
        
        strokeSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        textSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 20));
        
        // inicializamos el slider y enlazamos con el zoom
        zoomSlider.setMin(0.5);
        zoomSlider.setMax(3.5);
        zoomSlider.setValue(1.0);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(mapScrollpane.getContent());
        mapScrollpane.setContent(contentGroup);
        
        /*
        Stage stage = (Stage) menuBar.getScene().getWindow();
        
        stage.setOnCloseRequest(ev -> {
            closeSession();
        });*/
        
        try{
            baseDatos = Navegacion.getSingletonNavegacion();
            probDisp = baseDatos.getProblems();
        }
        catch (Exception e) {
            System.out.print("Error al cargar la base de datos: ");
            System.out.println(e.toString());
        }
        
        
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
        
        // TODO: Refactorizar lo que hace cada botón
        
        
        if (!event.isPrimaryButtonDown()) { 
            // Si no está apretado el botón derecho del ratón no hace nada
            // Así no dibuja cuando intentas eliminar un Node
            return; 
        } 
        
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
            mapPane.getChildren().add(linePainting);

            linePainting.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Eliminar");
                MenuItem applyColorItem = new MenuItem("Aplicar nuevo color");

                menuContext.getItems().add(deleteItem);
                menuContext.getItems().add(applyColorItem);
                
                deleteItem.setOnAction(ev -> {
                    mapPane.getChildren().remove((Node) e.getSource());
                    ev.consume();
                });
                
                applyColorItem.setOnAction(ev -> {
                    ((Line) e.getSource()).setStroke(colorPicker.getValue());
                    ev.consume();
                });
                
                menuContext.show(linePainting, e.getSceneX(), e.getSceneY());
                e.consume();
            });
        }
        
        if (drawPointBtn.selectedProperty().get()) {
            Circle circle = new Circle(event.getX(), event.getY(), strokeSize.getValue(), colorPicker.getValue());
            mapPane.getChildren().add(circle);
            
            circle.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Eliminar");
                MenuItem applyColorItem = new MenuItem("Aplicar nuevo color");

                menuContext.getItems().add(deleteItem);
                menuContext.getItems().add(applyColorItem);
                
                deleteItem.setOnAction(ev -> {
                    mapPane.getChildren().remove((Node) e.getSource());
                    ev.consume();
                });
                
                applyColorItem.setOnAction(ev -> {
                    ((Circle) e.getSource()).setStroke(colorPicker.getValue());
                    ev.consume();
                });
                
                
                menuContext.show(circle, e.getSceneX(), e.getSceneY());
                e.consume();
            });
        }
        
        if (drawArcBtn.selectedProperty().get()) {
            circlePainting = new Circle(1);
            circlePainting.setStroke(colorPicker.getValue());
            circlePainting.setStrokeWidth(strokeSize.getValue());
            circlePainting.setFill(Color.TRANSPARENT);
            circlePainting.setCenterX(event.getX());
            circlePainting.setCenterY(event.getY());
            initialXArc = event.getX();
            //zoomGroup.getChildren().add(circlePainting);
            mapPane.getChildren().add(circlePainting);
            
            
            circlePainting.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Eliminar");
                MenuItem applyColorItem = new MenuItem("Aplicar nuevo color");

                menuContext.getItems().add(deleteItem);
                menuContext.getItems().add(applyColorItem);
                
                deleteItem.setOnAction(ev -> {
                    mapPane.getChildren().remove((Node) e.getSource());
                    ev.consume();
                });
                
                applyColorItem.setOnAction(ev -> {
                    ((Circle) e.getSource()).setStroke(colorPicker.getValue());
                    ev.consume();
                });
                
                menuContext.show(circlePainting, e.getSceneX(), e.getSceneY());
                e.consume();
            });
        }        
        
        if (addTextBtn.selectedProperty().get()) {
            TextField textF = new TextField();
            mapPane.getChildren().add(textF);
            textF.setLayoutX(event.getX());
            textF.setLayoutY(event.getY());
            textF.requestFocus();
            
            textF.setOnAction(e -> {
                Text textT = new Text (textF.getText());
                textT.setLayoutX(textF.getLayoutX());
                textT.setLayoutY(textF.getLayoutY());
                textT.setFont(Font.font("Gafata", textSize.getValue()));
                textT.setFill(colorPicker.getValue());
                mapPane.getChildren().add(textT);
                mapPane.getChildren().remove(textF);
                
                textT.setOnContextMenuRequested(ev -> {
                    ContextMenu menuContext = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Eliminar");
                    menuContext.getItems().add(deleteItem);
                    deleteItem.setOnAction(eve -> {
                        mapPane.getChildren().remove((Node) ev.getSource());
                        eve.consume();
                    });
                    menuContext.show(textT, ev.getSceneX(), ev.getSceneY());
                    ev.consume();
                });
                                
                e.consume();
            });
            
        }
        
    }
    
    @FXML private void modAction(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) { 
            // Si no está apretado el botón derecho del ratón no hace nada
            // Así no dibuja cuando intentas eliminar un Node
            return; 
        } 
        
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
        
        if (drawArcBtn.selectedProperty().get()) {
            double radio = Math.abs(event.getX() - initialXArc);
            circlePainting.setRadius(radio);
        }   
        
        event.consume();
    } 

    @FXML private void cleanMap(ActionEvent event) {

        while (mapPane.getChildren().size() > 1) {
            mapPane.getChildren().remove(mapPane.getChildren().size() - 1);
        }
        event.consume();
    }

    @FXML
    private void nextExercise(ActionEvent event) {
        if(activeProblem == null || probDisp.indexOf(activeProblem) + 1 == probDisp.size()) {
            activeProblem = probDisp.get(0);
        } else {
            activeProblem = probDisp.get(probDisp.indexOf(activeProblem) + 1);
        }
        updateProblem();
    }
    
    @FXML
    private void newRandomExercise(ActionEvent event) {
        Random rd = new Random();
        
        activeProblem = probDisp.get(rd.nextInt(probDisp.size()));
        updateProblem();
    }
    
    private void updateProblem() {
        probStatement.textProperty().set(activeProblem.getText());
        List<Answer> ansListTemp = activeProblem.getAnswers();
        List<Integer> order = new LinkedList<Integer>();
        Random rd = new Random();
        /*No es la forma más eficiente para aleatorizar las respuestas
        pero para generar 4 valores solamente no es crítico y es simple*/
        while(order.size() < 4) {
            Integer n = rd.nextInt(4);
            if(!order.contains(n)){
                order.add(n);
            }
        }
        
        for(Toggle t: opciones.getToggles()){
            Answer ans = ansListTemp.get(order.remove(0));
            ((RadioButton)t).textProperty().set(ans.getText());
            ((RadioButton)t).setUserData(ans.getValidity());
        }
        
        clearAnswers();
    }

    @FXML
    private void checkAnswer(ActionEvent event) {
        if(opciones.getSelectedToggle() != null) {
            for(Toggle t: opciones.getToggles()){
                if(((RadioButton)t).isSelected() && (boolean)((RadioButton)t).getUserData()){
                    hits++;
                    ((RadioButton)t).textFillProperty().set(Color.GREEN);
                }
                else if (((RadioButton)t).isSelected() && !(boolean)((RadioButton)t).getUserData()) {
                    fails++;
                    ((RadioButton)t).textFillProperty().set(Color.RED);
                }
                else if (!((RadioButton)t).isSelected() && (boolean)((RadioButton)t).getUserData()) {
                    ((RadioButton)t).textFillProperty().set(Color.GREEN);
                }
            }
        } else {
            System.out.println("Error al evaluar respuesta");
        }
        
        submitAnsBtn.setDisable(true);
    }
    
    private void clearAnswers() {
        for(Toggle t: opciones.getToggles()){
            ((RadioButton)t).textFillProperty().setValue(Color.BLACK);
            ((RadioButton)t).setSelected(false);
        }
        
        submitAnsBtn.setDisable(false);
    }

    private Session generateSessionInfo() {
        LocalDateTime lt = LocalDateTime.now();
        return new Session(lt,hits,fails);
    }
    
    @FXML
    private void closeSession(ActionEvent event) throws Exception {
        FXMLLoader confirmationWindow = new FXMLLoader(getClass().getResource("/navapp/views/ConfirmationScreenView.fxml"));
        Parent root = confirmationWindow.load();
        
        ConfirmationScreenController controlador = confirmationWindow.getController();
        
        Stage stage = new Stage();
        stage.setTitle("Registro Nuevo Usuario");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        if (controlador.isClosing()) {
        
            Parent rootIni = FXMLLoader.load(getClass().getResource("/navapp/views/LoginScreenView.fxml"));

            loggedUser.addSession(generateSessionInfo());

            Stage stageIni = new Stage();
            stageIni.setTitle("NavApp - IPCLab 2022");
            stageIni.initStyle(StageStyle.UNDECORATED);
            Scene sceneIni = new Scene(rootIni);
            sceneIni.setFill(Color.TRANSPARENT);
            stageIni.initStyle(StageStyle.TRANSPARENT);
            stageIni.setResizable(false);
            stageIni.setScene(sceneIni);
            closeExercisesScreen();
            stageIni.show();
        }
        
    }
    
    private void closeExercisesScreen() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }
}
