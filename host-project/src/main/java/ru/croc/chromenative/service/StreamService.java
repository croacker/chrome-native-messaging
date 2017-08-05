package ru.croc.chromenative.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Сервис работы с системными принтерами.
 *
 * @author agumenyuk
 * @since 05.08.2016 17:01
 */
public class StreamService {

    /**
     * Размер буфера при копировании потоков
     */
    private static int BUFFER_SIZE = 1024;

    /**
     * Статический экземпляр, замена DI
     */
    private static StreamService instance;

    public static StreamService getInstance() {
        if (instance == null) {
            instance = new StreamService();
        }
        return instance;
    }

    /**
     * Копировать поток
     *
     * @param source
     * @param dest
     * @throws Exception
     */
    public void copyStream(final InputStream source, final OutputStream dest) throws Exception {
        byte data[] = new byte[BUFFER_SIZE];
        int count;
        while ((count = source.read(data, 0, BUFFER_SIZE)) != -1) {
            dest.write(data, 0, count);
        }
    }

    /**
     * Закрыть(поток).
     * @param closeable
     */
    public void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
