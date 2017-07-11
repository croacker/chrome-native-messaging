package ru.croc.chromenative.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.chromenative.HostApplication;
import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.dto.NativeResponse;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.HostMethodsService;
import ru.croc.chromenative.service.MapperService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Callable;

/**
 * Задача на выполнение метода в отдельном потоке.
 */
public class Job implements Callable<String> {

    NativeRequest request;

    public Job(NativeRequest request) {
        this.request = request;
    }

    @Override
    public String call() throws Exception {
        String jobResult = JobResult.OK.getName();
        try {
            NativeResponse response = execute(request);
            sendToExtension(response);
        } catch (Exception e) {
            HostApplication.log(e.getMessage());
            jobResult = JobResult.EXCEPTION.getName();
        }
        return jobResult;
    }

    /**
     * Вызвать выполнения задачи указзанной в запросе от Browser extension.
     * @param request запрос от Browser extension
     * @return
     */
    private NativeResponse execute(NativeRequest request){
        NativeResponse response = new NativeResponse();
        String executeResult = HostMethodsService.getInstance().execute(request);
        response.setStatus(JobResult.OK.getName());
        response.setData(executeResult);
        return response;
    }

    /**
     * Передать результат выполнения в Browser extension.
     * @param response результат выполнения запроса.
     * @throws IOException
     */
    private void sendToExtension(NativeResponse response) throws IOException {
        ObjectMapper mapper = MapperService.getInstance().getMapper();
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue(stringWriter, response);
        String responseJson = stringWriter.toString();
        CommunicateService.getInstance().sendMessage(responseJson);
    }
    
}
