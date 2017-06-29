package ru.croc.chromenative;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.dto.NativeResponse;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.JavaAppletService;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class Job implements Runnable {

    NativeRequest request;

    public Job(NativeRequest request) {
        this.request = request;
    }

    public void run() {
        ObjectMapper mapper = new ObjectMapper();

        String reqResult = JavaAppletService.getInstance().execute(request);

        NativeResponse response = new NativeResponse();
        response.setStatus("OK");
        response.setData(reqResult + Thread.currentThread().hashCode());

        StringWriter stringEmp = new StringWriter();
        try {
            mapper.writeValue(stringEmp, response);
            String responseJson = stringEmp.toString();
            CommunicateService.getInstance().sendMessage(responseJson);
//            CommunicateService.getInstance().sendMessage(requestJson);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
