/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
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
import javafx.scene.shape.Shape;
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
    @FXML private ImageView transportImg;
    @FXML private ToggleButton dragBtn;
    @FXML private ToggleButton drawLineBtn;
    @FXML private ToggleButton drawPointBtn;
    @FXML private ToggleButton drawArcBtn;
    @FXML private ToggleButton addTextBtn;
    @FXML private ToggleButton marcarExtremBtn;
    @FXML private ToggleButton transportBtn;
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
    private double initialArc[];
    private double baseX;
    private double baseY;
    private Group contentGroup;
    private Group zoomGroup;
    
    private Line linePainting; 
    private Line markerLines[];
    private Circle circlePainting;
    
    private Navegacion baseDatos;
    private List<Problem> probDisp;
    private Problem activeProblem;
    
    private int hits, fails;
    private int initialChildren;   
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dragActive = new SimpleBooleanProperty();
        dragActive.bind(dragBtn.selectedProperty());
        transportImg.visibleProperty().bind(transportBtn.selectedProperty());
        
        strokeSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        textSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 20));
        
        // inicializamos el slider y enlazamos con el zoom
        zoomSlider.setMin(0.5);
        zoomSlider.setMax(6);
        zoomSlider.setValue(1.0);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(mapScrollpane.getContent());
        mapScrollpane.setContent(contentGroup);
        initialChildren = mapPane.getChildren().size();
        
        
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
        event.consume();
    }

    @FXML void zoomOut(ActionEvent event) {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + -0.1);
        event.consume();
    }

    
    @FXML private void manageAction(MouseEvent event) {
        
        // TODO: Refactorizar lo que hace cada botón
        
        
        if (!event.isPrimaryButtonDown()) { 
            // Si no está apretado el botón derecho del ratón no hace nada
            // Así no dibuja cuando intentas eliminar un Node
            return; 
        } 
        
        else if (dragActive.get() && !transportBtn.selectedProperty().get()) {
            initialXTrans = event.getSceneX();
            initialYTrans = event.getSceneY();
            baseX = contentGroup.getTranslateX();
            baseY = contentGroup.getTranslateY();
        }
        
        else if (drawLineBtn.selectedProperty().get()) {
            drawLine(event);
        }
        
        else if (drawPointBtn.selectedProperty().get()) {
            drawPoint(event);
        }
        
        else if (drawArcBtn.selectedProperty().get()) {
            drawArc(event);
        }        
        
        else if (addTextBtn.selectedProperty().get()) {
            addText(event);
        }
        
        event.consume();
    }
    
    @FXML private void modAction(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) { 
            // Si no está apretado el botón derecho del ratón no hace nada
            // Así no dibuja cuando intentas eliminar un Node
            return; 
        } 
        
        if (dragActive.get() && !transportBtn.selectedProperty().get()) {
            double despX = event.getSceneX() - initialXTrans;
            double despY = event.getSceneY() - initialYTrans;
            contentGroup.setTranslateX(baseX + despX);
            contentGroup.setTranslateY(baseY + despY);
        }
        
        else if (drawLineBtn.selectedProperty().get() && linePainting != null) {
            linePainting.setEndX(event.getX());
            linePainting.setEndY(event.getY());
        }
        
        else if (drawArcBtn.selectedProperty().get()) {
            double dif[] = new double[]{ Math.abs(event.getX() - initialArc[0]), Math.abs(event.getY() - initialArc[1])};
            double radio = Math.sqrt((dif[0]*dif[0])+(dif[1]*dif[1]));
            circlePainting.setRadius(radio);
        }

        event.consume();
    } 

    @FXML private void cleanMap(ActionEvent event) {

        while (mapPane.getChildren().size() > initialChildren) {
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
        List<Integer> order = Arrays.asList(new Integer[]{0,1,2,3});
        Collections.shuffle(order);
       
        for(Toggle t: opciones.getToggles()){
            Answer ans = ansListTemp.get(order.get(opciones.getToggles().indexOf(t)));
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
                    ((RadioButton)t).textFillProperty().set(Color.LIGHTGREEN);
                }
                else if (((RadioButton)t).isSelected() && !(boolean)((RadioButton)t).getUserData()) {
                    fails++;
                    ((RadioButton)t).textFillProperty().set(Color.RED);
                }
                else if (!((RadioButton)t).isSelected() && (boolean)((RadioButton)t).getUserData()) {
                    ((RadioButton)t).textFillProperty().set(Color.LIGHTGREEN);
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
    private void loadStatsScreen(ActionEvent event) throws Exception {
        FXMLLoader statsWindow = new FXMLLoader(getClass().getResource("/navapp/views/StatsScreenView.fxml"));
        Parent root = statsWindow.load();
        
        StatsScreenController controlador = statsWindow.getController();
        controlador.initializeStats(loggedUser, hits, fails);
        controlador.initializeCharts();
        
        Stage stage = new Stage();
        stage.setTitle("Estadísticas de Usuario");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    
    @FXML
    public void closeSession(ActionEvent event) throws Exception {
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
        controlador.setCancelFocus();
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
    
    ////////////////////////////////////////
    // IMPLEMENTACIÓN DE LAS HERRAMIENTAS //
    ///////////////////////////////////////
    
    private void drawLine(MouseEvent event) {
        linePainting = new Line(event.getX(), event.getY(), event.getX(), event.getY());
            linePainting.setStrokeWidth(strokeSize.getValue());
            linePainting.setStroke(colorPicker.getValue());
            mapPane.getChildren().add(linePainting);

            addContextualMenu(linePainting);
    }
    
    private void addText(MouseEvent event) {
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
    
    private void drawArc(MouseEvent event) {
        circlePainting = new Circle(1);
        circlePainting.setStroke(colorPicker.getValue());
        circlePainting.setStrokeWidth(strokeSize.getValue());
        circlePainting.setFill(Color.TRANSPARENT);
        circlePainting.setCenterX(event.getX());
        circlePainting.setCenterY(event.getY());
        initialArc = new double[]{event.getX(), event.getY()};
        //zoomGroup.getChildren().add(circlePainting);
        mapPane.getChildren().add(circlePainting);
            
        addContextualMenu(circlePainting);
       
    }
    
    private void drawPoint(MouseEvent event) {
    
        Circle circle = new Circle(event.getX(), event.getY(), strokeSize.getValue(), colorPicker.getValue());
            mapPane.getChildren().add(circle);
            
            Runnable drawMarks = () -> {
                if(markerLines != null) {
                    mapPane.getChildren().remove(markerLines[0]);
                    mapPane.getChildren().remove(markerLines[1]);
                }
                markerLines = new Line[2];
                    
                markerLines[0] = new Line(circle.getCenterX(), 0, circle.getCenterX(), mapImg.getFitHeight());
                markerLines[0].setStrokeWidth(strokeSize.getValue());
                markerLines[0].setStroke(colorPicker.getValue());
                markerLines[0].strokeProperty().addListener( (ob) -> {
                    markerLines[1].setStroke(markerLines[0].strokeProperty().get());
                });
                    
                markerLines[1] = new Line(0, circle.getCenterY(), mapImg.getFitWidth(), circle.getCenterY());
                markerLines[1].setStrokeWidth(strokeSize.getValue());
                markerLines[1].setStroke(colorPicker.getValue());
                markerLines[1].strokeProperty().addListener( (ob) -> {
                    markerLines[0].setStroke(markerLines[1].strokeProperty().get());
                });
                
                mapPane.getChildren().add(markerLines[0]);
                mapPane.getChildren().add(markerLines[1]);
                
                for(Line line : markerLines) {
                    addContextualMenu(line);
                }
            };
            
            addContextualMenu(circle, drawMarks, "Marcar Extremos");
    }
    
    private void addContextualMenu(Shape shape) {
        addContextualMenu(shape, null, null);
    }
    
    private void addContextualMenu(Shape shape, Runnable action, String actionName) {
         shape.setOnContextMenuRequested(e -> {
            ContextMenu menuContext = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Eliminar");
            MenuItem applyColorItem = new MenuItem("Aplicar nuevo color");

            menuContext.getItems().add(deleteItem);
            menuContext.getItems().add(applyColorItem);
                
            deleteItem.setOnAction(ev -> {
                if(((Node) e.getSource()).equals(markerLines[0]) || ((Node) e.getSource()).equals(markerLines[1])){
                    mapPane.getChildren().remove(markerLines[0]);
                    mapPane.getChildren().remove(markerLines[1]);
                } else {
                    mapPane.getChildren().remove((Node) e.getSource());
                }
                ev.consume();
            });
                
            if(action != null) {
                
                MenuItem drawMarksItem = new MenuItem(actionName);
                menuContext.getItems().add(drawMarksItem);
                
                applyColorItem.setOnAction(ev -> {
                    ((Shape) e.getSource()).setFill(colorPicker.getValue());
                    ev.consume();
                });
                
                drawMarksItem.setOnAction(ev -> {
                        action.run();
                        ev.consume();
                });
            }
            else {
                applyColorItem.setOnAction(ev -> {
                    ((Shape) e.getSource()).setStroke(colorPicker.getValue());
                    ev.consume();
                });
            }
            
            menuContext.show(shape, e.getSceneX(), e.getSceneY());
            e.consume();
        }); 
         
         if(action == null) { return; }   
         shape.setOnMousePressed(e -> {
                if(marcarExtremBtn.selectedProperty().get() && e.isPrimaryButtonDown()) { action.run(); }
                e.consume();
         });
    }

    

    @FXML
    private void dragTool(MouseEvent event) {
        if(transportBtn.selectedProperty().get() && dragActive.get()) {
            double despX = event.getSceneX() - initialXTrans;
            double despY = event.getSceneY() - initialYTrans;
            transportImg.setTranslateX(baseX + despX);
            transportImg.setTranslateY(baseY + despY);
        }
    }

    @FXML
    private void holdTool(MouseEvent event) {
        if(transportBtn.selectedProperty().get() && dragActive.get()) {
            initialXTrans = event.getSceneX();
            initialYTrans = event.getSceneY();
            baseX = transportImg.getTranslateX();
            baseY = transportImg.getTranslateY();
        }
        
    }

    @FXML
    private void showProblemList(ActionEvent event) throws Exception {
        FXMLLoader questionsListWindow = new FXMLLoader(getClass().getResource("/navapp/views/QuestionsListView.fxml"));
        Parent root = questionsListWindow.load();
        
        QuestionsListController controlador = questionsListWindow.getController();
        
        Stage stage = new Stage();
        stage.setTitle("Lista de Preguntas");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        if (controlador.userHasSelectedProblem()) {
            activeProblem = controlador.getSelectedQuestion();
            updateProblem();
        }
        else {
            return;
        }
        
    }
}
