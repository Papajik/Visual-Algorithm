/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Papi
 */
public class DatabaseConnector extends JFrame {

    private JList<String> jlSave_List, jlLoad_List;
    private DefaultListModel<String> dlLoad_List;
    private JPanel cards, loader, saver;
    private JButton loaderLoad, loaderCancel;
    private JButton saverSave, saverCancel;
    private Statement st;
    private ResultSet rs;
    private JTextField saveName;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public DatabaseConnector() {
        Container pane = getContentPane();
        cards = new JPanel(new CardLayout());
        saver = new JPanel();
        loader = new JPanel();
        cards.add(saver, "saver");
        cards.add(loader, "loader");
        pane.add(cards);
        setConnection();
        setLoader();
        setSaver();
        setButtons();
        setVisible(true);
    }

    private void setConnection() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mariadb://localhost/Graphs", "root", "sqlserverpsw");
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean load() {
        try {
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, "loader");
            String query = "select zadane_jmeno, pridelene_jmeno from jmena_grafu";
            rs = st.executeQuery(query);
            ArrayList<String> graphNames = new ArrayList<String>();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return false;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    private void setLoader() {
        ScrollPane spLoad_List = new ScrollPane();
        dlLoad_List = new DefaultListModel<>();
        jlLoad_List = new JList<>(dlLoad_List);
     
        jlLoad_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spLoad_List.add(jlLoad_List);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        loaderLoad = new JButton("Načíst");
        loaderCancel = new JButton("Zrušit");
        buttons.add(loaderLoad);
        buttons.add(loaderCancel);
        loader.add(jlLoad_List);
        loader.add(buttons);

    }

    public void save(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, "saver");
        this.nodes = nodes;
        this.edges = edges;
        pack();

    }

    private void setSaver() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Zadejte jméno pro vaši databázi"));
        saveName = new JTextField(30);
        saveName.setMaximumSize(saveName.getPreferredSize());
        panel.add(saveName);
        saverSave = new JButton("Uložit");
        saverCancel = new JButton("Zrušit");
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        panel.add(buttons);
        buttons.add(saverSave);
        buttons.add(saverCancel);

        saver.add(panel);

    }

    private void setButtons() {
        saverCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        saverSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int nodesCount = nodes.size();
                    int edgesCount = edges.size();
                    if (saveName.getText().equals("")) {
                        return;
                    }
                    //select zadane_jmeno from jmena_grafu

                    String query = "INSERT INTO jmena_grafu (id, zadane_jmeno,pridelene_jmeno) VALUES (null,\"" + saveName.getText() + "\",\"" + nodesCount + ":" + edgesCount + "\")";
                    st.executeQuery(query);
                    query = "select id from jmena_grafu where zadane_jmeno = \"" + saveName.getText() + "\"";
                    rs = st.executeQuery(query);
                    int graphID = -1;
                    if (rs.next()) {
                        graphID = Integer.parseInt(rs.getString(1));
                    }
                    ArrayList<Integer> nodesID = new ArrayList<>();
                    for (Node n : nodes) {
                        query = "insert into node (id, graf_id, x, y, name) VALUES (" + null + ",\"" + graphID + "\",\"" + n.getX() + "\",\"" + n.getY() + "\",\"" + n.getName() + "\");";
                        st.execute(query);

                    }
                    query = "select id from node where graf_id =" + graphID;

                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        nodesID.add(Integer.parseInt(rs.getString(1)));
                    }
                    for (Edge edge : edges) {
                        int i = getIndexOfNode(edge.getFrom());
                        int fromID = nodesID.get(i);
                        i = getIndexOfNode(edge.getTo());
                        int toID = nodesID.get(i);
                        query = "insert into edge (id, from_id, to_id, oriented, length) VALUES (null," + fromID + "," + toID + ",\"" + edge.isOriented() + "\",\"" + edge.getLength() + "\")";
                        st.executeQuery(query);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private int getIndexOfNode(Node n) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).equals(n)) {
                return i;
            }
        }
        return -1;
    }

    private String[] getNamesOfFiles() {
        return null;
    }

    public ArrayList<Node> getNodes() {
        return null;
    }

    public ArrayList<Edge> getEdges() {
        return null;
    }
}
