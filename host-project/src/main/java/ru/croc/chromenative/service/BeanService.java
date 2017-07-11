package ru.croc.chromenative.service;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.hostmethod.IMethod;
import ru.croc.chromenative.service.hostmethod.Methods;

/**
 *
 */
public class BeanService {

    /**
     * Статический экземпляр, замена DI
     */
    private static BeanService instance;

    public static BeanService getInstance() {
        if (instance == null){
            instance = new BeanService();
        }
        return instance;
    }

    public IMethod getMethod(NativeRequest request){
        Methods method = Methods.get(request.getMethod());
        return newInstance(method);
    }

    private IMethod newInstance(Methods method) {
        IMethod result;
        try {
            result = (IMethod) method.getAppletClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

}
