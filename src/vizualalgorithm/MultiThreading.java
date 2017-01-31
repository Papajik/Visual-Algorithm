/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import jdk.nashorn.internal.parser.TokenType;

/**
 *
 * @author Papi
 */
public class MultiThreading {
    
    public MultiThreading(int threads, Node start, Node finish){
        MultiBFS.setFinish(finish);
        MultiBFS.setNumberOfThreads(threads);
        MultiBFS mt = new MultiBFS(start);
    
    }

    
}
