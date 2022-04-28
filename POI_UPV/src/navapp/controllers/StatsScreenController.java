/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Session;
import model.User;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class StatsScreenController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private Button returnBtn;
    @FXML private PieChart actualSessionHitsPie;
    @FXML private PieChart historicalHitsPie;
    @FXML private Text actualSessionLabel;
    @FXML private Text historicalHitsLabel;
    
    private User loggedUser;
    private int hits, faults;
    private int histHits, histFaults;
    private double xOffset;
    private double yOffset;
    
    

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
        
        returnBtn.setOnAction((ActionEvent event) -> {
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
    }    
    
    public void initializeStats(User user, int hits, int fails) {
        loggedUser = user;
        this.hits = hits;
        this.faults = fails;
    }
    
    public void initializeCharts() {
        histHits = 0;
        histFaults = 0;
               
        ObservableList<PieChart.Data> actualData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> histHitsFaults = FXCollections.observableArrayList();

        // Recuperación de los datos históricos
        List<Session> histSessions = loggedUser.getSessions();
       
        
        for(Session session : histSessions) {
            histHits += session.getHits();
            histFaults += session.getFaults();
        }

        actualData.add(new PieChart.Data("Aciertos", this.hits));
        actualData.add(new PieChart.Data("Fallos", this.faults));
        actualSessionHitsPie.setData(actualData);
        
        histHitsFaults.add(new PieChart.Data("Aciertos", histHits));
        histHitsFaults.add(new PieChart.Data("Fallos", histFaults));
        historicalHitsPie.setData(histHitsFaults);
        
        actualSessionLabel.textProperty().set("Aciertos: " + this.hits + " (" + "%)" + 
                                            "\nFallos: " + this.faults + " (" + "%)");
        
        historicalHitsLabel.textProperty().set("Aciertos: " + histHits + " (" + "%)" +
                                             "\nFallos: " + histFaults + " (" + "%)");
    }
            
}
