package ru.croc.chromenative.service;

import ru.croc.chromenative.dto.NativeRequest;
import ru.croc.chromenative.job.Job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Сервис для запуска задач в отдельных потоках.
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class JobService {

    /**
     * Количество потоков выполнения.
     */
    public static final int THREAD_COUNT = 5;

    /**
     * Статический экземпляр, замена DI
     */
    private static JobService instance;

    private ExecutorService executorService;

    public static JobService getInstance() {
        if (instance == null) {
            instance = new JobService();
        }
        return instance;
    }

    private JobService() {
        init();
    }

    /**
     * Инициализация сервиса.
     */
    private void init() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    /**
     * Выполшнить запрос в отдельном потоке.
     * 
     * @param request
     *            запрос от Browser extension
     */
    public Future<String> submit(NativeRequest request) {
        return executorService.submit(new Job(request));
    }

    /**
     * Завершить работу.
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Завершить работу немедленно.
     */
    public void shutdownNow() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
