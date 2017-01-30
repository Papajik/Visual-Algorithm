/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Papi
 */
public class DatabaseConnector extends JFrame {

    private static String TABLE_GRAPH = "graf";
    private static String ATT_GRAPH_NAME = "jmeno";
    private static String ATT_GRAPH_NODES = "vrcholy";
    private static String ATT_GRAPH_EDGES = "hrany";

    private Window win;
    private JList<String> jlLoad_List;
    private JPanel cards, loader, saver;
    private JTable table;
    private JButton loaderLoad, loaderCancel;
    private JButton saverSave, saverCancel;
    private Statement st;
    private PreparedStatement pSt;
    private Connection con;
    private ResultSet rs;
    private JTextField saveName;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private int selectedID = -1;

    public DatabaseConnector(Window win) {
        super.setTitle("Connection with database");
        this.win = win;
        Container pane = getContentPane();
        cards = new JPanel(new CardLayout());
        saver = new JPanel();
        loader = new JPanel(new BorderLayout());
        cards.add(saver, "saver");
        cards.add(loader, "loader");
        pane.add(cards);
        setConnection();
        setLoader();
        setSaver();
        setButtons();

    }

    private void setConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mariadb://localhost/Graphs", "root", "sqlserverpsw");
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        try {
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, "loader");
            pack();
            String query = "select * from " + TABLE_GRAPH;
            rs = st.executeQuery(query);
            int i = 0;
            Object[] ob = new Object[rs.getMetaData().getColumnCount()];
            DefaultTableModel def = (DefaultTableModel) table.getModel();
            while (rs.next()) {
                for (int j = 1; j <= table.getColumnCount(); j++) {
                    ob[j - 1] = rs.getString(j);
                }
                def.addRow(ob);
                i++;
            }
            setVisible(true);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    private void setLoader() {

        JScrollPane scrollPane = new JScrollPane();
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        ;
        };
        Object[] columnNames = getColumnNames();
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table.setAutoCreateRowSorter(true);
        table.setModel(tableModel);
        scrollPane.setViewportView(table);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        loaderLoad = new JButton("Načíst");
        loaderCancel = new JButton("Zrušit");
        buttons.add(loaderLoad);
        buttons.add(loaderCancel);

        loader.add(scrollPane);
        loader.add(buttons, BorderLayout.SOUTH);
        pack();

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting()) {
                selectedID = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            }
        });
    }

    private Object[] getColumnNames() {
        try {
            String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'Graphs' AND table_name = 'graf'";
            rs = st.executeQuery(query);
            rs.next();
            int columnCount = Integer.parseInt(rs.getString(1));
            query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'Graphs' AND TABLE_NAME = 'graf';";
            rs = st.executeQuery(query);
            Object[] ob = new Object[columnCount];

            for (int i = 0; i < columnCount; i++) {
                rs.next();
                System.out.println(rs.getString(1));
                ob[i] = rs.getString(1);
            }
            return ob;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public void save(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, "saver");
        setVisible(true);
        pack();
        this.nodes = nodes;
        this.edges = edges;

    }

    private void setSaver() {
        saver.setLayout(new BoxLayout(saver, BoxLayout.Y_AXIS));
        saver.add(new JLabel("Zadejte jméno pro vaši databázi"));
        saveName = new JTextField(30);
        saveName.setMaximumSize(saveName.getPreferredSize());
        saver.add(saveName);
        saverSave = new JButton("Uložit");
        saverCancel = new JButton("Zrušit");
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(saverSave);
        buttons.add(saverCancel);
        saver.add(buttons);
        pack();

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
                    String name = saveName.getText();
                    if (name == null) {
                        return;
                    }
                    if (isNameDuplicated(name)) {
                        JOptionPane.showMessageDialog(saver, "This name already exists in the database. Please select different name");
                        return;
                    }
                    int nodesCount = nodes.size();
                    int edgesCount = edges.size();
                    if (saveName.getText().equals("")) {
                        return;
                    }

                    pSt = con.prepareStatement("INSERT INTO " + TABLE_GRAPH + " (id, " + ATT_GRAPH_NAME + "," + ATT_GRAPH_NODES + ", " + ATT_GRAPH_EDGES + ") "
                            + "VALUES (null,?,\"" + nodesCount + "\",\"" + edgesCount + "\")");
                    pSt.setString(1, saveName.getText());
                    pSt.executeUpdate();
                    pSt = con.prepareStatement("select id from " + TABLE_GRAPH + " where " + ATT_GRAPH_NAME + " = ?;");
                    pSt.setString(1, saveName.getText());
                    rs = pSt.executeQuery();
                    int graphID = -1;
                    if (rs.next()) {
                        graphID = Integer.parseInt(rs.getString(1));
                    }
                    ArrayList<Integer> nodesID = new ArrayList<>();
                    for (Node n : nodes) {
                        pSt = con.prepareStatement("insert into node (id, graf_id, x, y, name) "
                                + "VALUES (" + null + ",\"" + graphID + "\",?,?,?);");
                        pSt.setInt(1, n.getX());
                        pSt.setInt(2, n.getY());
                        pSt.setString(3, n.getName());
                        pSt.executeQuery();

                    }
                    pSt = con.prepareStatement("select id from node where graf_id =" + graphID);
                    rs = pSt.executeQuery();
                    while (rs.next()) {
                        nodesID.add(Integer.parseInt(rs.getString(1)));
                    }
                    for (Edge edge : edges) {
                        int i = getIndexOfNode(edge.getFrom());
                        int fromID = nodesID.get(i);
                        i = getIndexOfNode(edge.getTo());
                        int toID = nodesID.get(i);
                        pSt = con.prepareStatement("insert into edge (id, from_id, to_id, oriented, length) "
                                + "VALUES (null," + fromID + "," + toID + ",?,?)");
                        pSt.setString(1, "" + edge.isOriented());
                        pSt.setInt(2, edge.getLength());
                        pSt.executeQuery();
                    }
                    JOptionPane.showMessageDialog(saver, "Graph saved");

                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseConnector.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        loaderCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }

        });

        loaderLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedID >= 0) {
                    try {
                        ArrayList<Integer> nodeID = new ArrayList<>();
                        ArrayList<Integer> nodeXPos = new ArrayList<>();
                        ArrayList<Integer> nodeYPos = new ArrayList<>();
                        ArrayList<String> nodeNames = new ArrayList<>();

                        String query = "select * from node where graf_id =" + selectedID;
                        rs = st.executeQuery(query);

                        while (rs.next()) {
                            nodeID.add(Integer.parseInt(rs.getString(1)));
                            nodeXPos.add(Integer.parseInt(rs.getString(3)));
                            nodeYPos.add(Integer.parseInt(rs.getString(4)));
                            nodeNames.add(rs.getString(5));
                        }
                        ArrayList<Node> nodes = new ArrayList<>();

                        for (int i = 0; i < nodeID.size(); i++) {
                            nodes.add(new Node(nodeXPos.get(i), nodeYPos.get(i), nodeNames.get(i)));
                        }

                        query = "select edge.from_id, edge.to_id, edge.oriented, edge.length from edge "
                                + "inner join node on edge.from_id =node.ID "
                                + "inner join " + TABLE_GRAPH + " on " + TABLE_GRAPH + ".id = node.graf_id "
                                + "where node.graf_id =" + selectedID;
                        rs = st.executeQuery(query);
                        ArrayList<Edge> edges = new ArrayList<>();
                        while (rs.next()) {
                            System.out.println("------------");
                            int fromID = Integer.parseInt(rs.getString(1));
                            System.out.println(fromID);
                            int toID = Integer.parseInt(rs.getString(2));
                            System.out.println(toID);
                            boolean oriented = Boolean.parseBoolean(rs.getString(3));
                            System.out.println(oriented);
                            int length = Integer.parseInt(rs.getString(4));
                            System.out.println(length);
                            Node from = null;
                            Node to = null;
                            for (int i = 0; i < nodeID.size(); i++) {
                                if (fromID == nodeID.get(i)) {

                                    from = nodes.get(i);
                                    System.out.println(from.getName());
                                }
                                if (toID == nodeID.get(i)) {
                                    to = nodes.get(i);
                                    System.out.println(to.getName());
                                }

                            }
                            edges.add(new Edge(from, to, oriented, length));
                        }
                        System.out.println(edges.size());
                        win.setGraph(nodes, edges);

                    } catch (SQLException ex) {
                        Logger.getLogger(DatabaseConnector.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

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

    private boolean isNameDuplicated(String name) {
        try {
            System.out.println("Zadané jméno: " + name);
            rs = st.executeQuery("select " + ATT_GRAPH_NAME + " from " + TABLE_GRAPH);
            while (rs.next()) {
                String compare = rs.getString(1);
                System.out.println("Comparing: " + compare);
                if (name.equals(compare)) {
                    return true;
                }
            }
            return false;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnector.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
