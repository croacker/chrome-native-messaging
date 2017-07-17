package ru.croc.chromenative.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Сервис трансляции json в объекты и обратно.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class MapperService {

    /**
     * Логгер
     */
    private static Logger log = LogManager.getLogger(MapperService.class);

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
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return stringWriter.toString();
    }

}
