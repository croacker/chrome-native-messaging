package ru.croc.chromenative.service;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.hostmethod.IMethod;

/**
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class HostMethodsService {

    /**
     * Статический экземпляр, замена DI
     */
    private static HostMethodsService instance;

    public static HostMethodsService getInstance() {
        if (instance == null) {
            instance = new HostMethodsService();
        }
        return instance;
    }

    /**
     * Выполнить метод приложения.
     * @param request запрос от Browser extension
     * @return
     */
    public String execute(final NativeRequest request) {
        IMethod method = BeanService.getInstance().getMethod(request);
        method.init(request.getData());
        return method.getResult();
    }

}
