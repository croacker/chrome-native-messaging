package com.croc.documentum.print;

import javax.swing.*;
import java.awt.*;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class SwingTestApplet implements IMethod{

    @Override
    public String getResult() {
        showFrame();
        return "show";
    }

    private void showFrame(){
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel lbl0 = new JLabel("CAPTION000");
        JLabel lbl1 = new JLabel("CAPTION111");
        JLabel lbl2 = new JLabel("CAPTION222");
        JLabel lbl3 = new JLabel("CAPTION333");
        JLabel lbl4 = new JLabel("CAPTION444");
        frame.getContentPane().add(lbl0, BorderLayout.NORTH);
        frame.getContentPane().add(lbl1, BorderLayout.WEST);
        frame.getContentPane().add(lbl2, BorderLayout.CENTER);
        frame.getContentPane().add(lbl3, BorderLayout.EAST);
        frame.getContentPane().add(lbl4, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

}