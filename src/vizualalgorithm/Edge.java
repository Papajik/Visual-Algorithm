/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

/**
 *
 * @author Papi
 */
public class Edge {

    private Node from, to;
    private boolean oriented;
    private int length;

    public Edge(Node from, Node to, boolean oriented, int length) {
        this.from = from;
        this.to = to;
        this.oriented = oriented;
        this.length = length;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public boolean isOriented() {
        return oriented;
    }

    public int getLength() {
        return length;
    }

}
