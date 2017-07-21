package ru.croc.chromenative.service;

/**
 * Сервис записи в журнал. Временно не используется, в связи с проблемамаи в старых версияъ chrome, например в ver.37
 * вывод лога направляется в основной поток вывыода.
 *
 * @author agumenyuk
 * @since 20.07.2016 17:01
 */
public class LogService {

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

    /**
     * Вывод в лог информационного сообщения. В связи с проблемами в Chrome v37, в данный момент ничего, никуда не
     * выводит.
     * 
     * @param msg
     *            текст сообщения
     */
    public void info(String msg) {
    }

    /**
     * Вывод в лог сообщения об ошибке. В связи с проблемами в Chrome v37, в данный момент ничего, никуда не выводит.
     * 
     * @param msg
     *            текст ошибки
     */
    public void error(String msg) {
    }

    /**
     * Вывод в лог сообщения об ошибке. В связи с проблемами в Chrome v37, в данный момент ничего, никуда не выводит.
     * 
     * @param msg
     *            текст ошибки
     */
    public void error(String msg, Throwable e) {
    }
}
