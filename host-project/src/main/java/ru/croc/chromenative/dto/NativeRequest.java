package ru.croc.chromenative.dto;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class NativeRequest {

    private String method;

    private String data;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NativeRequest() {
    }

    public NativeRequest(String method, String data) {
        this.method = method;
        this.data = data;
    }

}
