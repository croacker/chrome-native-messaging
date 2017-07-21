package ru.croc.chromenative.service.hostmethod.scan.actions;

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
import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;


/**
 * Обработчик события загрузки на сервер
  * @since  26.01.12 Time: 12:34
 */

public class AcquireImageAction extends AbstractAction implements Runnable {

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

            java.util.List<Source> sources = ScanApp.getManager().
                    scanListSources(false, "all", true, false);
            Source currentScanner = null;
            for (Source source : sources) {
                if (source.getSourceName().equals(app.getScannerId())) {
                    currentScanner = source;
                    break;
                }
            }

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
                    .setTwainCap(
                            TwainConstants.CAP_UICONTROLLABLE, !app.isUseDefaults())
                    .addOutputItem(
                            new RequestOutputItem(Imaging.OUTPUT_RETURN_BASE64, Imaging.FORMAT_JPG)
                    )
                    .addOutputItem(
                            new RequestOutputItem(Imaging.OUTPUT_SAVE_THUMB,
                                    Imaging.FORMAT_JPG).setSavePath(".\\${TMS}-thumb${EXT}")
                    );

            Result result = ScanApp.getManager().scan(request, currentScanner.getSourceName(), !app.isUseDefaults(),
                    !app.isUseDefaults());

            int imgCount = result.getImageCount();

            if (imgCount == 0) {
                JOptionPane.showMessageDialog(app.getRootPanel(), "Сканирование не выполнено",
                        "Ошибка при сканировании", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < imgCount; i++) {
                BufferedImage img = result.getImage(i);
                app.add(img);
                int imageWidth = img.getWidth();
                int imageHeight = img.getHeight();
                app.updateStatus("Готово - размер изображения " + imageWidth + " x " +
                        imageHeight + " ...");
            }


        } catch (final OutOfMemoryError error) {
            JOptionPane.showMessageDialog(app.getRootPanel(),
                    "Слишком большой размер документа. Используйте кнопку \"Сканировать\"", "Error",
                    JOptionPane.ERROR_MESSAGE);
            error.printStackTrace();
            app.updateStatus("Не удалось отсканировать. Слишком большой размер отсканированного документа ...");
        } catch (final Throwable exception) {
            JOptionPane.showMessageDialog(app.getRootPanel(), exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
            app.updateStatus("Не удалось отсканировать. Попробуйте снова ...");
        } finally {
            ScanApp.getApp().updateStatus("Операция завершена.");
        }
    }

    @Override
    public boolean isEnabled() {
        return ScanApp.getApp() != null && ScanApp.getApp().hasScanner();
    }
}
