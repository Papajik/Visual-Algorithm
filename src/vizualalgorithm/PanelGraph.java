/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 *
 * @author Papi
 */
public class PanelGraph extends JPanel {

    private boolean changed;
    private boolean updating;
    private int namesAsigned = 0;
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;
    JToolBar createBar, runBar;
    /**
     * Vlastnosti zobrazeného objektu
     */
    JPanel properties;
    /**
     * Vlastnosti bodu
     */
    JTextField tName, tWide;
    /**
     * Vlastnosti hrany
     */
    JTextField tLength, tStroke;
    /**
     * Výběr orientace
     */
    JComboBox combProperties, combAlgorithm;
    CardLayout cards;
    Graphics g2;

    /**
     * Odpovídá vybrané volbě na panelu nástrojů -1 nic nevybráno 1 vytvořit
     * nový uzel 2 vytvořit novou hranu
     */
    int chooser = -1;
    Node selectedNode = null;
    Edge selectedEdge = null;

    public PanelGraph() {
        super(new BorderLayout());
//        canvas = new JPanel();
//        add(canvas, BorderLayout.CENTER);
//        canvas.setBackground(Color.white);
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        changed = false;
        setProperties();
        setCreateBar();
        setRunOptions();
        setMouse();
        setBackground(Color.white);
    }

    public boolean getChanged() {
        return changed;
    }

    //Seting mouse listeners
    private void setMouse() {
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (properties.getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                if (chooser == 1) {
                    // System.out.println("Creating node");
                    Node n = createNode(e.getX(), e.getY());
                    nodes.add(n);
                }
                if (chooser == 2 && getNode(e.getX(), e.getY()) != null && getNode(e.getX(), e.getY()) != selectedNode) {
                    edges.add(new Edge(selectedNode, getNode(e.getX(), e.getY()), false, 1));

                }
                paintImmediately(0, 0, getWidth(), getHeight());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (properties.getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                if (selectNode(getNode(e.getX(), e.getY()))) {
                } else if (!selectEdge(getEdge(e.getX(), e.getY()))) {

                    deselectProperties();
                }

                paintImmediately(0, 0, getWidth(), getHeight());
            }

            private boolean selectNode(Node n) {
                if (selectedNode != null) {
                    if (selectedEdge != null) {
                        selectedEdge.setSelected(false);
                    }
                    selectedNode.setSelected(false);
                    deselectProperties();
                }

                selectedNode = n;
                if (selectedNode != null) {
                    updateProperties(true);
                    selectedNode.setSelected(true);
                    return true;
                }
                return false;
            }

            private boolean selectEdge(Edge n) {
                if (selectedEdge != null) {
                    selectedEdge.setSelected(false);
                    if (selectedNode != null) {
                        selectedNode.setSelected(false);
                    }
                }

                selectedEdge = n;
                if (n != null) {

                    updateProperties(false);
                    selectedEdge.setSelected(true);
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //System.out.println("Graph: Mouse released");
                if (selectedNode != null && chooser == 2 && getNode(e.getX(), e.getY()) != null) {
                    System.out.println("Creating edge");
                    edges.add(createEdge(selectedNode, getNode(e.getX(), e.getY()), false, 1));
                }
                paintImmediately(0, 0, getWidth(), getHeight());
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
                if (properties.getBounds().contains(e.getX(), e.getY()) || !getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                if (selectedNode != null && chooser != 2) {
                    selectedNode.setX(e.getX());
                    selectedNode.setY(e.getY());
                    paintImmediately(0, 0, getWidth(), getHeight());
                }
                if (selectedNode != null && chooser == 2) {
                    paintLine((int) selectedNode.getX(), (int) selectedNode.getY(), (int) e.getX(), (int) e.getY());
                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        this.addMouseMotionListener(ml);
    }
    //<editor-fold desc="Work with Nodes and Edges" defaultstate="collapsed">

    private Node createNode(double x, double y) {
        //System.out.println("creating node");
        changed = true;
        // System.out.println(getChanged());
        return new Node(x, y, asignName());
    }

    /**
     *
     * @param node If true, then looking for node name. Otherwise we are looking
     * for name of edge
     * @return
     */
    private String asignName() {
        char startChar;
        String name = "";
        char c;
        int temp = namesAsigned;
        startChar = 'A';
        for (int i = 0; i <= (namesAsigned / 26); i++) {
            c = (char) (startChar + temp % 26);
            name = c + name;
            temp /= 26;
        }
        namesAsigned++;
        return name;

    }

    private Edge createEdge(Node a, Node b, boolean oriented, int length) {
        changed = true;
        return new Edge(a, b, oriented, length);
    }

    private Node getNode(double x, double y) {
        for (Node nod : nodes) {
            if (nod.contain(x, y)) {
                return nod;
            }
        }
        return null;
    }

    private Edge getEdge(double x, double y) {
        int hitSize = 8;
        Line2D.Double line;
        for (Edge ed : edges) {
            line = new Line2D.Double(ed.getFrom().getX(), ed.getFrom().getY(), ed.getTo().getX(), ed.getTo().getY());
            if (line.intersects(x - hitSize / 2, y - hitSize / 2, hitSize, hitSize)) {
                return ed;
            }
        }
        return null;
    }
//</editor-fold>
    //<editor-fold desc="Rendering" defaultstate="collapsed">
    public void paintComponent() {
        Graphics g = getGraphics();
        paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        edges.stream().forEach((edg) -> {
            edg.paint(g);
        });
        nodes.stream().forEach((nod) -> {
            nod.paint(g);
        });

        createBar.repaint();
    }

    public void paintLine(int x1, int y1, int x2, int y2) {
        Graphics g = this.getGraphics();
        paintImmediately(Math.min(x1, x2) / 2, Math.min(y1, y2) / 2, (int) (Math.max(x1, x2) * 1.5), (int) (Math.max(y1, y2) * 1.5));
        g.drawLine(x1, y1, x2, y2);
    }
//</editor-fold>
    //<editor-fold desc="Creating JToolBar for generating graph" defaultstate="collapsed">
    private void setCreateBar() {
        JButton jbRun, jbNode, jbEdge;
        createBar = new JToolBar("Tvorba grafu");
        createBar.setRollover(true);
        jbNode = new JButton("Nový uzel");
        jbEdge = new JButton("Nová hrana");
        jbRun = new JButton("Spusť algoritmus");

        String filePath = new File("").getAbsolutePath() + "\\src\\Assets\\";
        jbNode.setIcon(new ImageIcon(filePath + "Node.png"));
        jbEdge.setIcon(new ImageIcon(filePath + "Edge.png"));
        jbRun.setIcon(new ImageIcon(filePath + "Run.png"));
        combAlgorithm = new JComboBox();
        combAlgorithm.addItem("Prohledávání do hloubky");
        combAlgorithm.addItem("Prohledávání do šířky");
        combAlgorithm.setMaximumSize(jbRun.getPreferredSize());
        combAlgorithm.setAlignmentX(LEFT_ALIGNMENT);
        createBar.addSeparator();
        createBar.add(jbNode);
        createBar.addSeparator();
        createBar.add(jbEdge);
        createBar.addSeparator();
        createBar.add(combAlgorithm);
        createBar.addSeparator();
        createBar.add(jbRun);

        jbNode.addActionListener((ActionEvent e) -> {
            if (chooser != 1) {
                jbNode.setBackground(new Color(153, 204, 255));
                jbEdge.setBackground(null);
                chooser = 1;
            } else {
                jbNode.setBackground(null);
                jbEdge.setBackground(null);
                chooser = -1;
            }
        });

        jbEdge.addActionListener((ActionEvent e) -> {
            if (chooser != 2) {
                jbEdge.setBackground(new Color(153, 204, 255));
                jbNode.setBackground(null);
                chooser = 2;
            } else {
                jbNode.setBackground(null);
                jbEdge.setBackground(null);
                chooser = -1;
            }
        });

        jbRun.addActionListener((ActionEvent e) -> {
            createBar.setVisible(false);
            createRunBar();
        });
        add(createBar, BorderLayout.PAGE_START);
    }
 
//</editor-fold>
    //<editor-fold desc="Creating JToolBar for running algorithm" defaultstate="collapsed">

    private void createRunBar() {
        runBar = new JToolBar("Průběh algoritmu");
        JButton jbBack = new JButton("Zpět");
        JButton jbNext = new JButton("Dopředu");
        JButton jbPause = new JButton("Pozastavit");
        JButton jbContinue = new JButton("Pokračovat");
        JButton jbStop = new JButton("Zastavit");

        String filePath = new File("").getAbsolutePath() + "\\src\\Assets\\";
        jbNext.setIcon(new ImageIcon(filePath + "Forw.png"));
        jbBack.setIcon(new ImageIcon(filePath + "Back.png"));
        jbPause.setIcon(new ImageIcon(filePath + "Pause.png"));
        jbContinue.setIcon(new ImageIcon(filePath + "Continue.png"));
        jbStop.setIcon(new ImageIcon(filePath + "Stop.png"));

        runBar.add(jbBack);
        runBar.addSeparator();
        runBar.add(jbNext);
        runBar.addSeparator();
        runBar.addSeparator();
        runBar.addSeparator();
        runBar.add(jbPause);
        runBar.addSeparator();
        runBar.add(jbContinue);
        runBar.addSeparator();
        runBar.add(jbStop);
        add(runBar, BorderLayout.PAGE_START);
    }
//</editor-fold>
    //<editor-fold desc="Create Properties Panel" defaultstate="collapsed">

    private void setProperties() {
        cards = new CardLayout();
        properties = new JPanel(cards);
        properties.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel propertiesNode = new JPanel();
        propertiesNode.setLayout(new BoxLayout(propertiesNode, BoxLayout.Y_AXIS));
        JLabel prop = new JLabel("Vlastnosti");
        prop.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesNode.add(prop);
        JLabel glue = new JLabel(" ");
        propertiesNode.add(glue);
        JLabel name = new JLabel("Název");
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesNode.add(name);
        tName = new JTextField(15);
        tName.setMaximumSize(tName.getPreferredSize());
        tName.setEditable(true);
        tName.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesNode.add(tName);
        JLabel wide = new JLabel("Velikost");
        wide.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesNode.add(wide);
        tWide = new JTextField(15);
        tWide.setMaximumSize(tWide.getPreferredSize());
        tWide.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesNode.add(tWide);
        properties.add(propertiesNode, "node");

        JPanel propertiesEdge = new JPanel();
        propertiesEdge.setLayout(new BoxLayout(propertiesEdge, BoxLayout.Y_AXIS));
        JLabel prop2 = new JLabel("Vlastnosti");
        prop2.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesEdge.add(prop2);
        JLabel glue2 = new JLabel(" ");
        propertiesEdge.add(glue2);
        JLabel size = new JLabel("Délka");
        size.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesEdge.add(size);
        tLength = new JTextField(15);
        tLength.setMaximumSize(tLength.getPreferredSize());
        tLength.setEditable(true);
        propertiesEdge.add(tLength);
        JLabel width = new JLabel("Šířka");
        width.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesEdge.add(width);

        tStroke = new JTextField(15);
        tStroke.setMaximumSize(tStroke.getPreferredSize());
        propertiesEdge.add(tStroke);
        properties.add(propertiesEdge, "edge");
        ;
        JLabel ori = new JLabel("Orientace");
        ori.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesEdge.add(ori);
        combProperties = new JComboBox();
        combProperties.setMaximumSize(tStroke.getMaximumSize());
        propertiesEdge.add(combProperties);
        setActions();
        deselectProperties();
        cards.show(properties, "edge");
        add(properties, BorderLayout.EAST);
    }

    private void updateProperties(boolean node) {
        if (node) {
            tName.setText(selectedNode.getName());
            tName.setEditable(true);
            tName.setEnabled(true);
            tWide.setText("" + selectedNode.getSize());
            tWide.setEditable(true);
            tWide.setEnabled(true);
            cards.show(properties, "node");
        } else {
            tLength.setText("" + selectedEdge.getLength());
            tLength.setEditable(true);
            tLength.setEnabled(true);
            tStroke.setText("" + selectedEdge.getStroke());
            tStroke.setEditable(true);
            tStroke.setEnabled(true);
            updating = true;
            combProperties.removeAllItems();
            combProperties.addItem("Bez orientace");
            String from = selectedEdge.getFrom().getName();
            String to = selectedEdge.getTo().getName();
            combProperties.addItem(from + "->" + to);
            combProperties.addItem(to + "->" + from);
            if (!selectedEdge.isOriented()) {
                combProperties.setSelectedItem("Bez orientace");
            } else {
                combProperties.setSelectedItem(from + "->" + to);
            }
            combProperties.setEnabled(true);
            updating = false;
            cards.show(properties, "edge");
        }

    }

    private void deselectProperties() {
        tName.setText("");
        tName.setEditable(false);
        tName.setEnabled(false);
        tWide.setText("");
        tWide.setEditable(false);
        tWide.setEnabled(false);
        tLength.setText("");
        tLength.setEditable(false);
        tLength.setEnabled(false);
        tStroke.setText("");
        tStroke.setEditable(false);
        tStroke.setEnabled(false);
        combProperties.setEnabled(false);
    }

    private void setActions() {
        tName.addActionListener((ActionEvent e) -> {
            selectedNode.setName(tName.getText());
            paintImmediately(0, 0, getWidth(), getHeight());
        });
        tWide.addActionListener((ActionEvent e) -> {
            try {
                double d = Double.parseDouble(tWide.getText());
                if (d < 10) {
                    d = 10;
                } else if (d > 70) {
                    d = 70;
                }
                tWide.setText("" + d);
                selectedNode.setSize(d);
            } catch (NumberFormatException ex) {
                tWide.setText(selectedNode.getSize() + "");
            }
            paintImmediately(0, 0, getWidth(), getHeight());
        });
        tLength.addActionListener((ActionEvent e) -> {
            try {
                int i = Integer.parseInt(tLength.getText());
                selectedEdge.setLength(i);
            } catch (NumberFormatException ex) {
                tLength.setText(selectedEdge.getLength() + "");
            }
            paintImmediately(0, 0, getWidth(), getHeight());
        });
        tStroke.addActionListener((ActionEvent e) -> {
            try {
                int i = Integer.parseInt(tStroke.getText());
                if (i < 1) {
                    i = 10;
                } else if (i > 10) {
                    i = 10;

                }
                tStroke.setText("" + i);
                selectedEdge.setStroke(i);
            } catch (NumberFormatException ex) {
                tStroke.setText(selectedEdge.getStroke() + "");
            }
            paintImmediately(0, 0, getWidth(), getHeight());
        });

        combProperties.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !updating) {
                String selected = (String) e.getItem();
                if (selected.equals("Bez orientace")) {
                    System.out.println("No orientation");
                    selectedEdge.setOriented(false);
                    return;
                }

                selectedEdge.setOriented(true);
                if (!selected.startsWith(selectedEdge.getFrom().getName())) {
                    System.out.println("Switching nodes");
                    selectedEdge.switchNodes();
                }
            }
        });

    }
    //</editor-fold>
    //<editor-fold desc="Create Run options panel" defaultstate="collapsed">
    private void setRunOptions(){
        
    }
    //</editor-fold>
}
