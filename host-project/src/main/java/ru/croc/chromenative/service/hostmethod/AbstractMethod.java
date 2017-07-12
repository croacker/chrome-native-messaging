package ru.croc.chromenative.service.hostmethod;

import ru.croc.chromenative.dto.PrintResult;

/**
 * Некоторый абстрактный метод хоста.
 * 
 * @author agumenyuk
 * @since 01.07.2016 17:01
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

    /**
     * Получить результат-ошибка.
     * 
     * @param message
     *            сообщение для передачи в результат
     * @return Объект-результат выполнения.
     */
    protected PrintResult getError(String message) {
        return new PrintResult(ResultStatus.ERROR.getName(), message);
    }

    /**
     * Получить результат-успех.
     * 
     * @param message
     *            сообщение для передачи в результат
     * @return Объект-результат выполнения.
     */
    protected PrintResult getSuccess(String message) {
        return new PrintResult(ResultStatus.SUCCESS.getName(), message);
    }
}
