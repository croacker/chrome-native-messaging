package ru.croc.chromenative.service;

import com.croc.documentum.print.IMethod;
import ru.croc.chromenative.dto.NativeRequest;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class JavaAppletService {

    private static JavaAppletService instance;

    public static JavaAppletService getInstance() {
        if (instance == null){
            instance = new JavaAppletService();
        }
        return instance;
    }

    public String execute(NativeRequest request){
        IMethod method = BeanService.getInstance().getMethod(request);
        method.init(request.getData());
        return method.getResult();
    }

}
