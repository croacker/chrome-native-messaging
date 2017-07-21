package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.croc.chromenative.service.hostmethod.scan.ScanApp;


/**
 * Обработчик удаления отсканированного изображения
 * @since  26.01.12 Time: 12:34
 */
public class RemoveImageAction extends AbstractAction implements Runnable {

    public synchronized void actionPerformed(final ActionEvent event) {
        new Thread(this).start();
    }

    public synchronized void run() {
        ScanApp.getApp().remove(ScanApp.getApp().getSelected());
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasSelected();
    }
}