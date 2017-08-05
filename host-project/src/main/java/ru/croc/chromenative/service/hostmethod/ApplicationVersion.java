package ru.croc.chromenative.service.hostmethod;

import ru.croc.chromenative.HostApplication;

/**
 * 14.07.2017.
 */
public class ApplicationVersion implements IMethod {

    /**
     * Данные запроса.
     */
    private String data;

    @Override
    public void init(final String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return HostApplication.VERSION;
    }

}
