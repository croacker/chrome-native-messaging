package ru.croc.chromenative.service.hostmethod;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import ru.croc.chromenative.HostApplication;
import ru.croc.chromenative.dto.PrintResult;
import ru.croc.chromenative.service.MapperService;
import ru.croc.chromenative.util.StringUtils;

/**
 * Метод печати вложений. Конверсия: <APPLET id="printApplet"
 * CODE="com.croc.documentum.applet.PrintAttachmentListApplet" ARCHIVE="/uht/_0/1bk3vhj10-9se/applets/print/print.jar"
 * CODEBASE="/uht/applets/print" width="1px" height="1px" MAYSCRIPT>
 * <param name="printurl" value="/uht/attachmentcontenttransfer?objectId=0900029a80864154"/> </APPLET>
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class PrintAttachmentListMethod extends AbstractMethod {

    /**
     * Список временных файлов, полученных для печати от сервлета приложения КСЭД.
     */
    protected List<File> tempFileList;

    @Override
    public String getResult() {
        PrintResult result;
        try {
            result = print();
        } catch (Exception e) {
            HostApplication.error(e);
            result = getError(e.getMessage());
        }
        return MapperService.getInstance().toString(result);
    }

    /**
     * Распечатать файлы на принтер поумолчанию, предварительно запросив файлы по http у сервлета приложения КСЭД.
     * 
     * @return результат печати.
     */
    protected PrintResult print() {
        PrintResult result;
        try {
            String attachmentURL = getData();
            tempFileList = downloadAndUnzip(attachmentURL);
            for (File tempFile : tempFileList) {
                HostApplication.log("PRINTING: " + tempFile.getAbsolutePath());
                printFile(tempFile);
                HostApplication.log("FILE PRINTED: " + tempFile.getAbsolutePath());
            }
            result = getSuccess(StringUtils.EMPTY);
        } catch (final Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Печать файла
     * 
     * @param file
     *            файл
     * @throws Exception
     */
    protected void printFile(final File file) throws Exception {
        printFileViaDesktop(file);
    }

    /**
     * Печать pdf "напрямую"
     * 
     * @param file
     *            файл
     * @return
     */
    protected boolean printPDFDirectly(final File file) {
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        DocPrintJob printerJob;
        printerJob = defaultPrintService.createPrintJob();
        Doc doc = null;
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            doc = new SimpleDoc(fileStream, flavor, null);
            printerJob.print(doc, aset);
        } catch (final Throwable e) {
            return false;
        } finally {
            try {
                if (doc.getStreamForBytes() != null) {
                    doc.getStreamForBytes().close();
                }
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (final Throwable e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Печать файла на принтер поумолчанию.
     * 
     * @param file
     *            файл для печати
     * @throws Exception
     */
    protected void printFileViaDesktop(final File file) throws Exception {
        try {
            Desktop.getDesktop().print(file);
        } catch (final NullPointerException e) {
            throw new Exception("File is null", e);
        } catch (final IllegalArgumentException e) {
            throw new Exception("Не найден файл " + file.getAbsolutePath(), e);
        } catch (final UnsupportedOperationException e) {
            throw new Exception("Текущее окружение рабочего стола не поддерживает функцию печати", e);
        } catch (final SecurityException e) {
            throw new Exception("Доступ, либо возможность печати файла "
                    + file.getAbsolutePath()
                    + " заблокированы текущими настройками безопасности", e);
        } catch (final IOException e) {
            throw new Exception(
                    "Для печати файла " + file.getAbsolutePath() + " в системе не найдено ассоциированное приложение",
                    e);
        } catch (final Throwable e) {
            throw new Exception("Не предусмотренное исключение при печати файла "
                    + file.getAbsolutePath()
                    + "<br/>"
                    + e.getMessage(), e);
        }
    }

    /**
     * Получить по http файлы у сервлета приложения КСЭД.
     * 
     * @param attachmentURL
     *            url сервлета с необходимыми параметрами
     * @return список полученных файлов
     * @throws Exception
     */
    protected List<File> downloadAndUnzip(final String attachmentURL) throws Exception {
        List<File> result = new ArrayList<>();
        InputStream urlStream = null;
        ZipInputStream zippedStream = null;
        try {
            URL url = new URL(getDocumentBase(), attachmentURL);
            urlStream = new BufferedInputStream(url.openStream());
            HostApplication.log("GETTING STREAM FROM: " + url.toString());
            zippedStream = new ZipInputStream(urlStream);
            ZipEntry entry;
            while ((entry = zippedStream.getNextEntry()) != null) {
                File file = createTempFileFromZippedStream(entry.getName(), zippedStream);
                HostApplication.log("TEMP FILE CREATED: " + file.getAbsolutePath());
                result.add(file);
                zippedStream.closeEntry();
            }
        } catch (final Throwable e) {
            String message = "Exception when downloading and uzipping files form " + attachmentURL;
            HostApplication.error(message);
            HostApplication.error(e);
            throw new Exception(message, e);
        } finally {
            try {
                if (urlStream != null) {
                    urlStream.close();
                    HostApplication.log("URL STREAM CLOSED");
                }
                if (zippedStream != null) {
                    zippedStream.close();
                    HostApplication.log("ZIP STREAM CLOSED");
                }
            } catch (final Throwable e) {
                String message = "Exception when closing streams form " + attachmentURL;
                HostApplication.error(message);
                HostApplication.error(e);
                throw new Exception(message, e);
            }
        }
        return result;
    }

    /**
     * Создать временный файл.
     * 
     * @param entryName
     *            наименование
     * @param zippedStream
     *            поток
     * @return временный файл
     * @throws Exception
     */
    protected File createTempFileFromZippedStream(final String entryName, final ZipInputStream zippedStream)
            throws Exception {
        String invalidCharRemoved = entryName.replaceAll("[\\\\/:*?\"<>|]", "-");
        File file = File.createTempFile("attachment-", invalidCharRemoved);
        FileOutputStream fileStream = new FileOutputStream(file);
        copyStream(zippedStream, fileStream);
        fileStream.close();
        return file;
    }

    /**
     * Копировать поток
     * 
     * @param source
     * @param dest
     * @throws Exception
     */
    protected void copyStream(final InputStream source, final OutputStream dest) throws Exception {
        final int initialCapacity = 1024;
        byte data[] = new byte[initialCapacity];
        int count;
        while ((count = source.read(data, 0, initialCapacity)) != -1) {
            dest.write(data, 0, count);
        }
    }

    /**
     * Определение, что файл pdf.
     * 
     * @param file
     *            файл
     * @return
     */
    protected boolean isPDF(final File file) {
        String fileName = file.getAbsolutePath();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.equalsIgnoreCase("pdf");
    }

    /**
     * Получить url из строки.
     * 
     * @return url
     */
    public URL getDocumentBase() {
        URL url;
        try {
            url = new URL(getData());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return url;
    }
}
