package ru.croc.chromenative.service.hostmethod;

/**
 * Метод хоста для получения системной информации.
 * Сейчас, только версия JRE.
 */
public class SystemInfoMethod implements IMethod {

    private String data;

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return getJavaVersion();
    }

}
