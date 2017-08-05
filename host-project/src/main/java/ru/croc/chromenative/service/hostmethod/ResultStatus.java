package ru.croc.chromenative.service.hostmethod;

/**
 * Результирующий статус выполнения метода.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public enum ResultStatus {
    /**
     * Успех.
     */
    SUCCESS("success"),
    /**
     * Остановка приложения.
     */
    SHUTDOWN("shutdown"),
    /**
     * Ошибка
     */
    ERROR("error");

    /**
     * Наименование результата.
     */
    private final String name;

    public String getName() {
        return name;
    }

    ResultStatus(final String name) {
        this.name = name;
    }
}
