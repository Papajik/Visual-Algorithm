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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.io.Serializable;

/**
 *
 * @author Papi
 */
public class Edge implements Serializable {

    /**
     * Algorithm Již navštívená hrana, nelze znovu použít
     */
    private boolean visited;
    /**
     * PanelGraph Üživatelem vybraná hrana
     */
    private boolean selected;
    private Node from, to;
    /**
     * PanelGraph Značí orientovanou hranu
     */
    private boolean oriented;
    /**
     * Algorithm Vyznačí zpětně nejvýhodnější cestu k cílovému bodu
     */
    private boolean bestPath;
    /**
     * Algorithm Cesta již nalezená při procházení grafu
     */
    private boolean known;
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

    public void setOriented(boolean x) {
        oriented = x;
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int s) {
        stroke = s;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int l) {
        length = l;
    }

    public void setSelected(boolean x) {
        selected = x;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setVisited(boolean x) {
        visited = x;
    }

    public void setBestPath(boolean x) {
        bestPath = x;
    }

    public void setKnown(boolean x) {
        known = x;
    }

    public boolean getKnown() {
        return known;
    }

    public void switchNodes() {
        Node temp = from;
        from = to;
        to = temp;
    }

    public Node getOposite(Node n) {
        if (from.equals(n)) {
            return to;
        }
        if (to.equals(n)) {
            return from;
        }
        return null;
    }

    public void paint(Graphics g) {
        Point start = new Point(from.getX(), from.getY());
        Point finish = new Point(to.getX(), to.getY());
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selected) {
            g2.setColor(Color.CYAN);
        } else {
            g2.setColor(Color.ORANGE);
        }
        if (known) {
            g2.setColor(new Color(100, 238, 138));

        }
        if (visited) {
            g2.setColor(new Color(0, 102, 102));
        }

        if (bestPath) {
            g2.setColor(new Color(27, 30, 251));
        }
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine(start.x, start.y, finish.x, finish.y);

        Font font = new Font("Serif", Font.PLAIN, 20);
        g2.setFont(font);
        if (!oriented) {
            drawArrow(g2, finish, start);
        }
        drawArrow(g2, start, finish);
        g2.setColor(Color.BLACK);
        g2.setStroke(oldStroke);
        g2.drawString("" + length, (float) ((from.getX() + to.getX()) / 2), (float) ((from.getY() + to.getY()) / 2));
    }

    private void drawArrow(Graphics2D g2, Point tail, Point tip) {

        double deviation = Math.toRadians(20);
        int arrowLength = 30;
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        tip.move(tip.x - (int) (to.getSize() / 2 * Math.cos(theta)), tip.y - (int) (to.getSize() / 2 * Math.sin(theta)));
        double x, y, rho = theta + deviation;
        for (int j = 0; j < 2; j++) {
            x = tip.x - arrowLength * Math.cos(rho);
            y = tip.y - arrowLength * Math.sin(rho);
            g2.draw(new Line2D.Double(tip.x, tip.y, x, y));
            rho = theta - deviation;
        }
    }

    @Override
    public String toString() {
        return from.getName() + ":" + to.getName();
    }
}
