package ru.croc.chromenative.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Сервис работы с сетью.
 *
 * @author agumenyuk
 * @since 05.08.2016 17:01
 */
public class NetworkService {

    /**
     * Статический экземпляр, замена DI
     */
    private static NetworkService instance;

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    private StreamService getStreamService() {
        return StreamService.getInstance();
    }

    /**
     * Скачать файл и вернуть имя временного в качестве которого был записан скачанный.
     * 
     * @param spec
     *            адрес файла, с указанием протокола
     * @return
     */
    public File download(final String spec, final File toFile) {
        return download(getUrl(spec), toFile);
    }

    public File download(final URL url, final File toFile) {
        // File tmpFile = createTempFile("barcode-", "ddddd");
        ReadableByteChannel channel = null;
        try (FileOutputStream fos = new FileOutputStream(toFile)) {
            channel = Channels.newChannel(url.openStream());
            fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            getStreamService().close(channel);
        }
        return toFile;
    }

    /**
     * Получить url по указанной спецификации(адресу).
     * 
     * @param spec
     * @return
     */
    public URL getUrl(final String spec) {
        URL url;
        try {
            url = new URL(spec);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return url;
    }

    /**
     * Получить url для файла.
     * 
     * @param file
     * @return
     */
    public URL getUrl(final File file) {
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return url;
    }
}
