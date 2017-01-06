/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Papi
 */
public class Edge {
    
    private String name;
    private boolean selected;
    private Node from, to;
    private boolean oriented;
    private int length;
    private int stroke = 5;

    public Edge(Node from, Node to, boolean oriented, int length) {
        this.from = from;
        this.to = to;
        this.oriented = oriented;
        this.length = length;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public boolean isOriented() {
        return oriented;
    }
    
    public void setOriented(boolean x){
        oriented = x;
    }
    
    public int getStroke(){
        return stroke;
    }
    
    public void setStroke(int s){
       stroke = s;
    }

    public int getLength() {
        return length;
    }
    
    public void  setLength(int l){
        length = l;
    }
    
    public void setSelected(boolean x){
        selected = x;
    }
    
    public boolean getSelected(){
        return selected;
    }
    public void switchNodes(){
        Node temp = from;
        from = to;
        to = temp;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selected) {
            g2.setColor(Color.CYAN);
        } else {
            g2.setColor(Color.ORANGE);
        }
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine((int)from.getX(),(int)from.getY(),(int)to.getX(),(int) to.getY());
        g2.setStroke(oldStroke);
        Font font = new Font("Serif", Font.PLAIN, 20);
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        g2.drawString(""+length, (float) ((from.getX()+to.getX())/2),(float)((from.getY()+to.getY())/2));
    }
}
