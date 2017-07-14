package ru.croc.chromenative;

import ru.croc.chromenative.service.JobService;

/**
 * 14.07.2017.
 */
public class ApplicationManager {

    public static final int SHUTDOWN_DELAY = 1000;

    /**
     * Статический экземпляр, замена DI
     */
    private static ApplicationManager instance;

    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    /**
     * Завершить работу приложения.
     */
    public void shutdown() {
        new Thread(new ShutdownRunnable()).start();
    }

    /**
     * Отдельный поток, который остановит выполнение методов и завершит работу приложения.
     */
    private static class ShutdownRunnable implements Runnable {

        @Override
        public void run() {
            delay();
            JobService.getInstance().shutdown();
            System.exit(0);
        }

        /**
         * Приостановить поток, перед остановкой приложения.
         */
        private void delay(){
            try {
                Thread.currentThread().sleep(SHUTDOWN_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
