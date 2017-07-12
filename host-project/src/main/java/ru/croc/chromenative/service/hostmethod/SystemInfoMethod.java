package ru.croc.chromenative.service.hostmethod;

/**
 * Метод хоста для получения системной информации.
 * Сейчас, только версия JRE.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class SystemInfoMethod implements IMethod {

    /**
     * Данные полученные в качестве параметра из Browser extension.
     */
    private String data;

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return getJavaVersion();
    }

    /**
     * Получить версию JRE
     * @return
     */
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

}
