package ru.croc.chromenative.service.hostmethod;

/**
 * Результирующий статус выполнения метода.
 */
public enum ResultStatus {
    SUCCESS("success"), ERROR("error");

    private final String success;

    public String getName() {
        return success;
    }

    ResultStatus(String success) {
        this.success = success;
    }
}
