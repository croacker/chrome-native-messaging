package ru.croc.chromenative.job;

/**
 * Результат вызова метода в отдельном потоке.
 */
public enum JobResult {
    OK("ok"),
    EXCEPTION("exception");

    private final String name;

    public String getName() {
        return name;
    }

    JobResult(String name) {
        this.name = name;
    }
}
