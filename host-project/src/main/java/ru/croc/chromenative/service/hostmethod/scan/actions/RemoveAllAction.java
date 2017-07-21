package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.croc.chromenative.service.hostmethod.scan.ScanApp;


/**
 * Обработчик удаления всех отсканированных изображений
 * @since  26.01.12 Time: 12:34
 */
public class RemoveAllAction extends AbstractAction implements Runnable {

    public synchronized void actionPerformed(final ActionEvent event) {
        new Thread(this).start();
    }

    public synchronized void run() {
        ScanApp.getApp().removeAll();
        ScanApp.getApp().select(null);
    }
}