package ru.croc.chromenative.dto;

/**
 * Подготовленный к трансляции в json ответ для Browser extension.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class NativeResponse {

    /**
     * Статус выполнения. см. ru.croc.chromenative.service.hostmethod.ResultStatus
     */
    private String status;

    /**
     * Данные в виде json-строки для педачи в Browser extension
     */
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    public NativeResponse() {
    }

}
