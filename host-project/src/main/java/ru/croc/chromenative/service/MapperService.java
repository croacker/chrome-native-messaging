package ru.croc.chromenative.service;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Сервис трансляции json в объекты и обратно.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class MapperService {

    /**
     * Статический экземпляр, замена DI
     */
    private static MapperService instance;

    public static MapperService getInstance() {
        if (instance == null) {
            instance = new MapperService();
        }
        return instance;
    }

    /**
     * Подготовленный объект для трансляции объекта в json либо обратно.
     * 
     * @return
     */
    public ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return mapper;
    }

    /**
     * Объект в json-строку.
     * 
     * @param obj
     * @return
     */
    public String toString(Object obj) {
        StringWriter stringWriter = new StringWriter();
        try {
            getMapper().writeValue(stringWriter, obj);
        } catch (IOException e) {
            error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return stringWriter.toString();
    }

    /**
     * json-строка в объект
     * 
     * @param json
     *            json-строка
     * @param clazz
     *            класс
     * @return
     */
    public <T> T readValue(String json, Class<T> clazz) {
        T result = null;
        ObjectMapper mapper = MapperService.getInstance().getMapper();
        try {
            result = mapper.readValue(json, clazz);
        } catch (IOException e) {
            error("Error json:{" + json + "} translation to " + clazz);
            error(e.getMessage(), e);
        }
        return result;
     }

    private void error(String msg) {
        LogService.getInstance().error(msg);
    }

    private void error(String msg, Throwable e) {
        LogService.getInstance().error(msg, e);
    }

}
