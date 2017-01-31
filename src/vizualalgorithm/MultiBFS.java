/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.util.LinkedList;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author Papi
 */
public class MultiBFS extends RecursiveAction {

    private LinkedList<Node> queue;
    private static int maxThreads;
    private static Node finish;
    private Node node;

    public static void setFinish(Node fin) {
        finish = fin;
    }
    
    public static void setNumberOfThreads(int number){
        maxThreads = number;
    }

    public MultiBFS(Node start) {
        this(start, new LinkedList<>());
    }

    private MultiBFS(Node n, LinkedList<Node> queue) {
        node = n;
        this.queue = queue;
    }

    @Override
    protected void compute() {
        
    }

}
