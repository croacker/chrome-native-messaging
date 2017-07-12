package ru.croc.chromenative.service.hostmethod;

/**
 * Интерфейс-контракт метода Native application.
 * 
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public interface IMethod {

    /**
     * Инициализация метода.
     *
     * @param data
     *            данные в виде строки, возм. json.
     */
    void init(String data);

    /**
     * Получить результат выполнения. Фактически вызвать выполнение метода.
     * 
     * @return строка(json) выполнения метода.
     */
    String getResult();

}
