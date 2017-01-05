/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author Papi
 */
public class PanelGraph extends JPanel {

    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private double radius;
    JToolBar toolBar;
    JButton jbBackward, jbForward, jbNode, jbEdge;

    /**
     * Odpovídá vybrané volbě na panelu nástrojů -1 nic nevybráno 1 vytvořit
     * nový uzel 2 vytvořit novou hranu
     */
    int chooser = -1;
    Node selectedNode = null;
    Node selectedEdge = null;

    public PanelGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        setToolBar();
        setMouse();
        // Graphics g = this.getGraphics();
        // paintComponent(g);
    }

    private void setMouse() {
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Graph: Mouse clicked");
                if (chooser == -1) {
                    selectedNode = selectNode(e.getX(), e.getY());
                }
                if (chooser == 1 && selectedNode != selectNode(e.getX(), e.getY())) {
                    selectedNode = createNode(e.getX(), e.getY());
                    nodes.add(selectedNode);
                }
                if (chooser == 2 && selectNode(e.getX(), e.getY()) != null && selectNode(e.getX(), e.getY()) != selectedNode) {
                    edges.add(new Edge(selectedNode, selectNode(e.getX(), e.getY()), false, 1));
                }
                setName("Graf");
                System.out.println(e.getComponent().getName());

            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Graph: Mouse pressed");
                selectedNode = selectNode(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Graph: Mouse released");
                if (selectedNode != null) {
                    edges.add(createEdge(selectedNode, selectNode(e.getX(), e.getY()), false, 1));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        };
        this.addMouseListener(mouseListener);

        MouseMotionListener ml = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        this.addMouseMotionListener(ml);
    }

    private Node createNode(double x, double y) {
        return new Node(x, y, "A");
    }

    private Edge createEdge(Node a, Node b, boolean oriented, int length) {
        return new Edge(a, b, oriented, length);
    }

    private Node selectNode(double x, double y) {
        Node selected = null;
        for (Node nod : nodes) {
            if (nod.contain(x, y, radius)) {
                selected = nod;
            }
        }
        return selected;
    }

    public void paintComponent() {
        Graphics g = this.getGraphics();
        paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.println("Painting component");
        for (Node nod : nodes) {
            nod.paint(g);
        }
    }

    private void setToolBar() {
        toolBar = new JToolBar();
        toolBar.setRollover(true);
        jbBackward = new JButton("Backward");
        jbForward = new JButton("Forward");
        jbNode = new JButton("Node");
        jbEdge = new JButton("Edge");
        
//        try {
//            jbBackward = new JButton();
//            jbBackward.setBorder(null);
//            jbBackward.setContentAreaFilled(false);
//            ImageIcon img = new ImageIcon(getClass().getResource("Back.png"));
//            jbBackward.setIcon(img);
//             jbBackward = new JButton(new ImageIcon("scr\\Assets\\Back.png"));
//            jbForward = new JButton(new ImageIcon("scr\\Assets\\Forw.png"));
//            jbNode = new JButton(new ImageIcon("scr\\Assets\\Node.png"));
//            jbEdge = new JButton(new ImageIcon("scr\\Assets\\Edge.png"));
//            Image img = ImageIO.read(getClass().getResource("Assets/Back.png"));
//            jbBackward.setIcon(new ImageIcon(img));
//            img = ImageIO.read(getClass().getResource("Assets\\Forw.png"));
//            jbForward.setIcon(new ImageIcon(img));
//            img = ImageIO.read(getClass().getResource("Assets\\Edge.png"));
//            jbEdge.setIcon(new ImageIcon(img));
//            img = ImageIO.read(getClass().getResource("Assets\\Node.png"));
//            jbNode.setIcon(new ImageIcon(img));
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        toolBar.add(jbBackward);
        toolBar.addSeparator();
        toolBar.add(jbForward);
        toolBar.addSeparator();
        toolBar.add(jbNode);
        toolBar.addSeparator();
        toolBar.add(jbEdge);
        add(toolBar);
    }

}
