package ru.croc.chromenative.service.hostmethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ru.croc.chromenative.HostApplication;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.util.Base64;

/**
 * Метод хоста для получения файла в Base64.
 *
 * @author IlAlekseev
 */
public class FileBase64Method implements IMethod {

    /**
     * Количество информации, передаваемое за 1 раз : небольше 1мб + дополнительныя информация.
     */
    private static final int BUFFER_SIZE = 1048000;

    /**
     * Данные полученные в качестве параметра из Browser extension.
     */
    private String pathToFile;

    private int chunkNumber;

    @Override
    public void init(final String data) {
        final int chunkNumberIndex = data.lastIndexOf(":");

        this.chunkNumber = Integer.parseInt(data.substring(chunkNumberIndex + 1));

        this.pathToFile = data.substring(0, chunkNumberIndex);
    }

    @Override
    public String getResult() {
        Base64.InputStream encoder = null;
        try {
            encoder = new Base64.InputStream(new FileInputStream(new File(pathToFile)), Base64.ENCODE);

            final int startPosition = chunkNumber * BUFFER_SIZE;

            final byte[] buffer = new byte[startPosition + BUFFER_SIZE];
            final int readSize = encoder.read(buffer);
            String result = "";
            if (readSize - startPosition > 0) {
                result = new String(buffer, startPosition, readSize - startPosition, "UTF-8");
            }
            return chunkNumber + ":" + result;
        } catch (final IOException e) {
            error(e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (encoder != null) {
                try {
                    encoder.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Вывод в лог информационного сообщения.
     *
     * @param msg
     */
    private static void info(final String msg) {
        LogService.getInstance().info(msg);
    }

    /**
     * Вывод в лог сообщения об ошибке.
     *
     * @param e
     */
    private static void error(final Throwable e) {
        LogService.getInstance().error(e);
    }

}
