/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;

/**
 *
 * @author Papi
 */
public class Dijkstra implements Runnable {

    private AlgorithmPanel alg;
    private final Queue<Node> nodeList;
    private final Comparator<Node> comparator;
    private Node finish;
    private boolean skip = false;
    private boolean run = true;
    private Object lock;

    public void setSkip(boolean b) {
        skip = b;
    }

    public void setRun(boolean b) {
        run = b;
    }

    public Object getLock() {
        return lock;
    }

    public Dijkstra(AlgorithmPanel alg) {
        this.alg = alg;
        //Vlastní comparator na setřídění vrcholů podle váhy cest k němu
        comparator = (Node o1, Node o2) -> {
            if (o1.getPathCost() > o2.getPathCost()) {
                return 1;
            } else {
                return -1;
            }
        };
        nodeList = new PriorityQueue<>(comparator);
        lock = new Object();
        alg.load();
    }

    @Override
    public void run() {
        Node start = alg.getStart();
        start.setPathCost(0);
        nodeList.add(start);
        finish = alg.getFinish();
        if (finish != null) {
            System.out.println("DIJ: hledaný - " + finish.getName());
        }
        alg.setHistoryPoint();
        while (!nodeList.isEmpty()) {
            checkPause();
            Node node = nodeList.poll();
            //nastavý cestu, přes kterou jsme k bodu přišli na prohledanou
            if (node.getPathBack() != null) {
                node.getPathBack().setVisited(true);
            }
            //nastaví bod na aktuální
            node.setSelected(true);
            alg.repaint();
            ArrayList<Edge> edges = node.getOutcome();
            delay();
            for (Edge e : edges) {
                delay();
                Node n = e.getOposite(node);
                if (n.isVisited()) {
                    continue;
                }
                e.setKnown(true);
                alg.repaint();
                //přirazení kratší cesty

                int newPathCost = node.getPathCost() + e.getLength();
                if (n.getPathCost() > newPathCost) {
                    n.setPathCost(newPathCost);
                    n.addPathBack(e);
                }
                //Zařazení nově vypočítané cesty do setříděné fronty
                if (nodeList.contains(n)) {
                    nodeList.remove(n);
                    nodeList.add(n);
                } else {
                    nodeList.add(n);
                }
                alg.repaint();
                delay();
                //alg.setHistoryPoint();
                e.setKnown(false);
                alg.setHistoryPoint();
                alg.repaint();

            }

            node.setSelected(false);
            node.setVisited(true);
            alg.repaint();
        }
        paintRightPath();
        alg.setHistoryPoint();
        alg.threadStopped();
    }

    /**
     * Zkontroluje, jestli uživatel neurychlil algoritmus. Pokud ne, program se
     * na 500 vteřin zastaví
     *
     */
    private void delay() {
        try {
            if (!skip) {
                sleep(500);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(FS.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zvýrazní hledanou cestu
     */
    private void paintRightPath() {
        Node act = finish;
        if (finish == null){
            return;
        }
        while (act.getPathBack() != null) {
            Edge n = act.getPathBack();
            n.setBestPath(true);

            act = n.getOposite(act);
        }
    }

    /**
     * Zkontoluje, zda uživatel nevyvolal pozastavení algoritmu
     */
    private void checkPause() {
        if (!run) {
            run = true;
            System.out.println("Paused");
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FS.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

}
