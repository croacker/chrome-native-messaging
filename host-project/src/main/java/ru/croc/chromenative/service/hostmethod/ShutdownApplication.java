package ru.croc.chromenative.service.hostmethod;

import ru.croc.chromenative.service.JobService;

/**
 * Метод останавливающий приложение.
 * 14.07.2017.
 */
public class ShutdownApplication implements IMethod {

    private String data;

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        JobService.getInstance().shutdown();
        return ResultStatus.SHUTDOWN.getName();
    }
}
