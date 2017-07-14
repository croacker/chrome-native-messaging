package ru.croc.chromenative;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.JobService;
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
     * Объект для журналирования.
     */
    private static Logger LOGGER;

    /**
     * Файл журнала поумолчанию.
     */
    public static final String LOG_FILENAME = "croc_external_app.log";

    /**
     * Получить/инициализировать и получить объект журналирования.
     * 
     * @return объект журналирования
     */
    public static Logger getLOGGER() {
        if (LOGGER == null) {
            try {
                LOGGER = initLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return LOGGER;
    }

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
            getInstance().run();
            System.exit(0);
        } catch (Exception e) {
            JobService.getInstance().shutdownNow();
            log(e.getMessage());
        }
    }

    /**
     * Инициирует бесконечное чтение потока ввода, для получения сообщений от Browser extension.
     * 
     * @throws Exception
     */
    private void run() throws Exception {
        boolean stop = false;
        for (;;) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
            log("Request JSON: " + requestJson);
            ObjectMapper mapper = MapperService.getInstance().getMapper();
            NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
            JobService.getInstance().submit(request);
            if (stop) {
                break;
            }
        }
    }

    /**
     * Инициализация объект журналирования.
     * 
     * @return Объект журналирования.
     * @throws IOException
     */
    private static Logger initLogger() throws IOException {
        Logger logger = Logger.getLogger(HostApplication.class.getName());
        FileHandler fileHandler = new FileHandler(LOG_FILENAME);
        fileHandler.setLevel(Level.ALL);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    /**
     * Добавить запись в журнал.
     * @param message
     */
    public static void log(String message) {
        getLOGGER().log(Level.INFO, message);
    }
}