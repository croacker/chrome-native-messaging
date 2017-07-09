package ru.croc.chromenative;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.service.CommunicateService;
import ru.croc.chromenative.service.MapperService;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * То
 */
public class MainCls {

    private static Logger LOGGER;

    public static final String LOG_FILENAME = "d:/tmp/1/extension.log";

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

    private static MainCls instance;

    public static MainCls getInstance() {
        if(instance == null){
            instance = new MainCls();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {
        try {
            getInstance().run();
            System.exit(0);
        }catch (Exception e){
            getLOGGER().log(Level.INFO, e.getMessage());
        }
    }

    private void run() throws Exception {
        boolean stop = false;
        for(;;) {
            String requestJson = CommunicateService.getInstance().readMessage(System.in);
            //Для тестирования. В последствие удалить
//            String requestJson = "{\"method\":\"swingTestApplet\",\"data\":\"java\"}";
            getLOGGER().log(Level.INFO, "Request JSON: " + requestJson);
            ObjectMapper mapper = MapperService.getInstance().getMapper();
            NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
            new Thread(new Job(request)).start();
            if(stop){
                break;
            }
        }
    }

    private static Logger initLogger() throws IOException {
        Logger logger = Logger.getLogger(MainCls.class.getName());
        FileHandler fileHandler = new FileHandler(LOG_FILENAME);
        fileHandler.setLevel(Level.ALL);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        return logger;
    }
}