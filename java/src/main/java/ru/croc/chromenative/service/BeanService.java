package ru.croc.chromenative.service;

import com.croc.documentum.print.IMethod;
import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.applets.Method;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class BeanService {

    private static BeanService instance;

    public static BeanService getInstance() {
        if (instance == null){
            instance = new BeanService();
        }
        return instance;
    }

    public IMethod getMethod(NativeRequest request){
        Method method = Method.get(request.getMethod());
        return newInstance(method);
    }

    private IMethod newInstance(Method method) {
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
