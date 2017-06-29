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
        JLabel lbl = new JLabel("CAPTION11111111111111111111111111111111111111111111111111111111111111111111111\n" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
        frame.getContentPane().add(lbl, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

}
