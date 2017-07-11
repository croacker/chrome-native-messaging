package com.croc.documentum.print;

import ru.croc.chromenative.dto.PrintResult;
import ru.croc.chromenative.service.applets.Result;

/**
 *
 */
public abstract class AbstractMethod implements IMethod {

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
        return new PrintResult(Result.ERROR.getName(), message);
    }

    protected PrintResult getSuccess(String message){
        return new PrintResult(Result.SUCCESS.getName(), message);
    }
}
