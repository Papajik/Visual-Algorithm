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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *
 * @author Papi
 */
public class PanelGraph extends JPanel {

    /**
     * Představuje instanci třídy procházející graf
     */
    private PanelAlgorithm alg;
    /**
     * Pokud true - vytváříme graf Pokud false - jsme v prostředí pro tvorbu
     * grafu
     */
    private boolean generateGraph = false;
    /**
     * Pokud proběhla změna
     */
    private boolean changed;
    /**
     * Pro update comboboxu, aby nespouštěl handler
     */
    private boolean updating;
    /**
     * Pro generátor jmen
     */
    private int namesAsigned = 0;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    JToolBar createBar;
    /**
     * Vlastnosti zobrazeného objektu
     */
    JPanel rightPanel;
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
        super.setBackground(Color.white);
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        changed = false;
        setProperties();
        setCreateBar();
        setMouse();
        setKeyboard();
        cards.show(rightPanel, "node");
    }

    public boolean getChanged() {
        return changed;
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public void stopGenerating() {
        generateGraph = false;
        createBar.setVisible(true);
        add(createBar, BorderLayout.PAGE_START);
        cards.show(rightPanel, "node");

    }

    /**
     * Odstraní označení u všech uzlů a hran
     */
    public void deselectAll() {
        for (Node n : nodes) {
            n.setSelected(false);
        }
        for (Edge e : edges) {
            e.setSelected(false);
        }
    }

    /**
     * Nastaví všechny uzly na vykrelování nejkratších vzdáleností
     *
     * @param x
     */
    public void setDijkstra(boolean x) {
        for (Node n : nodes) {
            n.setDijkstra(true);
        }

    }

    //<editor-fold desc="Setting up mouse and keyboard listeners" defaultstate="collapsed">
    private void setMouse() {
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (rightPanel.getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                if (chooser == 1) {
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
                if (alg != null) {
                    //  System.out.println(alg.filePath);   
                }
                if (rightPanel.getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                deselectProperties();
                if (selectNode(getNode(e.getX(), e.getY()))) {
                    deselectEdge();
                    updateProperties(true);
                } else if (selectEdge(getEdge(e.getX(), e.getY()))) {
                    updateProperties(false);
                }

                paintImmediately(0, 0, getWidth(), getHeight());
            }

            private boolean selectNode(Node n) {
                if (selectedNode != null) {
                    if (selectedNode == n) {
                        return true;
                    } else {
                        selectedNode.setSelected(false);
                    }
                }
                selectedNode = n;
                if (selectedNode == null) {
                    return false;
                } else {
                    selectedNode.setSelected(true);
                    return true;
                }
            }

            private void deselectEdge() {
                if (selectedEdge != null) {
                    selectedEdge.setSelected(false);
                    selectedEdge = null;
                }
            }

            private boolean selectEdge(Edge n) {
                if (!generateGraph) {
                    if (selectedEdge != null) {
                        if (selectedEdge == n) {
                            return true;
                        } else {
                            selectedEdge.setSelected(false);
                        }
                    }
                    selectedEdge = n;
                    if (selectedEdge == null) {
                        return false;
                    } else {
                        selectedEdge.setSelected(true);
                        return true;
                    }
                } else {
                    return false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedNode != null && chooser == 2 && getNode(e.getX(), e.getY()) != null) {
                    System.out.println("Creating edge");
                    Edge edge = createEdge(selectedNode, getNode(e.getX(), e.getY()), false, 1);
                    if (edge != null) {
                        edges.add(edge);
                    }

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
                if (rightPanel.getBounds().contains(e.getX(), e.getY()) || !getBounds().contains(e.getX(), e.getY())) {
                    return;
                }
                if (selectedNode != null && chooser != 2) {
                    selectedNode.setX(e.getX());
                    selectedNode.setY(e.getY());
                    paintImmediately(0, 0, getWidth(), getHeight());
                }
                if (selectedNode != null && chooser == 2) {
                    paintLine(selectedNode.getX(), selectedNode.getY(), e.getX(), e.getY());
                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        this.addMouseMotionListener(ml);
    }

    private void setKeyboard() {
        Action delete = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("DELTE");
                if (selectedEdge != null) {
                    edges.remove(selectedEdge);
                    selectedEdge = null;

                }
                if (selectedNode != null) {
                    for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
                        Edge edge = it.next();
                        if (edge.contains(selectedNode)) {
                            it.remove();
                        }
                    }
                    nodes.remove(selectedNode);
                    selectedNode = null;
                }
                repaint();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delete");
        this.getActionMap().put("delete", delete);
    }
    //</editor-fold>
    //<editor-fold desc="Work with Nodes and Edges" defaultstate="collapsed">

    private Node createNode(int x, int y) {
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
        boolean unique = false;
        String name = "";
        while (!unique) {
            name = "";
            char startChar;
            char c;
            int temp = namesAsigned;
            startChar = 'A';
            for (int i = 0; i <= (namesAsigned / 26); i++) {
                c = (char) (startChar + temp % 26);
                name = c + name;
                temp /= 26;
            }
            namesAsigned++;
            if (!nameExists(name)) {
                unique = true;
            }
        }

        return name;
    }

    private boolean nameExists(String s) {
        for (Node n : nodes) {
            if (n.getName().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private Edge createEdge(Node a, Node b, boolean oriented, int length) {
        System.out.println(a.getName() + "+" + b.getName());
        if (a.equals(b)) {
            System.out.println("Equals");
            return null;
        }
        if (edgeExists(a, b)) {
            System.out.println("Edge exists");
            return null;
        }
        changed = true;
        return new Edge(a, b, oriented, length);
    }

    /**
     * Kontrola, zda existuje hrana mezi dvěma body.
     *
     * @param a první bod
     * @param b druhý bod
     * @return
     */
    private boolean edgeExists(Node a, Node b) {
        for (Edge e : edges) {
            if ((e.getFrom().equals(a) || e.getFrom().equals(b)) && (e.getTo().equals(a) || e.getTo().equals(b))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metoda pro určení, který bod si uživatel přeje označit.
     *
     * @param x
     * @param y
     * @return
     */
    private Node getNode(double x, double y) {
        for (Node nod : nodes) {

            if (nod.contain(x, y)) {
                return nod;
            }
        }
        return null;
    }

    /**
     * Metoda pro určení, kterou hranu si uživatel přeje označit.
     *
     * @param x
     * @param y
     * @return
     */
    private Edge getEdge(double x, double y) {
        int hitSize = 8;
        Line2D.Double line;
        System.out.println("EDGES");
        for (Edge ed : edges) {
            System.out.println(ed.getFrom().getName() + ":" + ed.getTo().getName());
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
    }

    public void paintLine(int x1, int y1, int x2, int y2) {
        Graphics g = this.getGraphics();
        paintImmediately(Math.min(x1, x2) / 2, Math.min(y1, y2) / 2, (int) (Math.max(x1, x2) * 1.5), (int) (Math.max(y1, y2) * 1.5));
        g.drawLine(x1, y1, x2, y2);
    }
//</editor-fold>
    //<editor-fold desc="Creating JToolBar for generating graph" defaultstate="collapsed">

    /**
     * Vytvoří JToolBar s nástroji pro tvorbu grafu.
     */
    private void setCreateBar() {
        JButton jbRun, jbNode, jbEdge;
        createBar = new JToolBar("Tvorba grafu");
        createBar.setRollover(true);
        jbNode = new JButton("Nový uzel");
        jbEdge = new JButton("Nová hrana");
        jbRun = new JButton("Spusť algoritmus");

        jbNode.setIcon(new ImageIcon(this.getClass().getResource("/Assets/Node.png")));
        jbEdge.setIcon(new ImageIcon(this.getClass().getResource("/Assets/Edge.png")));
        jbRun.setIcon(new ImageIcon(this.getClass().getResource("/Assets/Run.png")));

        createBar.addSeparator();
        createBar.add(jbNode);
        createBar.addSeparator();
        createBar.add(jbEdge);
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
            deselectAll();
            alg = new PanelAlgorithm(this);
            createBar.setVisible(false);

            cards.show(rightPanel, "options");
            generateGraph = true;
            chooser = -1;
        });
        add(createBar, BorderLayout.PAGE_START);
    }

//</editor-fold>
    //<editor-fold desc="Create Properties Panel" defaultstate="collapsed">
    /**
     * Nastaví panel vlastnosti objektu v grafu. Přidá obě varianty pro
     * vlastnosti hrany i vrcholu. Přepínat mezi obsahem vlastností umožňuje
     * card layout.
     */
    private void setProperties() {
        cards = new CardLayout();
        rightPanel = new JPanel(cards);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        rightPanel.add(propertiesNode, "node");

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
        rightPanel.add(propertiesEdge, "edge");

        JLabel ori = new JLabel("Orientace");
        ori.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertiesEdge.add(ori);
        combProperties = new JComboBox();
        combProperties.setMaximumSize(tStroke.getMaximumSize());
        propertiesEdge.add(combProperties);
        setActions();
        deselectProperties();
        cards.show(rightPanel, "edge");
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Nastaví postraní panel s vlastnostmi vybraného objektu v závislosti na
     * typu vybraného objektu.
     *
     * @param node Pokud node = true, zobrazí se vlastnosti vrcholu. V opačném
     * případě dojde k zobrazení vlastností hrany.
     *
     */
    private void updateProperties(boolean node) {
        if (!generateGraph) {
            if (node) {
                tName.setText(selectedNode.getName());
                tName.setEditable(true);
                tName.setEnabled(true);
                tWide.setText("" + selectedNode.getSize());
                tWide.setEditable(true);
                tWide.setEnabled(true);
                cards.show(rightPanel, "node");
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
                cards.show(rightPanel, "edge");
            }
        } else {
            alg.updateNodeName(selectedNode);
        }
    }

    /**
     * Nastaví program do stavu, kdy nebyl uživatelem vybrán žádný objekt grafu.
     */
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
        combProperties.removeAllItems();
        combProperties.setEnabled(false);
    }

    /**
     * Nastaví tlačítka dostupná při vytváření grafu.
     */
    private void setActions() {
        tName.addActionListener((ActionEvent e) -> {
            if (!nameExists(tName.getText())) {
                selectedNode.setName(tName.getText());
            } else {
                tName.setText(selectedNode.getName());
            }

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
            paintImmediately(0, 0, getWidth(), getHeight());
        });

    }

    //</editor-fold>
}
