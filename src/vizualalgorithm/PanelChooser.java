/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.scene.layout.Border;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Papi
 */
public class PanelChooser extends JPanel {

    private JButton jbTree, jbGraph, jbField;
    private Window win;

    public PanelChooser(Window win) {
        initComponents();
        this.win = win;
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        jbGraph = new JButton("Graph");
        jbField = new JButton("Field");
        jbTree = new JButton("Tree");
        this.add(jbGraph);
        this.add(jbField);
        this.add(jbTree);

        setListeners();
    }

    private void setListeners() {
        jbGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.setPanel(1);
                win.repaint();
            }
        });
        jbField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.setPanel(2);
                win.repaint();
            }
        });
        jbTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.setPanel(3);
                win.repaint();
            }
        });
    }

  

}
