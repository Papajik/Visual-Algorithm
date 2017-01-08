/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Papi
 */
public class Algorithm {

    private Node start;
    private Node finish;
    private PanelGraph pg;
    private JToolBar runBar;
    private JTextField output, name, step;
    private JComboBox combo;
    private Object lock;
    private FS fs = null;
    private Stack<Object> history;
    private int historyPoint = 0;

    public void setStart(Node n) {
        if (start != null) {
            start.setStart(false);
        }
        if (n != null) {
            n.setStart(true);
            n.setFinish(false);
        }
        start = n;
    }

    public void setFinish(Node n) {
        if (finish != null) {
            finish.setFinish(false);
        }
        if (finish == n) {
            finish = null;
            return;
        }
        if (n != null) {
            n.setFinish(true);
            n.setStart(false);
        }

        finish = n;
    }

    public Node getStart() {
        return start;
    }

    public Node getFinish() {
        return finish;
    }

    public Algorithm(PanelGraph pg) {
        this.pg = pg;
        history = new Stack<>();
        setHistoryPoint();
        setRunOptions();
        createRunBar();

    }

    public void write(String t, Node n) {
        output.setText(t);
        updateNode(n);

    }

    public void repaint() {
        pg.paintImmediately(0, 0, pg.getWidth(), pg.getHeight());
    }

    public void load() {

        ArrayList<Edge> edges = pg.getEdges();
        ArrayList<Node> nodes = pg.getNodes();
        System.out.println("Algoritmy Načítání ");
        for (Node n : nodes) {
            n.initialize();
            for (Edge e : edges) {
                if (e.isOriented()) {
                    if (e.getFrom().equals(n)) {
                        n.addOutcome(e);
                        System.out.println("DFS: adding " + e + " to " + n);
                    }
                } else if (e.getFrom().equals(n) || e.getTo().equals(n)) {
                    n.addOutcome(e);
                    System.out.println("DFS: adding " + e + " to " + n);
                }
            }
        }
    }

    public void start(String choice) {
        switch (choice) {
            case "Prohledávání do hloubky":

                if (lock == null) {
                    pg.deselectAll();
                    fs = new FS(this, false);
                    lock = fs.getLock();
                    System.out.println("Spouštím vlákno");
                    new Thread(fs).start();
                    System.out.println("Opouštím vlákno");
                }

                break;
            case "Prohledávání do šířky": {
                if (lock == null) {
                    pg.deselectAll();
                    fs = new FS(this, true);
                    lock = fs.getLock();
                    new Thread(fs).start();
                    break;
                }
            }
        }
    }

    public void pauseRunningThread() {
        if (fs != null) {
            System.out.println("SETING PAUSE");
            fs.setRun(false);
        }
    }

    public void threadStoped() {
        System.out.println("Thread is stopped");
        lock = null;
        fs = null;
        historyPoint = history.size() - 1;
        repaint();
    }

    /**
     * Nastaví postraní panel ukazující aktuální informace
     */
    private void setRunOptions() {
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        output = new JTextField();

        step = new JTextField();
        Dimension d = output.getPreferredSize();
        step.setMaximumSize(d);
        step.setEditable(false);
        d.width = Integer.MAX_VALUE;
        output.setMaximumSize(d);
        output.setEditable(false);
        name = new JTextField();
        name.setMaximumSize(d);
        name.setEditable(false);
        JButton setStart = new JButton("Nastav start");
        setStart.setMaximumSize(d);
        setStart.setBackground(new Color(240, 240, 0));
        JButton setEnd = new JButton("Nastav konec");
        setEnd.setMaximumSize(d);
        setEnd.setBackground(new Color(255, 121, 77));
        setEnd.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        JLabel noCoDefault = new JLabel("Základní bod");
        noCoDefault.setBackground(Node.colorDefault);
        noCoDefault.setOpaque(true);
        noCoDefault.setMaximumSize(d);
        JLabel noCoSelected = new JLabel("Aktuálně vybraný bod");
        noCoSelected.setBackground(Node.colorSelected);
        noCoSelected.setOpaque(true);
        noCoSelected.setMaximumSize(d);
        JLabel noCoStart = new JLabel("Počáteční bod");
        noCoStart.setBackground(Node.colorStart);
        noCoStart.setOpaque(true);
        noCoStart.setMaximumSize(d);
        JLabel noCoFinish = new JLabel("Hledaný bod");
        noCoFinish.setBackground(Node.colorFinish);
        noCoFinish.setOpaque(true);
        noCoFinish.setMaximumSize(d);
        JLabel noCoVisited = new JLabel("Již prošlý bod");
        noCoVisited.setBackground(Node.colorVisited);
        noCoVisited.setOpaque(true);
        noCoVisited.setMaximumSize(d);
        JLabel noCoStacked = new JLabel("Nalezený neprozkoumaný bod");
        noCoStacked.setBackground(Node.colorStacked);
        noCoStacked.setOpaque(true);
        noCoStacked.setMaximumSize(d);

        JLabel edCoDefault = new JLabel("Základní hrana");
        edCoDefault.setBackground(Edge.colorDefault);
        edCoDefault.setMaximumSize(d);
        edCoDefault.setOpaque(true);
        JLabel edCoSelect = new JLabel("Aktuálně vybraný bod");
        edCoSelect.setBackground(Edge.colorSelect);
        edCoSelect.setMaximumSize(d);
        edCoSelect.setOpaque(true);
        JLabel edCoKnown = new JLabel("Nalezená neprozkoumaná hrana");
        edCoKnown.setBackground(Edge.colorKnown);
        edCoKnown.setMaximumSize(d);
        edCoKnown.setOpaque(true);
        JLabel edCoVisited = new JLabel("Již prošlá hrana");
        edCoVisited.setBackground(Edge.colorVisited);
        edCoVisited.setMaximumSize(d);
        edCoVisited.setOpaque(true);
        JLabel edCoBestPath = new JLabel("Vyobrazení nalezené cesty");
        edCoBestPath.setBackground(Edge.colorBestPath);
        edCoBestPath.setMaximumSize(d);
        edCoBestPath.setOpaque(true);

        options.add(new JLabel("Aktuálně"));
        options.add(output);
        options.add(new JLabel("Krok"));
        options.add(step);
        options.add(new JLabel("Vybraný bod"));
        options.add(name);
        options.add(new JLabel(" "));
        options.add(setStart);
        options.add(new JLabel(" "));
        options.add(setEnd);
        options.add(new JLabel(" "));
        options.add(new JLabel("Vysvětlivky"));
        options.add(new JLabel(" "));
        options.add(new JLabel("Barvy bodů"));
        options.add(new JLabel(" "));
        options.add(noCoDefault);
        options.add(noCoStart);
        options.add(noCoFinish);
        options.add(noCoVisited);
        options.add(noCoStacked);
        options.add(noCoVisited);
        options.add(new JLabel(" "));
        options.add(new JLabel("Barvy hran"));
        options.add(new JLabel(" "));
        options.add(edCoDefault);
        options.add(edCoSelect);
        options.add(edCoKnown);
        options.add(edCoVisited);
        options.add(edCoBestPath);

        pg.getRightPanel().add(options, "options");

        setStart.addActionListener((ActionEvent e) -> {
            if (pg.getSelectedNode() != null) {
                setStart(pg.getSelectedNode());
            }
            repaint();
        });

        setEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFinish(pg.getSelectedNode());
                repaint();
            }

        });
    }

    public void updateNode(Node n) {
        if (n == null) {
            name.setText("");
            return;
        }
        name.setText(n.getName());
    }

    /**
     * Vytvoří JtoolBar pro ovládání programu během běhu algoritmu
     */
    private void createRunBar() {
        runBar = new JToolBar("Průběh algoritmu");
        JButton jbStart = new JButton("Na začátek");
        JButton jbBack = new JButton("Zpět");
        JButton jbNext = new JButton("Dopředu");
        JButton jbPause = new JButton("Pozastavit");
        JButton jbContinue = new JButton("Pokračovat");
        JButton jbStop = new JButton("Zastavit");
        JButton jbRun = new JButton("Spusť prohledávání");
        String filePath = new File("").getAbsolutePath() + "\\src\\Assets\\";
        jbNext.setIcon(new ImageIcon(filePath + "Forw.png"));
        jbStart.setIcon(new ImageIcon(filePath + "Rewind.png"));
        jbBack.setIcon(new ImageIcon(filePath + "Back.png"));
        jbPause.setIcon(new ImageIcon(filePath + "Pause.png"));
        jbContinue.setIcon(new ImageIcon(filePath + "Continue.png"));
        jbStop.setIcon(new ImageIcon(filePath + "Stop.png"));
        jbRun.setIcon(new ImageIcon(filePath + "Run2.png"));
        combo = new JComboBox();
        combo.addItem("Prohledávání do hloubky");
        combo.addItem("Prohledávání do šířky");
        combo.setMaximumSize(combo.getPreferredSize());
        combo.setAlignmentX(LEFT_ALIGNMENT);
        jbStart.setPreferredSize(jbBack.getPreferredSize());
        runBar.add(jbStart);
        runBar.addSeparator();
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
        runBar.addSeparator();
        runBar.add(combo);
        runBar.addSeparator();
        runBar.add(jbRun);
        pg.add(runBar, BorderLayout.PAGE_START);
        jbStart.addActionListener((ActionEvent e) -> {
            if (fs == null && history.size() >= 2) {
                historyPoint = 1;
                retrieveHistory(historyPoint);
                step.setText(historyPoint + "/" + (history.size() - 1));
            }
        });
        jbNext.addActionListener((ActionEvent e) -> {
            if (fs == null) {
                if (historyPoint < history.size() - 1) {
                    historyPoint++;
                    retrieveHistory(historyPoint);
                    step.setText(historyPoint + "/" + (history.size() - 1));
                }
            }
        });
        jbBack.addActionListener((ActionEvent e) -> {
            if (fs == null) {
                if (historyPoint > 1) {
                    historyPoint--;
                    retrieveHistory(historyPoint);
                    step.setText(historyPoint + "/" + (history.size() - 1));
                }
            }

        });
        jbPause.addActionListener((ActionEvent e) -> {
            pauseRunningThread();
        });
        jbContinue.addActionListener((ActionEvent e) -> {
            if (lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                }
                fs.setRun(true);
            }
        });
        jbStop.addActionListener((ActionEvent e) -> {
            runBar.setVisible(false);
            pg.stopGenerating();
            while (history.size() > 1) {
                history.pop();
            }
            retrieveHistory(0);
            repaint();
        });
        jbRun.addActionListener((ActionEvent e) -> {
            if (start != null && history.size() == 1) {
                start(combo.getSelectedItem().toString());
            }
        });
    }

    /**
     * Najde prvek ekvivalentní v tomu používaném v některém z předchozích kroků
     * Nalezne ho podle stejného jména (V programu je nemožné, aby dva body měli
     * stejné jméno)
     *
     * @param n
     */
    private Node getEqualsInCurrent(Node n) {
        if (n == null) {
            return null;
        }
        ArrayList<Node> nodes = pg.getNodes();
        for (Node nod : nodes) {
            if (n.getName().equalsIgnoreCase(nod.getName())) {
                return nod;
            }
        }
        return null;
    }

    public void setHistoryPoint() {
        try {
            ArrayList<Edge> edges = pg.getEdges();
            ArrayList<Node> nodes = pg.getNodes();
            ArrayList<Object> ob = new ArrayList<>();
            ob.add(edges);
            ob.add(nodes);
            System.out.println("Vkládám do historie");
            ArrayList<Object> original = (ArrayList<Object>) Loader.deepCopy(ob);
            history.add(original);
            System.out.println("Size = " + history.size());
        } catch (Exception ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void retrieveHistory(int historyPoint) {
        try {
            ArrayList<Object> original;
            original = (ArrayList<Object>) Loader.deepCopy(history.get(historyPoint));
            ArrayList<Edge> edges = (ArrayList< Edge>) original.get(0);
            ArrayList<Node> nodes = (ArrayList< Node>) original.get(1);
            pg.setEdges(edges);
            pg.setNodes(nodes);
            System.out.println(history.size());
            repaint();
        } catch (Exception ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void retrieveOriginal() {
        try {
            while (history.size() > 1) {
                history.pop();
            }
            System.out.println("Retrieving original");
            System.out.println(history.size());
            ArrayList<Object> original = (ArrayList<Object>) Loader.deepCopy(history.get(0));

            ArrayList<Edge> edges = (ArrayList< Edge>) original.get(0);
            ArrayList<Node> nodes = (ArrayList< Node>) original.get(1);
            pg.setEdges(edges);
            pg.setNodes(nodes);
            System.out.println(history.size());
            repaint();
        } catch (Exception ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
