package ru.croc.chromenative;

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
            getInstance().run();
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

    /**
     * Вывод в лог информационного сообщения.
     * @param msg
     */
    private static void info(String msg) {
        LogService.getInstance().info(msg);
    }

    /**
     * Вывод в лог сообщения об ошибке.
     * @param msg
     * @param e
     */
    private static void error(String msg, Throwable e) {
        LogService.getInstance().error(msg, e);
    }

}