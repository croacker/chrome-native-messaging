package ru.croc.chromenative.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис записи в журнал. Временно не используется, в связи с проблемамаи в старых версияъ chrome, например в ver.37
 * вывод лога направляется в основной поток вывыода.
 *
 * @author agumenyuk
 * @since 20.07.2016 17:01
 */
public class LogService {

    /**
     * Логгер
     */
//    private static Logger log = LogManager.getLogger(MapperService.class);

    /**
     * Статический экземпляр, замена DI
     */
    private static LogService instance;

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    public void info(String msg){
//        log.info(msg);
    }

    public void error(String msg) {
//        log.error(msg);
    }

    public void error(String msg, Throwable e) {
//        log.error(msg, e);
    }
}
