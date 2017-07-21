package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.panels.CroppableImagePanel;

/**
 * Обработчик события обрезки изображения
 * @since  26.01.12 Time: 12:34
 */
public class CropImageAction extends AbstractAction {

    public void actionPerformed(final ActionEvent e) {

        Image src = ScanApp.getApp().getSelected();
        Image dst = ((CroppableImagePanel) ScanApp.getApp().getImagePanel()).cropImage();
        if (dst != null) {
            int pos = ScanApp.getApp().remove(src);
            ScanApp.getApp().add(dst, pos);
        }
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasSelected();
    }
}
