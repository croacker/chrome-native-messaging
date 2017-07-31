package ru.croc.chromenative.service.hostmethod.scan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.UIManager;

import com.asprise.imaging.core.Imaging;
import com.asprise.imaging.core.scan.twain.Source;
import org.json.JSONException;
import org.json.JSONObject;
import ru.croc.chromenative.HostApplication;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.hostmethod.ScanMethod;

/**
 * Основное окно приложения сканирования
 * @since  26.01.12 Time: 12:34
 */
public class ScanApp extends JPanel {

    private static App app;

    private static URL documentBase;

    private static String uploadUrl;

    private static String userName;

    private static String modelClass;

    private static String uploadCallback;

    private static Imaging manager;

    private static final int DEFAULT_BATCH_SIZE = 10;

    private static int uploadbatchsize = DEFAULT_BATCH_SIZE;

    private String scannerId = null;

    public static Imaging getManager() {
        return manager;
    }


    public static App getApp() {
        return app;
    }

    public static URL getDocBase() {
        return documentBase;
    }

    public static String getUploadUrl() {
        return uploadUrl;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getModelClass() {
        return modelClass;
    }

    public static int getUploadbatchsize() {
        return uploadbatchsize;
    }

    public static void setUploadbatchsize(final int uploadbatchsize) {
        ScanApp.uploadbatchsize = uploadbatchsize;
    }


    public static void callUploadCallback(final JSONObject data,
                                          final boolean leaveListenerActive) throws JSONException, IOException {
        CommunicateService.getInstance().sendMessage(prepareCallbackData(data, leaveListenerActive));

    }

    public static String prepareCallbackData(final JSONObject data,
                                             final boolean leaveListenerActive) throws JSONException {
        JSONObject complete = new JSONObject();
        complete.accumulate(ScanMethod.EXT_APP_ATTR_LEAVE_LISTENER_ACTIVE, leaveListenerActive);
        complete.accumulate(ScanMethod.EXT_APP_ATTR_DATA, data);
        String message = complete.toString();
        LogService.getInstance().info(message);
        return message;
    }

    public void init(final JSONObject request) {

        /**
         * Начало блока установки лицензии
         */

        System.setProperty("ASCAN_LICENSE_NAME", "Single_2_JSC_CROC_Inc-STD");
        System.setProperty("ASCAN_LICENSE_CODE", "D9E60-F5A79-3DB86-7E558");

        /**
         * Конец блока установки лицензии
         */

        app = new App();

        try {
            if (request != null) {
                uploadUrl = request.getString("uploadUrl");
                userName = request.getString("userName");
                modelClass = request.getString("modelClass");
                documentBase = new URL(request.getString("documentBase"));
                scannerId = request.getString("scannerId");
                getApp().setScannerId(scannerId);
            }

        } catch (final JSONException e) {
            error(e);
        } catch (final MalformedURLException e) {
            error(e);
        }
        Imaging.configureNativeLogging(Imaging.LOG_LEVEL_DEBUG, Imaging.LOG_TO_STDERR);
        manager = new Imaging("myApp", 0);
        uploadCallback = "uploadCallback";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (final Exception e) {
            info("Failed to set Look And Feel");
        }


        Imaging.getDefaultExecutorServiceForScanning().submit(new Runnable() {
            @Override
            public void run() {
                List<Source> sources = ScanApp.getManager().
                        scanListSources(false, "all", false, false);

                ArrayList<String> scannerNames = app.getSystemScanners();
                for (Source source : sources) {
                    scannerNames.add(source.getSourceName());
                }
                int systemScanLength = app.getSystemScanners().size();


                if (systemScanLength > 0) {
                    if (scannerId != null) {
                        if (app.getSystemScanners().contains(scannerId)) {
                            app.setScannerId(scannerId);
                        }
                    }
                    app.setNoScanner(false);
                } else {
                    if (systemScanLength == 0) {
                        info("No scanners in system");
                        app.setNoScanner(true);
                    }
                }
                add(app.getRootPanel());
                app.updateStatus("ID NEW 1: " + app.getClass().getClassLoader().hashCode() + " " + scannerId);
            }
        });


    }

    public String getScannerId() {
        return app.getScannerId();
    }


    public void saveAndUpload() {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                App.Actions.uploadImagesAction.actionPerformed(null);
                return null;
            }
        });
    }

    /**
     * Вывод в лог информационного сообщения.
     *
     * @param msg
     */
    private static void info(final String msg) {
        LogService.getInstance().info(msg);
    }

    /**
     * Вывод в лог сообщения об ошибке.
     *
     * @param e
     */
    private static void error(final Throwable e) {
        LogService.getInstance().error(e);
    }
}