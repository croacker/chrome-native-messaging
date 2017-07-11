package ru.croc.chromenative;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.MapperService;
import ru.croc.chromenative.util.StringUtils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * То
 */
public class HostApplication {

    private static Logger LOGGER;

    public static final String LOG_FILENAME = "croc_external_app.log";

    public static Logger getLOGGER() {
        if(LOGGER == null){
            try {
                LOGGER = initLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return LOGGER;
    }

    private static HostApplication instance;

    public static HostApplication getInstance() {
        if(instance == null){
            instance = new HostApplication();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {
        try {
            getInstance().run();
            System.exit(0);
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    private void run() throws Exception {
        boolean stop = false;
        for(;;) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
            log("Request JSON: " + requestJson);
            ObjectMapper mapper = MapperService.getInstance().getMapper();
            NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
            new Thread(new Job(request)).start();
            if(stop){
                break;
            }
        }
    }

    private static Logger initLogger() throws IOException {
        Logger logger = Logger.getLogger(HostApplication.class.getName());
        FileHandler fileHandler = new FileHandler(LOG_FILENAME);
        fileHandler.setLevel(Level.ALL);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    public static void log(String message){
        getLOGGER().log(Level.INFO, message);
    }
}