package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.ImageUtils;

/**
 * Обработчик поворота изображения
 * @since  26.01.12 Time: 12:34
 */
public class RotateImageAction extends AbstractAction {

    private int angle = 0;

    public RotateImageAction(final int angle) {
        super();
        this.angle = angle;
    }

    public void actionPerformed(final ActionEvent e) {

        Image src = ScanApp.getApp().getSelected();
        Image dst = ImageUtils.tilt(ImageUtils.toBufferedImage(src), Math.toRadians(angle));

        int pos = ScanApp.getApp().remove(src);
        ScanApp.getApp().add(dst, pos);
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasSelected();
    }
}
