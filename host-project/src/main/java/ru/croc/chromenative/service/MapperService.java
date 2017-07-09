package ru.croc.chromenative.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.chromenative.HostApplication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * Created by agumenyuk on 09.07.2017.
 */
public class MapperService {

    private static MapperService instance;

    public static MapperService getInstance() {
        if(instance == null){
            instance = new MapperService();
        }
        return instance;
    }

    public ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return mapper;
    }

    public String toString(Object obj){
        StringWriter stringWriter = new StringWriter();
        try {
            getMapper().writeValue(stringWriter, obj);
        } catch (IOException e) {
            HostApplication.getLOGGER().log(Level.INFO, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return stringWriter.toString();
    }

}
