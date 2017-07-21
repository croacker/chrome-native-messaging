package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.asprise.imaging.core.Imaging;
import com.asprise.imaging.core.Request;
import com.asprise.imaging.core.RequestOutputItem;
import com.asprise.imaging.core.Result;
import com.asprise.imaging.core.scan.twain.Source;
import com.asprise.imaging.core.scan.twain.TwainConstants;
import org.json.JSONObject;
import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.croc.chromenative.service.hostmethod.scan.uploader.DirectUploader;
import ru.sggr.scan.uploader.UploadResponse;

/**
 * Обработчик события сканирования и загрузки на сервер
 * @since  26.01.12 Time: 12:34
 */
public class AcquireAndUploadImageAction extends AbstractAction implements Runnable {

    public synchronized void actionPerformed(final ActionEvent event) {
        Imaging.getDefaultExecutorServiceForScanning().submit(this);
    }

    public synchronized void run() {
        App app = ScanApp.getApp();
        if (!app.hasScanner()) {
            return;
        }

        try {
            app.updateStatus("Сканирование ...");
            ScanApp.getApp().getRootPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            java.util.List<Source> sources = ScanApp.getManager().
                    scanListSources(false, "all", true, false);
            Source currentScanner = null;
            for (Source source : sources) {
                if (source.getSourceName().equals(app.getScannerId())) {
                    currentScanner = source;
                    break;
                }
            }

            if(currentScanner==null){
                JOptionPane.showMessageDialog(app.getRootPanel(), "Сканер по-умолчанию не доступен, " +
                                "выберете другой источник сканирования",
                        "Ошибка при сканировании", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DirectUploader uploader = new DirectUploader();
            UploadResponse response = null;
            int pageCount = 1;
            String modelId = null;

            final int ICAP_RESOLUTION_VALUE = 100;
            Request request = new Request()
                    .setTwainCap( // greyscale
                            TwainConstants.ICAP_PIXELTYPE, TwainConstants.TWPT_GRAY)
                    .setTwainCap( // A4
                            TwainConstants.ICAP_SUPPORTEDSIZES, TwainConstants.TWSS_A4)
                    .setTwainCap(
                            TwainConstants.ICAP_XRESOLUTION, ICAP_RESOLUTION_VALUE)
                    .setTwainCap(
                            TwainConstants.ICAP_YRESOLUTION, ICAP_RESOLUTION_VALUE)
                    .addOutputItem(
                            new RequestOutputItem(Imaging.OUTPUT_RETURN_BASE64, Imaging.FORMAT_JPG)
                    )
                    .addOutputItem(
                            new RequestOutputItem(Imaging.OUTPUT_SAVE_THUMB,
                                    Imaging.FORMAT_JPG).setSavePath(".\\${TMS}-thumb${EXT}")
                    );


            Result result = ScanApp.getManager().scan(request, currentScanner.getSourceName(),
                    !app.isUseDefaults(), !app.isUseDefaults());

            int imgCount=result.getImageCount();

            if (imgCount==0) {
                app.updateStatus("Сканирование не выполнено");
                return;
            }

            for (int i=0;i<imgCount;i++) {
                BufferedImage img = result.getImage(i);
                response = uploader.upload(img, true, pageCount, modelId);
                pageCount++;
                if (response != null && !response.isSuccess()) {
                    JOptionPane.showMessageDialog(app.getRootPanel(), response.getData().getMsg(),
                            "Ошибка при передаче файлов", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (response != null && response.getData().getModelids() != null &&
                        response.getData().getModelids().size() > 0) {
                    modelId = (String) response.getData().getModelids().toArray()[0];
                }
            }


            if (response != null) {
                JSONObject data = new JSONObject();
                data.accumulate("scanresult", response.getData().getModelids().get(0));
                data.accumulate("scannerId", ScanApp.getApp().getScannerId());
                ScanApp.callUploadCallback(data, false);
            }

            app.removeAll();

            System.exit(0);

        } catch (final NoSuchMethodError error) {
            JOptionPane.showMessageDialog(app.getRootPanel(), "Установлена более поздняя версия Asprise.  " +
                    System.getProperty("java.home") + ".\nНеобходимо её удалить.\nНажмите ОК, " +
                    "чтобы заершить работу.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (final Throwable exception) {

            JOptionPane.showMessageDialog(app.getRootPanel(), exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
            app.updateStatus("Не удалось отсканировать. Попробуйте снова ...");
        } finally {
            ScanApp.getApp().getRootPanel().setCursor(Cursor.getDefaultCursor());
            ScanApp.getApp().updateStatus("Операция завершена.");
        }
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasScanner();
    }
}