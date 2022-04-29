/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Navegacion;
import model.Problem;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class QuestionsListController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private Button closeBtn;
    @FXML private Button maximizeButton;
    @FXML private Button minimizeBtn;
    @FXML private VBox vBox;
    @FXML private ScrollPane questionsScrollPane;
    @FXML private Accordion accordion;
    @FXML private Button selectQuestionBtn;
    @FXML private Button backBtn;
    
    
    private Navegacion baseDatos;
    private List<Problem> probDisp;

    private double xOffset, yOffset;
    private TitledPane[] tps;
    private boolean userSelection;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userSelection = false;
        
        try{
            baseDatos = Navegacion.getSingletonNavegacion();
            probDisp = baseDatos.getProblems();
        }
        catch (Exception e) {
            System.out.print("Error al cargar la base de datos: ");
            System.out.println(e.toString());
        }
        
        tps = new TitledPane[probDisp.size()];
        
        for (int i = 0; i < probDisp.size(); i++) {
            // POR CADA PROBLEMA
            // Accordion
                // Por cada Pregunta un TitledPane
                // Añadir al TittledPane un TextField con el texto de la pregunta
               
            tps[i] = new TitledPane();
            tps[i].setText(new String("Problema: " + (i+1)));
            
            Text question = new Text(probDisp.get(i).getText());
            question.wrappingWidthProperty().set(400);
            tps[i].setContent(question);
        }
        
        accordion.getPanes().addAll(tps);
        accordion.setExpandedPane(tps[0]);
        
        selectQuestionBtn.setOnAction((ActionEvent event) -> {
            userSelection = true;
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        closeBtn.setOnAction((ActionEvent event) -> {
            userSelection = false;
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        backBtn.setOnAction((ActionEvent event) -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
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
                
        minimizeBtn.setOnAction((ActionEvent event) -> {
            ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true);
        });
  
    }
    
    public Problem getSelectedQuestion() {
        for(int i = 0; i < tps.length; i++) {
            if(tps[i].isExpanded()) {
                System.out.println("Seleccionado problema: " + (i + 1));
                return probDisp.get(i);
            }
        }
        System.out.println("Seleccionado problema: " + 1);
        return probDisp.get(0);
    }

    public boolean userHasSelectedProblem() { return userSelection; }
}
