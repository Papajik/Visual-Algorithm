/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Papi
 */
public class Algorithm {

    private Stack<Object[]> history;
    private Node start;
    private Node finish;

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

    public Algorithm() {
        history = new Stack<>();
    }

    public void load(ArrayList<Edge> edges, ArrayList<Node> nodes) {
        for (Node n : nodes) {
            n.initialize();
            for (Edge e : edges) {
                if (e.isOriented()) {
                    if (e.getFrom().equals(n)) {
                        n.addOutcome(e);
                    } else if (e.getFrom().equals(n) || e.getTo().equals(n)) {
                        n.addOutcome(e);
                    }
                }
            }
        }
    }
    
    public void start(){
        
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
