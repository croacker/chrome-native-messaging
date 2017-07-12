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

    ResultStatus(String name) {
        this.name = name;
    }
}
