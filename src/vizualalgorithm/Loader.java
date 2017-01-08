/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Papi
 */
public class Loader {

    private String filePath = new File("").getAbsolutePath() + "\\src\\Assets\\";

    private Loader() {
    }

    static public Object deepCopy(Object oldObj) throws Exception {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(oldObj);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bin);
            return ois.readObject();
        } catch (Exception e) {
            System.out.println("Exception in ObjectCloner = " + e);
            throw (e);
        } finally {
            oos.close();
            ois.close();
        }
    }

    static public void loadGraph(PanelGraph pg, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (
                InputStream is = new FileInputStream(file);
                InputStream buffer = new BufferedInputStream(is);
                ObjectInput oi = new ObjectInputStream(buffer);) {
            ArrayList<Edge> edges = (ArrayList<Edge>) oi.readObject();
            ArrayList<Node> nodes = (ArrayList<Node>) oi.readObject();
            pg.setEdges(edges);
            pg.setNodes(nodes);
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void saveGraph(PanelGraph pg, String filePath) {
        File file = new File(filePath);
        try (
                OutputStream os = new FileOutputStream(file);
                OutputStream bos = new BufferedOutputStream(os);
                ObjectOutput output = new ObjectOutputStream(bos);) {
            ArrayList<Edge> edges = pg.getEdges();
            ArrayList<Node> nodes = pg.getNodes();
            output.writeObject(edges);
            output.writeObject(nodes);
        } catch (IOException ex) {

        }

    }
}
