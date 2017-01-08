/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author Papi
 */
public class Window extends JFrame {

    JMenuBar menuBar;
    JMenu menuNew, menuLoad, menuSave, menuSettings;
    JMenuItem itNewGraph;
    //, itNewTree, itNewField;
    JMenuItem itLoadOfGraph, itLoadDaGraph;
    //itLoadOfTree, itLoadOfField, itLoadDaGraph, itLoadDaTree, itLoadDaField;
    //JMenu LoadDatabase, LoadOffline;
    JMenuItem itSaveDatabase, itSaveOffline;
    JMenuItem itSetLanguage;
    JPanel cards;
    PanelTree panelTree;
    PanelGraph panelGraph;
    Panel2DField panelField;
    PanelChooser panelChooser;

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
//        itNewTree = new JMenuItem("Strom");
//        itNewField = new JMenuItem("2D pole");
//
//        LoadDatabase = new JMenu("Z databáze");
//        LoadOffline = new JMenu("Z počítače");

//        itLoadOfField = new JMenuItem("2D pole");
        itLoadOfGraph = new JMenuItem("Graf");
//        itLoadOfTree = new JMenuItem("Strom");

//        itLoadDaField = new JMenuItem("2D pole");
        itLoadDaGraph = new JMenuItem("Graf");
//        itLoadDaTree = new JMenuItem("Strom");

        itSaveDatabase = new JMenuItem("Do databáze");
        itSaveOffline = new JMenuItem("Na disk");

//        itLoadOfField = new JMenuItem("2D pole");
        itLoadOfGraph = new JMenuItem("Z počítače");
//        itLoadOfTree = new JMenuItem("Strom");

//        itLoadDaField = new JMenuItem("2D pole");
        itLoadDaGraph = new JMenuItem("Z databáze");
//        itLoadDaTree = new JMenuItem("Strom");

        itSetLanguage = new JMenuItem("Jazyk");

        menuBar.add(menuNew);
        menuBar.add(menuLoad);
        menuBar.add(menuSave);
        menuBar.add(menuSettings);

        menuNew.add(itNewGraph);
//        menuNew.add(itNewField);
//        menuNew.add(itNewTree);

//        menuLoad.add(LoadOffline);
//        menuLoad.add(LoadDatabase);
        menuLoad.add(itLoadOfGraph);
//        LoadOffline.add(itLoadOfGraph);
//        LoadOffline.add(itLoadOfField);
//        LoadOffline.add(itLoadOfTree);
        menuLoad.add(itLoadDaGraph);
//        LoadDatabase.add(itLoadDaGraph);
//        LoadDatabase.add(itLoadDaField);
//        LoadDatabase.add(itLoadDaTree);

        menuSave.add(itSaveOffline);
        menuSave.add(itSaveDatabase);

    }

    private void setCards() {
        Container pane = getContentPane();
        cards = new JPanel(new CardLayout());
        pane.add(cards);
        panelField = new Panel2DField();
        panelTree = new PanelTree();
        panelGraph = new PanelGraph();
        panelChooser = new PanelChooser(this);
        cards.add(panelChooser, "chooser");
        cards.add(panelGraph, "graph");
        cards.add(panelTree, "tree");
        cards.add(panelField, "field");

    }

    private void setMenuButtons() {

        itNewGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.remove(panelGraph);
                cards.add(new PanelGraph(), "graph");
                setPanel(1);
                System.out.println("Graph");
            }
        });

//        itNewField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setPanel(2);
//                System.out.println("Field");
//            }
//        });
//
//        itNewTree.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setPanel(3);
//                System.out.println("Tree");
//            }
//        });
    }

    public void setPanel(int p) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        switch (p) {
            case 1:
                cardLayout.show(cards, "graph");
                break;
            case 2:
                cardLayout.show(cards, "field");
                break;
            case 3:
                cardLayout.show(cards, "tree");
                break;

        }
    }

}
