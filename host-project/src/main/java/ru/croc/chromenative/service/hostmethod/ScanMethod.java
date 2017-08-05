package ru.croc.chromenative.service.hostmethod;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;
import ru.croc.chromenative.HostApplication;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;

/**
 * Приложение для сканирования для запуска через расширение браузера
 * @author dzamorin
 * @since 13.07.2017 13:19
 */
public class ScanMethod extends AbstractMethod {

    /**
     * Событие инициализации приложения сканирования
     */
    public static final String EXT_APP_EVENT_INITSCAN = "initscan";

    /**
     * Событие закрытия окна приложения сканирования
     */
    public static final String EXT_APP_EVENT_SCANCANCEL = "scancancel";

    /**
     * Атрибут обмена данными с расширением data
     */
    public final static String EXT_APP_ATTR_DATA ="data";


    @Override
    public String getResult() {
        initApp();
        String message = "";

        try {
            JSONObject data = new JSONObject();
            data.accumulate(EXT_APP_EVENT_INITSCAN, true);
            message = ScanApp.prepareCallbackData(data, true);
        } catch (final JSONException e) {
            error(e);
        }
        return message;
    }

    private void initApp() {

        try {
            JDialog f = new JDialog();
            f.setAlwaysOnTop(true);
            f.setLayout(new FlowLayout(FlowLayout.CENTER));
            ScanApp app = new ScanApp();
            app.init(new JSONObject(getData()));
            f.add(app);
            final int minimumWidth = 1050;
            final int minimumHeight = 600;
            f.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
            f.setPreferredSize(new Dimension(minimumWidth, minimumHeight));
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
            f.setVisible(true);
            f.requestFocus();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    JSONObject data = new JSONObject();
                    try {
                        data.accumulate(EXT_APP_EVENT_SCANCANCEL, true);
                        ScanApp.callUploadCallback(data, false);
                    } catch (final JSONException e1) {
                        error(e1);
                    } catch (final IOException e1) {
                        error(e1);
                    }

                }
            });

        } catch (final Throwable ex) {
            error(ex);
        }

        }

    private void error(final Throwable e) {
        LogService.getInstance().error(e);
    }

}
