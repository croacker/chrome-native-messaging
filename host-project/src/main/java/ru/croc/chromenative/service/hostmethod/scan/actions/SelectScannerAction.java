package ru.croc.chromenative.service.hostmethod.scan.actions;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import ru.croc.chromenative.service.hostmethod.scan.App;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.croc.chromenative.service.hostmethod.scan.dialogs.SelectScannerDialog;

/**
 * Обработчик выбора сканера
 * @since  26.01.12 Time: 12:34
 */
public class SelectScannerAction extends AbstractAction implements Runnable {
    Object source;

    public synchronized void actionPerformed(final ActionEvent e) {
        source = e.getSource();
        new Thread(this).start();
    }

    public synchronized void run() {
        App app = ScanApp.getApp();
        Window parentWindow = SwingUtilities.windowForComponent((Component) source);
        app.updateStatus("Выбор сканера...");
        try {
            String choice = SelectScannerDialog.ask(app.getSystemScanners(), parentWindow);
            ScanApp.Log("Выбран " + choice);
            app.setScannerId(choice);
            JSONObject data = new JSONObject();
            data.accumulate("scansource", choice);
            ScanApp.callUploadCallback(data, true);
            if (parentWindow instanceof JDialog) {
                parentWindow.setAlwaysOnTop(true);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            ScanApp.getApp().updateStatus("Операция завершена.");
        }
    }
}
