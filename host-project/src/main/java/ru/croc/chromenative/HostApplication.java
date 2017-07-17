package ru.croc.chromenative;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
     * Логгер
     */
    private static Logger log = LogManager.getLogger(HostApplication.class);

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
     * @param args аргументы запуска приложения.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        log.info("Application start...");
        try {
            getInstance().run();
            System.exit(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("Application shutdown...");
            JobService.getInstance().shutdownNow();
        }
    }

    /**
     * Инициирует бесконечное чтение потока ввода, для получения сообщений от Browser extension.
     *
     * @throws Exception
     */
    private void run() throws Exception {
        boolean stop = false;
        for (; ; ) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
            log.info("Request JSON: " + requestJson);
            ObjectMapper mapper = MapperService.getInstance().getMapper();
            NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
            JobService.getInstance().submit(request);
            if (stop) {
                break;
            }
        }
    }

}