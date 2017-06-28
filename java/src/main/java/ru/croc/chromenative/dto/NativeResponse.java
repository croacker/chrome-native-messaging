package ru.croc.chromenative.dto;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class NativeResponse {

    private String status;

    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NativeResponse() {
    }

}
