package ru.croc.chromenative.service.hostmethod;

import ru.croc.chromenative.dto.PrintResult;

/**
 * Некоторый абстрактный метод хоста
 */
public abstract class AbstractMethod implements IMethod {

    /**
     * Данные полученные из Browser extension.
     */
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void init(String data) {
        this.data = data;
    }

    protected PrintResult getError(String message){
        return new PrintResult(ResultStatus.ERROR.getName(), message);
    }

    protected PrintResult getSuccess(String message){
        return new PrintResult(ResultStatus.SUCCESS.getName(), message);
    }
}
