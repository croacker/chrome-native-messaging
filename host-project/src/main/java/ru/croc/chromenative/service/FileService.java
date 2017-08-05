package ru.croc.chromenative.service;

import java.io.File;
import java.io.IOException;

/**
 * Сервис работы с файлами.
 *
 * @author agumenyuk
 * @since 05.08.2016 17:01
 */
public class FileService {

    /**
     * Статический экземпляр, замена DI
     */
    private static FileService instance;

    public static FileService getInstance() {
        if (instance == null) {
            instance = new FileService();
        }
        return instance;
    }

    /**
     * Создать временный файл
     *
     * @param prefix
     * @param suffix
     * @return
     */
    public File createTempFile(final String prefix, final String suffix) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile(prefix, suffix);
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return tmpFile;
    }

}
