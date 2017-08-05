package ru.croc.chromenative.job;

/**
 * Результат вызова метода в отдельном потоке.
 * 
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public enum JobResult {
    /**
     * Выполнение завешено успешно.
     */
    OK("ok"),
    /**
     * При выполнении возникло исключение.
     */
    EXCEPTION("exception");

    /**
     * Наименование(ключ) результата.
     */
    private final String name;

    public String getName() {
        return name;
    }

    JobResult(final String name) {
        this.name = name;
    }
}
