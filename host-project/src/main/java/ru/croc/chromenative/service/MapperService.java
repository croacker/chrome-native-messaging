package ru.croc.chromenative.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.chromenative.HostApplication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * Сервис трансляции json в объекты и обратно.
 */
public class MapperService {

    /**
     * Статический экземпляр, замена DI
     */
    private static MapperService instance;

    public static MapperService getInstance() {
        if(instance == null){
            instance = new MapperService();
        }
        return instance;
    }

    /**
     * Подготовленный объект для трансляции объекта в json либо обратно.
     * @return
     */
    public ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return mapper;
    }

    /**
     * Объект в json-строку.
     * @param obj
     * @return
     */
    public String toString(Object obj){
        StringWriter stringWriter = new StringWriter();
        try {
            getMapper().writeValue(stringWriter, obj);
        } catch (IOException e) {
            HostApplication.log(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return stringWriter.toString();
    }

}
