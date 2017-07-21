package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.json.JSONObject;
import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.uploader.UploadResponse;
import ru.sggr.scan.uploader.Uploader;

/**
 * Обработчик загрузки изображения на сервер
 * @since  26.01.12 Time: 12:34
 */
public class UploadImagesAction extends AbstractAction implements Runnable {

    public void actionPerformed(final ActionEvent e) {
        new Thread(this).run();
    }

    public void run() {
        App app = ScanApp.getApp();
        List<Image> images = app.getImages();
        Uploader uploader = new Uploader(images);
        try {
            UploadResponse response = uploader.upload();
            if (!response.isSuccess()) {
                JOptionPane.showMessageDialog(app.getRootPanel(),
                        response.getData().getMsg(),
                        "Ошибка при передаче файлов",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (response != null) {
                JSONObject data = new JSONObject();
                data.accumulate("scanresult", response.getData().getModelids().get(0));
                data.accumulate("scannerId", ScanApp.getApp().getScannerId());
                ScanApp.callUploadCallback(data, false);
            }

            app.removeAll();

        } catch (final Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(app.getRootPanel(),
                    exception.toString(),
                    "Ошибка при передаче файлов",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasImages();
    }
}
