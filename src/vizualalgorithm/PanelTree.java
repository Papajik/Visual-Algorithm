/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizualalgorithm;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 *
 * @author Papi
 */
public class PanelTree extends JPanel {
    
    public PanelTree(){
    setMouse();
}
    
      private void setMouse() {
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Tree: Mouse clicked");
                setName("Tree");
                System.out.println(e.getComponent().getName());
                e.getComponent().repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Tree: Mouse pressed");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Tree: Mouse released");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        };
        this.addMouseListener(mouseListener);
    }

}
