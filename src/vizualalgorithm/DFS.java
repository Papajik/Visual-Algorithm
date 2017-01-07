/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Papi
 */
public class DFS implements Runnable {

    private Stack<Object[]> history;
    private Algorithm alg;
    private Node finish;
    private Stack<Node> stack;
    private Node last;
    private final Object lock;
    private boolean run = true;

    public Object getLock() {
        return lock;
    }

    public void setRun(boolean x) {
        run = x;
    }

    public DFS(Algorithm alg) {
        this.alg = alg;
        stack = new Stack<>();
        alg.load();
        lock = new Object();
    }

    @Override
    public void run() {
        finish = alg.getFinish();
        if (finish != null) {
            System.out.println("DFS: hledaný - " + finish.getName());
        }
        Node start = alg.getStart();
        stack.add(start);
        start.setVisited(true);
        last = start;
        while (!stack.isEmpty()) {
            System.out.println("1: " + stack.size());
            Node n = stack.pop();
            last.setSelected(false);
            n.setSelected(true);
            if (n.equals(finish)) {
                alg.write("Nalezen hledaný bod", n);
                paintRightPath();
                run = false;
                return;
            }
            Edge tempEdge = n.getPathBack();
            if (tempEdge != null) {
                System.out.println("Setting edge to truly visited");
                tempEdge.setVisited(true);
            } else {
                System.out.println("2: " + stack.size());
                System.out.println("Nemělo by být");
            }

            last = n;
            n.setStacked(false);
            alg.write("Procházím bod", n);
            // alg.repaint();
            System.out.println("Procházím bod " + n);
            checkPause();
            ArrayList<Edge> edges = n.getOutcome();
            for (Edge e : edges) {
                alg.repaint();

                if (e.getKnown()) {
                    continue;
                }
                e.setKnown(true);
                System.out.println("Visited setted to true");

                Node act = e.getTo();
                if (!act.isVisited()) {
                    System.out.println("Nalezena nová cesta přes " + e + " do " + act);
                    act.setVisited(true);
                    act.setStacked(true);
                    act.addPathBack(e);
                    stack.push(act);
                    ;
                }
                act = e.getFrom();
                if (!act.isVisited()) {
                    System.out.println("Nalezena nová cesta přes " + e + " do " + act);
                    System.out.println("Act->");
                    act.setVisited(true);
                    act.setStacked(true);
                    act.addPathBack(e);
                    stack.push(act);
                }
                alg.repaint();
                checkPause();
                System.out.println(e + "->visited = false");
                checkPause();
            }
            System.out.println("3: " + stack.size());
            n.setSelected(false);
        }
        if (finish == null) {
            alg.write("Prošli jsme celý dostupný graf", null);
        } else {
            alg.write("Can't reach end of graph", null);
        }
    }

    private void checkPause() {
        try {
            sleep(500);
            if (!run) {
                run = true;
                System.out.println("Paused");
                synchronized (lock) {
                    lock.wait();

                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DFS.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addHistory() {

    }

    private void paintRightPath() {
        Node act = finish;
        while (act.getPathBack() != null) {
            try {
                Edge n = act.getPathBack();
                n.setBestPath(true);
                alg.repaint();
                sleep(400);
                act = n.getOposite(act);

            } catch (InterruptedException ex) {
                Logger.getLogger(DFS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
