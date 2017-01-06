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
public class Node {

    private double size = 40;
    private String name;
    private double x, y;
    private boolean selected;

    public Node(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public boolean contain(double cX, double cY) {
        return (size / 2 >= Math.sqrt(Math.pow(cX - x, 2) + Math.pow(cY - y, 2)));
    }

    public void setSelected(boolean x) {

        selected = x;
    }

    public String getName() {
        return name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double x) {
        size = x;
    }
    
    public void setName(String s){
        name = s;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D e = new Ellipse2D.Double(x - size / 2, y - size / 2, size, size);
        if (selected) {
            g2.setColor(Color.CYAN);
        } else {
            g2.setColor(Color.WHITE);
        }
        Stroke oldStroke = g2.getStroke();
        g2.fill(e);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(3));
        g2.draw(e);
        g2.setStroke(oldStroke);
        Font font = new Font("Serif", Font.PLAIN, 20);
        g2.setFont(font);
        g2.drawString(name, (float) (x - size / 4), (float) (y + size / 4));
    }

}
