package ru.croc.chromenative.service;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.hostmethod.IMethod;
import ru.croc.chromenative.service.hostmethod.Methods;

/**
 * Сервис работы с бинами приложения, в частности сосздания экземпляров методов приложения.
 * 
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class BeanService {

    /**
     * Статический экземпляр, замена DI
     */
    private static BeanService instance;

    public static BeanService getInstance() {
        if (instance == null) {
            instance = new BeanService();
        }
        return instance;
    }

    /**
     * Получить объект-метод для полученного от Browser extension запроса.
     * @param request Запрос от Browser extension
     * @return
     */
    public IMethod getMethod(NativeRequest request) {
        Methods method = Methods.get(request.getMethod());
        return newInstance(method);
    }

    /**
     * Создать экземпляр класса-метода.
     * @param method полученный из перечисления-стратегии элемент метода.
     * @return
     */
    private IMethod newInstance(Methods method) {
        IMethod result;
        try {
            result = (IMethod) method.getAppletClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LogService.getInstance().error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

}
