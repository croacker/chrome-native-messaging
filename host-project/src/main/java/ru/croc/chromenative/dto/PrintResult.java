package ru.croc.chromenative.dto;

/**
 * Реультат выполнения метода связанного с печатью, для трансляции в json ответ для Browser extension.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class PrintResult {

    /**
     * Статус выполнения. см. ru.croc.chromenative.service.hostmethod.ResultStatus
     */
    private String status;

    /**
     * Сообщение для передачи в Browser extension.
     */
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public PrintResult() {
    }

    public PrintResult(final String status, final String message) {
        this.status = status;
        this.message = message;
    }

}
