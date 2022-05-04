/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp;

import javafx.scene.shape.Polygon;

/**
 *
 * @author Adrián Martínez
 */
public class TriangleShape extends Polygon {
    
    public TriangleShape() {
        super();
    }
    
    public TriangleShape(double xCenter, double yCenter, double offset) {
        super();
        super.getPoints().setAll(
            xCenter - offset, yCenter + offset, // Esquina inferior izquierda
            xCenter + offset, yCenter + offset, // Esquina inferior derecha
            xCenter, yCenter - offset           // Punta del triángulo
        );
    }
}
