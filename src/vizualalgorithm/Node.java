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
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Papi
 */
public class Node implements Serializable {

    private double size = 40;
    private String name;
    private int x, y;
    private boolean selected;
    /**
     * Cena součtu cest (pro dijksktru);
     */
    private int pathCost = Integer.MAX_VALUE;

    /**
     * Hrany vycházející z tohoto bodu. Pro funkcionalitu procházení grafu
     */
    private ArrayList<Edge> outcome = null;
    /**
     * Odkaz na cestu zpět ro vykreslení nalezené cesty
     */
    Edge pathBack;

    /**
     * Startovní bod
     */
    private boolean start = false;
    /**
     * Konečný bod
     */
    private boolean finish = false;
    /**
     * Navštívený (neaktivní)
     */
    private boolean visited = false;
    /**
     * Uložený v zásobníku
     */
    private boolean stacked = false;
    /**
     * Pro vykreslování vzdálenosti místo názvu
     */
    private boolean dijkstra = false;

    //barevná paleta
    static Color colorDefault = Color.WHITE;
    static Color colorStart = new Color(240, 240, 0);
    static Color colorFinish = new Color(255, 121, 77);
    static Color colorVisited = new Color(82, 173, 114);
    static Color colorStacked = new Color(100, 238, 138);
    static Color colorSelected = Color.CYAN;
    static Color colorString = Color.black;

    public Node(int x, int y, String name) {
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double x) {
        size = x;
    }

    public void setName(String s) {
        name = s;
    }

    public void setStart(boolean s) {
        start = s;
    }

    public void setFinish(boolean f) {
        finish = f;
    }

    public void setVisited(boolean s) {
        visited = s;
    }

    public void setStacked(boolean s) {
        stacked = s;
    }

    public boolean isVisited() {
        return visited;
    }

    public void initialize() {
        outcome = new ArrayList<>();
    }

    public ArrayList<Edge> getOutcome() {
        return outcome;
    }

    public void addOutcome(Edge e) {
        outcome.add(e);
    }

    public void addPathBack(Edge e) {
        pathBack = e;
    }

    public Edge getPathBack() {
        return pathBack;
    }

    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int cost) {
        this.pathCost = cost;
    }

    public Edge getEdgeTo(Node n) {
        for (Edge e : outcome) {
            if ((e.getTo().equals(n) || e.getTo().equals(this)) && (e.getFrom().equals(n) || e.getFrom().equals(this))) {
                return e;
            }
        }
        return null;
    }
    
    public void setDijkstra(boolean x){
        dijkstra = x;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D e = new Ellipse2D.Double(x - size / 2, y - size / 2, size, size);
        g2.setColor(colorDefault);

        if (start) {
            g2.setColor(colorStart);
        }
        if (finish) {
            g2.setColor(colorFinish);
        }
        if (visited) {
            g2.setColor(colorVisited);
        }

        if (stacked) {
            g2.setColor(colorStacked);
        }
        if (selected) {
            g2.setColor(colorSelected);
        }

        Stroke oldStroke = g2.getStroke();
        g2.fill(e);
        g2.setColor(colorString);
        g2.setStroke(new BasicStroke(3));
        g2.draw(e);
        g2.setStroke(oldStroke);
        Font font = new Font("Serif", Font.PLAIN, 20);
        g2.setFont(font);
        String text = "";
        if (dijkstra){
           if (pathCost == Integer.MAX_VALUE){
               text = "inf";
           } else {
               text = ""+pathCost;
           }
        } else {
            text = name;
        }
          g2.drawString(text, (float) (x - size / 4), (float) (y + size / 4));
       
    }

    @Override
    public String toString() {
        return name;
    }
}
