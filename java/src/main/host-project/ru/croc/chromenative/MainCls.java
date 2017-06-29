package ru.croc.chromenative;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.dto.NativeResponse;
import ru.croc.chromenative.service.CommunicateService;

import javax.swing.*;

/**
 *
 */
public class MainCls {

    private static MainCls instance;

    public static MainCls getInstance() {
        if(instance == null){
            instance = new MainCls();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {
        getInstance().run();
        System.exit(0);
    }

    private void run() throws Exception {
        boolean stop = false;
        for(;;) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
//            String requestJson = "{\"method\":\"swingTestApplet\",\"data\":\"java\"}";

            ObjectMapper mapper = new ObjectMapper();
            NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);

            new Thread(new Job(request)).start();

//            NativeResponse response = new NativeResponse();
//            response.setStatus("OK");
//            response.setData("Hello, " + request.getMethod() + "!");
//
//            StringWriter stringEmp = new StringWriter();
//            mapper.writeValue(stringEmp, response);
//            String responseJson = stringEmp.toString();
//            CommunicateService.getInstance().sendMessage(responseJson);
//            CommunicateService.getInstance().sendMessage(requestJson);

            if(stop){
                break;
            }
        }
    }
}