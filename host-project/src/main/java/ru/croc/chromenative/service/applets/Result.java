package ru.croc.chromenative.service.applets;

/**
 * Результат выполнения метода
 */
public enum Result {
    SUCCESS("success"), ERROR("error");

    private final String success;

    public String getName() {
        return success;
    }

    Result(String success) {
        this.success = success;
    }
}
