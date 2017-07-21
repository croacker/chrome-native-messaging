package ru.croc.chromenative;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.common.base.Strings;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.JobService;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.MapperService;

/**
 * Т.н. Native application для Browser extension. Класс - точкач входа в приложение.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class HostApplication {

    /**
     * Версия.
     */
    public static final String VERSION = "0.0.1";

    /**
     * Экземпляр приложения.
     */
    private static HostApplication instance;

    /**
     * Получить экземпляр приложения.
     *
     * @return Экземпляр приложения.
     */
    public static HostApplication getInstance() {
        if (instance == null) {
            instance = new HostApplication();
        }
        return instance;
    }

    /**
     * Точка входа.
     *
     * @param args
     *            аргументы запуска приложения.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            info("Application start...");
            info("args:" + Arrays.toString(args));
            if ((args != null) && (args.length > 0) && (args[0].equals("firefox"))){
                info("Message is from firefox");
                listentofirefoxextnlaunch();
            }else {
                getInstance().run();
            }
            System.exit(0);
        } catch (Exception e) {
            error(e.getMessage(), e);
            info("Application shutdown...");
            JobService.getInstance().shutdownNow();
        }
    }

    /**
     * Инициирует бесконечное чтение потока ввода, для получения сообщений от Browser extension.
     *
     * @throws Exception
     */
    private void run() throws Exception {
        for (;;) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
            info("Request JSON: " + requestJson);
            if (!Strings.isNullOrEmpty(requestJson)) {
                NativeRequest request = MapperService.getInstance().readValue(requestJson, NativeRequest.class);
                if (request != null) {
                    JobService.getInstance().submit(request);
                }
            }
        }
    }

    private static void listentofirefoxextnlaunch(){
        for (;;){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input;
                if ((input = br.readLine()) != null) {
                    info("Message from Firefox" + input);
                }
            } catch (Throwable e){
                error(e.getMessage(), e);
            }
        }
    }

    private static void info(String msg) {
        LogService.getInstance().info(msg);
    }

    private static void error(String msg, Throwable e) {
        LogService.getInstance().error(msg, e);
    }

}