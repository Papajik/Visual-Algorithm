/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author Papi
 */
public class PanelGraph extends JPanel {

    private int namesAsigned = 0;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    JToolBar toolBar;
    JButton jbRun, jbNode, jbEdge;
    Graphics g2;

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
                System.out.println("Chooser:" + chooser);
                if (chooser == -1) {
                    if (selectedNode!=null){
                        selectedNode.setSelected(false);
                    }
                    selectedNode = selectNode(e.getX(), e.getY());
                    if (selectedNode != null) {
                         System.out.println("Selecting node");
                        selectedNode.setSelected(true);
                    }
                   
                }
                if (chooser == 1) {
                    // System.out.println("Creating node");
                    selectedNode = createNode(e.getX(), e.getY());
                    nodes.add(selectedNode);
                }
                if (chooser == 2 && selectNode(e.getX(), e.getY()) != null && selectNode(e.getX(), e.getY()) != selectedNode) {
                    edges.add(new Edge(selectedNode, selectNode(e.getX(), e.getY()), false, 1));
                }
                setName("Graf");
                //System.out.println(e.getComponent().getName());
                paintComponent();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //System.out.println("Graph: Mouse released");
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
        return new Node(x, y, asignName(true));
    }

    private Edge createEdge(Node a, Node b, boolean oriented, int length) {
        return new Edge(a, b, oriented, length);
    }

    private Node selectNode(double x, double y) {
        for (Node nod : nodes) {
            System.out.println("looking at node" + nod.getName());
            if (nod.contain(x, y)) {
                System.out.println("found node");
               return nod;
            }
        }
        return null;
    }

    public void paintComponent() {
        Graphics g = this.getGraphics();
        paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Node nod : nodes) {
            nod.paint(g);
        }
        toolBar.repaint();
    }

    private void setToolBar() {
        toolBar = new JToolBar();
        toolBar.setRollover(true);
        jbNode = new JButton("Node");
        jbEdge = new JButton("Edge");
        jbRun = new JButton("Run");

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
        toolBar.addSeparator();
        toolBar.add(jbNode);
        toolBar.addSeparator();
        toolBar.add(jbEdge);
        toolBar.addSeparator();
        toolBar.add(jbRun);

        jbNode.addActionListener((ActionEvent e) -> {
            if (chooser != 1) {
                chooser = 1;
            } else {
                chooser = -1;
            }
        });

        jbEdge.addActionListener((ActionEvent e) -> {
            if (chooser != 2) {
                chooser = 2;
            } else {
                chooser = -1;
            }
        });

        jbRun.addActionListener((ActionEvent e) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
        add(toolBar);
    }

    /**
     * 
     * @param node If true, then looking for node name. Otherwise we are looking for name of edge
     * @return 
     */
    private String asignName(boolean node) {
        char startChar;
        String name = "";
        char c;
        int temp = namesAsigned;
        if (node) {
            startChar = 'A';
        } else {
            startChar = 'a';
        }
        for (int i = 0; i <= (namesAsigned / 26); i++) {
            c = (char) (startChar + temp % 26);
            name=c+name;
            temp/=26;
        }
        namesAsigned++;
        return name;
    }

} 
