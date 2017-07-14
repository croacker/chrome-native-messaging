package ru.croc.chromenative.service.hostmethod;

/**
 * Перечисление-стратегия, предоставляющая соответствие наименованию методов и классу методов.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public enum Methods {

    /**
     * Версия приложения
     */
    GET_VERSION("getVersion", ApplicationVersion.class),
    /**
     * Метод останавливающий приложение.
     */
    SHUTDOWN("shutdown", ShutdownApplication.class),
    /**
     * Получить информацию о системе, в частности версию JRE.
     */
    SYSTEM_INFO_METHOD("getSystemInfo", SystemInfoMethod.class),
    /**
     * Печать вложений.
     */
    PRINT_ATTACHMENT_LIST_METHOD("printAttachments", PrintAttachmentListMethod.class),
    /**
     * Печать ШК
     */
    PRINT_METHOD("printBarcode", PrintBarcodeMethod.class);

    /**
     * Ключ - наименование метода.
     */
    private String key;

    /**
     * Класс метода.
     */
    private Class appletClass;

    public Class getAppletClass() {
        return appletClass;
    }

    Methods(String key, Class appletClass) {
        this.appletClass = appletClass;
        this.key = key;
    }

    /**
     * Получить элемент по ключу(наименованию метода).
     * 
     * @param key
     *            Ключ(наименование метода)
     * @return элемент перечисления
     */
    public static Methods get(String key) {
        Methods result = null;
        for (Methods method : values()) {
            if (method.key.equals(key)) {
                result = method;
                break;
            }
        }
        return result;
    }

}
