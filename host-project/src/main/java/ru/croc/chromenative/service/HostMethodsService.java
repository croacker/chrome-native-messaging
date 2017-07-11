package ru.croc.chromenative.service;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.hostmethod.IMethod;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class HostMethodsService {

    /**
     * Статический экземпляр, замена DI
     */
    private static HostMethodsService instance;

    public static HostMethodsService getInstance() {
        if (instance == null){
            instance = new HostMethodsService();
        }
        return instance;
    }

    public String execute(NativeRequest request){
        IMethod method = BeanService.getInstance().getMethod(request);
        method.init(request.getData());
        return method.getResult();
    }

}
