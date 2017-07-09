package ru.croc.chromenative;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.dto.NativeResponse;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.JavaAppletService;
import ru.croc.chromenative.service.MapperService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public class Job implements Runnable {

    NativeRequest request;

    public Job(NativeRequest request) {
        this.request = request;
    }

    public void run() {
        ObjectMapper mapper = MapperService.getInstance().getMapper();

        String reqResult = JavaAppletService.getInstance().execute(request);

        NativeResponse response = new NativeResponse();
        response.setStatus(Result.OK.getName());
        response.setData(reqResult);

        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, response);
            String responseJson = stringWriter.toString();
            CommunicateService.getInstance().sendMessage(responseJson);
        } catch (IOException e) {
            HostApplication.getLOGGER().log(Level.INFO, e.getMessage());
        }
    }

    /**
     * Статус выполнения операции.
     */
    private enum Result{
        OK("ok");

        private final String name;

        public String getName() {
            return name;
        }

        Result(String name) {
            this.name = name;
        }
    }
}
