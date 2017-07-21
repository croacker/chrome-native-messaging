package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.panels.ImagePanel;

/**
 * Обработчик зуммирования изображения
 * @since  26.01.12 Time: 12:34
 */
public class ZoomImageAction extends AbstractAction {

    protected DIRECTION direction;

    public ZoomImageAction(final DIRECTION direction) {
        this.direction = direction;
    }

    public synchronized void actionPerformed(final ActionEvent event) {

        App app = ScanApp.getApp();
        final double ratio = 0.1d;
        ImagePanel panel = app.getImagePanel();
        if (!direction.equals(DIRECTION.ASIS)) {
            panel.addRatio(direction.equals(DIRECTION.OUT) ? -ratio : +ratio);
        } else {
            panel.resetRatio();
        }
        app.validateImage();
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasSelected();
    }

    public enum DIRECTION {
        IN, ASIS, OUT
    }
}