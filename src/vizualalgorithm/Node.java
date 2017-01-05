/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.Graphics;

/**
 *
 * @author Papi
 */
public class Node{
    private double x, y;
    private String name;
    private boolean selected;
    
    public Node(double x, double y, String name){
        this.x = x;
        this.y = y;
        this.name = name;
    }
    
    
    public boolean contain (double cX, double cY, double radius){
        return (radius<=Math.sqrt(Math.pow(cX-x, 2)+Math.pow(cY-y, 2)));
    }
    
    
   
    public void paint(Graphics g){
        System.out.println("NODE: painted");
    }
    
    
}
