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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
public class Algorithm {

    private Node start;
    private Node finish;
    private PanelGraph pg;
    private JToolBar runBar;
    private JTextField output, name;
    private JComboBox combo;
    private Object lock;
    private DFS dfs = null;
    //private BFS bfs = null;

    public void setStart(Node n) {
        if (start != null) {
            start.setStart(false);
        }
        if (n != null) {
            n.setStart(true);
        }
        start = n;
        start.setFinish(false);
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
        }

        finish = n;
        finish.setStart(false);
    }

    public Node getStart() {
        return start;
    }

    public Node getFinish() {
        return finish;
    }

    public Algorithm(PanelGraph pg) {
        this.pg = pg;
        setRunOptions();
        createRunBar();
    }

    public void write(String t, Node n) {
        output.setText(t);
        updateNode(n);
        pg.getRightPanel().repaint();

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
                dfs = new DFS(this);
                lock = dfs.getLock();
                System.out.println("Spouštím vlákno");
                new Thread(dfs).start();
                System.out.println("Opouštím vlákno");
                break;
        }
    }

    public void pauseRunningThread() {
        if (dfs != null) {
            System.out.println("SETING PAUSE");
            dfs.setRun(false);
        }
    }

    private void setRunOptions() {
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        JLabel step = new JLabel("Aktuální krok");

        output = new JTextField(15);
        output.setMaximumSize(output.getPreferredSize());
        output.setEditable(false);
        JLabel chosed = new JLabel("Vybraný bod");
        name = new JTextField(15);
        name.setMaximumSize(name.getPreferredSize());
        name.setEditable(false);
        JButton setStart = new JButton("Nastav start");
        setStart.setBackground(new Color(240, 240, 0));
        JButton setEnd = new JButton("Nastav konec");
        setEnd.setBackground(new Color(255, 121, 77));

        options.add(step);
        options.add(output);
        options.add(chosed);
        options.add(name);
        options.add(setStart);
        options.add(setEnd);
        pg.getRightPanel().add(options, "options");

        setStart.addActionListener((ActionEvent e) -> {
            if (pg.getSelectedNode() != null) {
                setStart(pg.getSelectedNode());
            }
            pg.paintImmediately(0, 0, pg.getWidth(), pg.getHeight());
        });

        setEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFinish(pg.getSelectedNode());
                pg.paintImmediately(0, 0, pg.getWidth(), pg.getHeight());
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

    private void createRunBar() {
        runBar = new JToolBar("Průběh algoritmu");
        JButton jbBack = new JButton("Zpět");
        JButton jbNext = new JButton("Dopředu");
        JButton jbPause = new JButton("Pozastavit");
        JButton jbContinue = new JButton("Pokračovat");
        JButton jbStop = new JButton("Zastavit");
        JButton jbRun = new JButton("Spusť prohledávání");
        String filePath = new File("").getAbsolutePath() + "\\src\\Assets\\";
        jbNext.setIcon(new ImageIcon(filePath + "Forw.png"));
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
        jbNext.addActionListener((ActionEvent e) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
        jbBack.addActionListener((ActionEvent e) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
        jbPause.addActionListener((ActionEvent e) -> {
            pauseRunningThread();
        });
        jbContinue.addActionListener((ActionEvent e) -> {
            if (lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                }

            }
        });
        jbStop.addActionListener((ActionEvent e) -> {
            runBar.setVisible(false);
            pg.stopGenerating();

        });
        jbRun.addActionListener((ActionEvent e) -> {
            if (start != null) {
                start(combo.getSelectedItem().toString());
            }
        });
    }

//    private void algoritmus() {
//        while (!zbyvajiciBody.isEmpty()) {
//            int minimum = Integer.MAX_VALUE;
//            int index = 0;
//            for (Hrana hrana : seznamHran) {
//                if (hrana.getVaha() < minimum && ((prosleBody.contains(hrana.getDruhy()) && !prosleBody.contains(hrana.getPrvni())) || (!prosleBody.contains(hrana.getDruhy()) && prosleBody.contains(hrana.getPrvni())))) {
//                    minimum = hrana.getVaha();
//                    index = seznamHran.indexOf(hrana);
//                }
//            }
//            pouziteHrany.add(seznamHr   an.get(index)
//            );
//            System.out.print(seznamHran.get(index).getPrvni().name);
//            System.out.print(seznamHran.get(index).getDruhy().name + " ");
//            System.out.println(seznamHran.get(index).getVaha());
//            if (!prosleBody.contains(seznamHran.get(index).getPrvni())) {
//                prosleBody.add(seznamHran.get(index).getPrvni());
//                zbyvajiciBody.remove(seznamHran.get(index).getPrvni());
//            }
//            if (!prosleBody.contains(seznamHran.get(index).getDruhy())) {
//                prosleBody.add(seznamHran.get(index).getDruhy());
//                zbyvajiciBody.remove(seznamHran.get(index).getDruhy());
//            }
//            seznamHran.remove(seznamHran.get(index));
//
//        }
//
//        System.out.println("-----");
//        for (Hrana hrana : pouziteHrany) {
//            hrana.vypisHrana();
//        }
//
//    }
}
