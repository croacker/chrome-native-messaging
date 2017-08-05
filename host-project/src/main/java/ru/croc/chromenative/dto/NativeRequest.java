package ru.croc.chromenative.dto;

/**
 * Транслированный из json-запрос от Browser extension.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class NativeRequest {

    /**
     * Наименование метода, который необходимо вызвать.
     */
    private String method;

    /**
     * Данные для передачи в метод. Возможно использование json-строки, для дальнейшей трансляции в объект.
     */
    private String data;

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    public NativeRequest() {
    }

}
