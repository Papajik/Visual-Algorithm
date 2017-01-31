/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Papi
 */
public class Window extends JFrame {

    JMenuBar menuBar;
    JMenu menuNew, menuLoad, menuSave, menuSettings;
    JMenuItem itNewGraph;
    JMenuItem itLoadOfGraph, itLoadDaGraph;
    JMenuItem itSaveDatabase, itSaveOffline;
    JMenuItem itSetLanguage;
    JPanel cards;
    PanelGraph panelGraph;

    public Window() {
        setWindow();
        setName("Okno");
    }

    private void setWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMenu();

        setCards();
        setMenuButtons();
        setSize(new Dimension(1366, 768));
    }

    public void setVisible() {
        setVisible(true);
    }

    private void setMenu() {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuNew = new JMenu("Nový");
        menuLoad = new JMenu("Otevřít");
        menuSave = new JMenu("Uložit");
        menuSettings = new JMenu("Nastavení");

        itNewGraph = new JMenuItem("Graf");
        itLoadOfGraph = new JMenuItem("Graf");
        itLoadDaGraph = new JMenuItem("Graf");
        itSaveDatabase = new JMenuItem("Do databáze");
        itSaveOffline = new JMenuItem("Na disk");
        itLoadOfGraph = new JMenuItem("Z počítače");
        itLoadDaGraph = new JMenuItem("Z databáze");

        itSetLanguage = new JMenuItem("Jazyk");

        menuBar.add(menuNew);
        menuBar.add(menuLoad);
        menuBar.add(menuSave);
        menuBar.add(menuSettings);

        menuNew.add(itNewGraph);
        menuLoad.add(itLoadOfGraph);
        menuLoad.add(itLoadDaGraph);

        menuSave.add(itSaveOffline);
        menuSave.add(itSaveDatabase);

    }

    private void setCards() {
        Container pane = getContentPane();
        cards = new JPanel(new CardLayout());
        pane.add(cards);
        panelGraph = new PanelGraph();
        cards.add(panelGraph, "graph");

    }

    private void setMenuButtons() {

        itNewGraph.addActionListener((ActionEvent e) -> {
            cards.remove(panelGraph);
            panelGraph = new PanelGraph();
            cards.add(panelGraph, "graph");
            setPanel(1);
            System.out.println("Graph");
        });

        itLoadOfGraph.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(new File("").getAbsolutePath() + "\\src\\Saves\\"));

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().getName();
                String path = chooser.getCurrentDirectory().toString();
                Loader.loadGraph(panelGraph, path + "\\" + file);
                panelGraph.repaint();
            }
        });

        itSaveOffline.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(new File("").getAbsolutePath() + "\\src\\Saves\\"));
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().getName();
                String path = chooser.getCurrentDirectory().toString();
                Loader.saveGraph(panelGraph, path + "\\" + file);
            }
        });

        itLoadDaGraph.addActionListener((ActionEvent e) -> {
            DatabaseConnector con = new DatabaseConnector(this);
            con.load();

        });

        itSaveDatabase.addActionListener((ActionEvent e) -> {
            DatabaseConnector con = new DatabaseConnector(this);
            con.save(panelGraph.getNodes(), panelGraph.getEdges());

        });
    }

    public void setGraph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        cards.remove(panelGraph);
        panelGraph = new PanelGraph();
        cards.add(panelGraph, "graph");
        panelGraph.setNodes(nodes);
        panelGraph.setEdges(edges);
        panelGraph.paintComponent();
        setPanel(1);
        setVisible(true);
    }

    public void setPanel(int p) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        switch (p) {
            case 1:
                cardLayout.show(cards, "graph");
                break;
            case 2:
        }
    }

}
