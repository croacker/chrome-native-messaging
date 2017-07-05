package com.croc.documentum.print;

import netscape.javascript.JSObject;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *Конверсия:
 * <APPLET
 id="printApplet"
 CODE="com.croc.documentum.applet.PrintAttachmentListApplet"
 ARCHIVE="/uht/_0/1bk3vhj10-9se/applets/print/print.jar"
 CODEBASE="/uht/applets/print"
 width="1px"
 height="1px"
 MAYSCRIPT>
 <param name="printurl" value="/uht/attachmentcontenttransfer?objectId=0900029a80864154"/>
 </APPLET>
 */
public class PrintAttachmentListApplet implements IMethod{

    @Override
    public String getResult() {
        return null;
    }

    protected List<File> tempFileList;

    String version = "02.07.2013 1";

    private String printUrl;

    private JSObject window;

    public void start() {
        printUrl = getParameter("printurl");
        window = JSObject.getWindow(this);
        final int appletWidth = 5;
        final int appletHeight = 5;
        setSize(appletWidth, appletHeight);
        print(printUrl);
    }

    public void stop() {
        /*
         * if (tempFileList != null){ for (File file : tempFileList){ System.out.println("DELETING FILE: "
         * +file.getAbsolutePath()); file.delete(); System.out.println("FILE DELETED: "+file.getAbsolutePath()); } }
         */
    }

    protected void print(final String attachmentURL) {
        try {

            tempFileList = downloadAndUnzip(attachmentURL);

            for (File tempFile : tempFileList) {
                System.out.println("PRINTING: " + tempFile.getAbsolutePath());
                printFile(tempFile);
                System.out.println("FILE PRINTED: " + tempFile.getAbsolutePath());
            }

        } catch (final Throwable e) {
            e.printStackTrace();
        } finally {
            closeComponent();
        }
    }

    protected void printFile(final File file) throws Exception {

        printFileViaDesktop(file);

        // if (! isPDF(file)){
        //
        // printFileViaDesktop(file);
        //
        // } else {
        //
        // if (! printPDFDirectly(file)){
        // printFileViaDesktop(file);
        // }
        // }

    }

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

    protected List<File> downloadAndUnzip(final String attachmentURL) throws Exception {
        List<File> result = new ArrayList<>();

        InputStream urlStream = null;
        ZipInputStream zippedStream = null;
        try {
            URL url = new URL(getDocumentBase(), attachmentURL);

            urlStream = new BufferedInputStream(url.openStream());
            System.out.println("GETTING STREAM FROM: " + url.toString());

            zippedStream = new ZipInputStream(urlStream);

            ZipEntry entry;
            while ((entry = zippedStream.getNextEntry()) != null) {

                File file = createTempFileFromZippedStream(entry.getName(), zippedStream);
                System.out.println("TEMP FILE CREATED: " + file.getAbsolutePath());

                result.add(file);

                zippedStream.closeEntry();
            }

        } catch (final Throwable e) {
            throw new Exception("Exception when downloading and uzipping files form " + attachmentURL, e);
        } finally {

            try {
                if (urlStream != null) {
                    urlStream.close();
                    System.out.println("URL STREAM CLOSED");
                }

                if (zippedStream != null) {
                    zippedStream.close();
                    System.out.println("ZIP STREAM CLOSED");
                }
            } catch (final Throwable e) {
                throw new Exception("Exception when closing streams form " + attachmentURL, e);
            }

        }

        return result;

    }

    protected File createTempFileFromZippedStream(final String entryName, final ZipInputStream zippedStream)
            throws Exception {
        String invalidCharRemoved = entryName.replaceAll("[\\\\/:*?\"<>|]", "-");
        File file = File.createTempFile("attachment-", invalidCharRemoved);
        FileOutputStream fileStream = new FileOutputStream(file);
        copyStream(zippedStream, fileStream);
        fileStream.close();
        return file;
    }

    protected void copyStream(final InputStream source, final OutputStream dest) throws Exception {
        final int initialCapacity = 1024;
        byte data[] = new byte[initialCapacity];
        int count;
        while ((count = source.read(data, 0, initialCapacity)) != -1) {
            dest.write(data, 0, count);
        }
    }

    protected void closeComponent() {
        window.eval("printFinish();");
    }

    protected boolean isPDF(final File file) {
        String fileName = file.getAbsolutePath();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.equalsIgnoreCase("pdf");
    }
}
