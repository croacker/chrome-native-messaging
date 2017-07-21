package ru.croc.chromenative.service.hostmethod.scan.dialogs;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Диалог выбора сканера
 * @author dzamorin
 * @since Date: 05.03.16 Time: 9:53
 */


public class SelectScannerDialog {

    public static String ask(final ArrayList<String> values, final Window parentWindow) {

        String result = null;

        if (EventQueue.isDispatchThread()) {

            JPanel panel = new JPanel();
            panel.add(new JLabel("Выберите сканер:"));
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            for (String value : values) {
                model.addElement(value);
            }
            JComboBox comboBox = new JComboBox(model);
            panel.add(comboBox);

            int iResult = JOptionPane.showConfirmDialog(parentWindow, panel, "Сканер",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (iResult) {
                case JOptionPane.OK_OPTION:
                    result = (String) comboBox.getSelectedItem();
                    break;
            }

        } else {

            Response response = new Response(values, parentWindow);
            try {
                SwingUtilities.invokeAndWait(response);
                result = response.getResponse();
            } catch (final InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (final InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        return result;

    }

    public static class Response implements Runnable {

        private ArrayList<String>  values;
        private String response;
        private Object parentWindow;

        public Response(final ArrayList<String> values, final Component parentWindow) {
            this.values = values;
            this.parentWindow=parentWindow;
        }

        @Override
        public void run() {
            response = ask(values, (Window) parentWindow);
        }

        public String getResponse() {
            return response;
        }
    }
}