package ru.croc.chromenative.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * 
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class Job implements Callable<String> {

    /**
     * Логгер
     */
    private static Logger log = LogManager.getLogger(Job.class);

    /**
     * Транслированный из json-запрос от Browser extension.
     */
    private NativeRequest request;

    /**
     * Конструктор.
     * 
     * @param request
     *            транслированный из json-запрос от Browser extension.
     */
    public Job(NativeRequest request) {
        this.request = request;
    }

    /**
     * Контрактный метод интерфейса Callable
     * 
     * @return String-объект, результат выполнения.
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        String jobResult = JobResult.OK.getName();
        try {
            NativeResponse response = execute(request);
            sendToExtension(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jobResult = JobResult.EXCEPTION.getName();
        }
        return jobResult;
    }

    /**
     * Вызвать выполнения задачи указзанной в запросе от Browser extension.
     * 
     * @param request
     *            запрос от Browser extension
     * @return Future-объект для асинхронного отслеживания результатов выполнения.
     */
    private NativeResponse execute(NativeRequest request) {
        NativeResponse response = new NativeResponse();
        String executeResult = HostMethodsService.getInstance().execute(request);
        response.setStatus(JobResult.OK.getName());
        response.setData(executeResult);
        return response;
    }

    /**
     * Передать результат выполнения в Browser extension.
     * 
     * @param response
     *            результат выполнения запроса.
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
