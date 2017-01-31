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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Papi
 */
public class AlgorithmPanel {
/**
 * Startovní bod algoritmu
 */
    private Node start;
    /**
     * Cílový bod algoritmu
     */
    private Node finish;
    private final PanelGraph pg;
    private JToolBar runBar;
    private JTextField output, name, step;
    private JComboBox combo;
    private Object lock;
    private FS fs = null;
    private Dijkstra dij = null;
    private final Stack<Object> history;
    private int historyPoint = 0;
/**
 * Nastaví startovní bod algoritmu, pokud je již nastavený jiný bod, je tento bod 
 * @param n 
 */
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

    public AlgorithmPanel(PanelGraph pg) {
        this.pg = pg;
        history = new Stack<>();
        setHistoryPoint();
        setRunOptions();
        createRunBar();

    }

    public void write(String t, Node n) {
        output.setText(t);
        updateNodeName(n);
    }

    public void repaint() {
        pg.paintImmediately(0, 0, pg.getWidth(), pg.getHeight());
    }

    /**
     * Načte a zinicializuje graf pro následné prohledávání
     */
    public void load() {

        ArrayList<Edge> edges = pg.getEdges();
        ArrayList<Node> nodes = pg.getNodes();
        for (Node n : nodes) {
            n.initialize();
            for (Edge e : edges) {
                if (e.isOriented()) {
                    if (e.getFrom().equals(n)) {
                        n.addOutcome(e);
                    }
                } else if (e.getFrom().equals(n) || e.getTo().equals(n)) {
                    n.addOutcome(e);
                }
            }
        }
    }

    /**
     * Spustí algoritmus prohledávání podle zadaného typu
     *
     * @param choice
     */
    public void start(String choice) {
        switch (choice) {
            case "Prohledávání do hloubky":

                if (lock == null) {
                    pg.deselectAll();
                    fs = new FS(this, false);
                    lock = fs.getLock();
                    new Thread(fs).start();
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
            case "Dijkstrův algoritmus": {
                if (lock == null) {
                    pg.deselectAll();
                    pg.setDijkstra(true);
                    dij = new Dijkstra(this);
                    lock = dij.getLock();
                    new Thread(dij).start();
                }
                break;
            }
            case "MultiThread prohledávání do šířky": {
                JFrame question = new JFrame("Počet jader");
                question.setLocationRelativeTo(null);
                Container con = question.getContentPane();
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                JLabel label = new JLabel("Zadejte počet vláken procesoru");
                panel.add(label);
                JTextField text = new JTextField();
                text.setMaximumSize(label.getMaximumSize());
                text.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(text);
                JPanel buttons = new JPanel();
                buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
                JButton ok = new JButton("Ok");
                JButton cancel = new JButton("Cancel");
                buttons.add(ok);
                buttons.add(cancel);
                buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(buttons);
                con.add(panel);
                question.pack();
                question.setVisible(true);

                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int threads = 0;
                        try {
                            threads = Integer.parseInt(text.getText());
                            MultiThreading mt = new MultiThreading(threads, start, finish);
                        } catch (NumberFormatException ex) {
                            text.setText("");
                        }
                        question.setVisible(false);
                    }
                });

                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        question.setVisible(false);
                    }
                });
                break;
            }
        }
    }
/**
 * Pozastavení běžícího vlákna
 */
    public void pauseRunningThread() {
        if (fs != null) {
            fs.setRun(false);
        }
        if (dij != null) {
            dij.setRun(false);
        }
    }

    /**
     * Pokud vlákno skončí, zavolá se tato metoda, která vyklidí proměnné pro
     * další spuštění algoritmu
     */
    public void threadStopped() {
        lock = null;
        fs = null;
        dij = null;
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

    /**
     * Upraví zobrazované jméno v postraním panelu
     *
     * @param n
     */
    public void updateNodeName(Node n) {
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
        combo.addItem("Dijkstrův algoritmus");
        combo.addItem("MultiThread prohledávání do šířky");
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
            if (isStopped() && history.size() >= 2) {
                historyPoint = 1;
                retrieveHistory(historyPoint);
                step.setText(historyPoint + "/" + (history.size() - 1));
            }
        });
        jbNext.addActionListener((ActionEvent e) -> {
            if (isStopped()) {
                if (historyPoint < history.size() - 1) {
                    historyPoint++;
                    retrieveHistory(historyPoint);
                    step.setText(historyPoint + "/" + (history.size() - 1));
                }
            }
        });
        jbBack.addActionListener((ActionEvent e) -> {
            if (isStopped()) {
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
                if (fs != null) {
                    fs.setRun(true);
                }
                if (dij != null) {
                    dij.setRun(true);
                }

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
 * Zkontroluje stav vláken. True, pokud nějaké již běží
 * @return 
 */
    private boolean isStopped() {
        return (fs == null && dij == null);
    }

/**
 * Nastaví bod historie v procházení grafu
 */
    public void setHistoryPoint() {
        try {
            ArrayList<Edge> edges = pg.getEdges();
            ArrayList<Node> nodes = pg.getNodes();
            ArrayList<Object> ob = new ArrayList<>();
            ob.add(edges);
            ob.add(nodes);
            ArrayList<Object> original = (ArrayList<Object>) Loader.deepCopy(ob);
            history.add(original);
        } catch (Exception ex) {
            Logger.getLogger(AlgorithmPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * Načte bod historie z procházeného grafu
 * @param historyPoint 
 */
    private void retrieveHistory(int historyPoint) {
        try {
            ArrayList<Object> original;
            original = (ArrayList<Object>) Loader.deepCopy(history.get(historyPoint));
            ArrayList<Edge> edges = (ArrayList< Edge>) original.get(0);
            ArrayList<Node> nodes = (ArrayList< Node>) original.get(1);
            pg.setEdges(edges);
            pg.setNodes(nodes);
            repaint();
        } catch (Exception ex) {
            Logger.getLogger(AlgorithmPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
