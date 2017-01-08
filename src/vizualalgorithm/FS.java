/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;

/**
 *
 * @author Papi
 */
public class FS implements Runnable {

    private Algorithm alg;
    private Node finish;
    private LinkedList<Node> queue;
    private Stack<Node> stack;
    private Node last;
    private final Object lock;
    private boolean run = true;
    private boolean first = true;
    private boolean skip = false;
    private boolean mode;

    public Node getFinish() {
        return finish;
    }

    public LinkedList<Node> getQueue() {
        return queue;
    }

    public Stack<Node> getStack() {
        return stack;
    }

//    public void setQueue(LinkedList<Node> queue) {
//        this.queue = queue;
//    }
//    public void setStack(Stack<Node> stack) {
//        this.stack = stack;
//    }
    public Object getLock() {
        return lock;
    }

    public void setRun(boolean x) {
        run = x;
    }

    public void setSkip(boolean x) {
        skip = x;
    }

    /**
     * Pro používání historie procházení Obnoví se poslední procházený prvek v
     * dané verzi procházení
     *
     * @param n
     */
    public void setLast(Node n) {
        last = n;
    }

    /**
     * Pro používání historie procházení Obnoví se hleadný prvek v dané verzi
     * procházení
     *
     * @param n
     */
    public void setFinish(Node n) {
        finish = n;
    }

    public Node getLast() {
        return last;
    }

    /**
     *
     * @param alg
     * @param mode True = nastavit frontu False = nastavit zásobník
     */
    public FS(Algorithm alg, boolean mode) {
        this.mode = mode;
        this.alg = alg;
        queue = new LinkedList<>();
        stack = new Stack<>();
        alg.load();
        lock = new Object();
    }

    @Override
    public void run() {

        boolean found = false;
        if (first) {
            finish = alg.getFinish();
            if (finish != null) {
                System.out.println("DFS: hledaný - " + finish.getName());
            }
            Node start = alg.getStart();
            addCollection(start);
            start.setVisited(true);
            last = start;
            first = false;
        }

        while (!isCollectionEmpty()) {

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

            Node n = withdrawFromCollection();
            last.setSelected(false);
            n.setSelected(true);
            if (n.equals(finish)) {
                alg.write("Nalezen hledaný bod", n);
                paintRightPath();
                found = true;
                break;
            }
            Edge tempEdge = n.getPathBack();
            if (tempEdge != null) {
                tempEdge.setVisited(true);
            }

            last = n;
            n.setStacked(false);
            alg.write("Procházím bod", n);
            delay();
            alg.setHistoryPoint();
            ArrayList<Edge> edges = n.getOutcome();
            for (Edge e : edges) {
                alg.repaint();
                alg.write("Procházím "+e.toString(), n);
                if (e.getKnown()) {
                    continue;
                }
                e.setKnown(true);
                Node act = e.getTo();
                if (!act.isVisited()) {
                    act.setVisited(true);
                    act.setStacked(true);
                    act.addPathBack(e);
                    addCollection(act);
                }
                act = e.getFrom();
                if (!act.isVisited()) {
                    act.setVisited(true);
                    act.setStacked(true);
                    act.addPathBack(e);
                    addCollection(act);
                }
                 
                alg.setHistoryPoint();
                alg.repaint();
                delay();
            }
            n.setSelected(false);
        }
        if (finish == null) {
            alg.write("Prošli jsme celý dostupný graf", null);
        } else if (!found) {
            alg.write("Neexistuje žádná cesta", null);
        }
        alg.setHistoryPoint();
        alg.threadStoped();
    }

    private void addCollection(Node e) {
        if (mode) {
            queue.add(e);
        } else {
            stack.add(e);
        }
    }

    private Node withdrawFromCollection() {
        if (mode) {
            return queue.poll();
        } else {
            return stack.pop();
        }
    }

    private boolean isCollectionEmpty() {
        if (mode) {
            return queue.isEmpty();
        } else {
            return stack.isEmpty();
        }
    }

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

    private void paintRightPath() {
        Node act = finish;
        while (act.getPathBack() != null) {
            Edge n = act.getPathBack();
            n.setBestPath(true);

            act = n.getOposite(act);
        }
    }
}
