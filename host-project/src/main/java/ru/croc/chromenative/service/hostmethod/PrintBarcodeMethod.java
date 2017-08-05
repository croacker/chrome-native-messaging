package ru.croc.chromenative.service.hostmethod;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;

import ru.croc.chromenative.dto.PrintResult;
import ru.croc.chromenative.service.FileService;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.MapperService;
import ru.croc.chromenative.service.NetworkService;
import ru.croc.chromenative.service.SystemPrintService;
import ru.croc.chromenative.util.StringUtils;

/**
 * Печать штрихкода, с выбором принтера для печати. Конверсия: <APPLET id="printApplet"
 * CODE="com.croc.documentum.print.PrintApplet" ARCHIVE="/uht/_0/1bk3vhj10-dvoo/applets/scan/scan.jar"
 * CODEBASE="/uht/applets/scan" width="1px" height="1px" MAYSCRIPT>
 * <param name="printurl" value="/uht/barcodegen?documentId=0900029a808d4eb8"/> </APPLET>
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class PrintBarcodeMethod extends AbstractMethod {

    /**
     * Префикс для временного файла.
     */
    private static final String FILE_PREFIX = "barcode-";

    /**
     * Параметр запроса.
     */
    private static final String QUERY_PARAMETER_ID = "documentId=";

    /**
     * Параметры области печати.
     */
    private static class Printable {

        private static final float X = 0f;

        private static final float Y = 0f;

        private static final float WIDTH = 50f;

        private static final float HEIGHT = 40f;
    }

    private SystemPrintService getSystemPrintService() {
        return SystemPrintService.getInstance();
    }

    private NetworkService getNetworkService() {
        return NetworkService.getInstance();
    }

    private FileService getFileService() {
        return FileService.getInstance();
    }

    public static void main(String[] args) {
        PrintBarcodeMethod method = new PrintBarcodeMethod();
        method.init("http://127.0.0.1:8082/dev/barcodegen?documentId=0900029a8004eb93");
        method.getDocument();
        // method.init("file:///d:/tmp/slon.jpg");
//        method.getResult();
    }

    @Override
    public String getResult() {
        PrintResult result;
        try {
            result = print();
        } catch (final Exception e) {
            error(e);
            result = getError(e.getMessage());
        }
        return MapperService.getInstance().toString(result);
    }

    /**
     * Выбрать принтер и напечатать.
     * 
     * @return
     */
    private PrintResult print() {
        PrintResult result;
        PrintRequestAttributeSet attributeSet = getPrintRequestAttributes();
        DocPrintJob printerJob = prepare(attributeSet);
        if (printerJob != null) {
            info("Printer Name : " + printerJob.getPrintService());
            result = printDocument(printerJob, attributeSet);
            info("Done Printing.");
        } else {
            result = getError("printCanceled");// Отменено пользователем, на этапе выбора принтера в диалоге
        }
        return result;
    }

    /**
     * Печать документа.
     */
    private PrintResult printDocument(final DocPrintJob printerJob, final PrintRequestAttributeSet attributeSet) {
        PrintResult result;
        Doc doc = getDocument();
        try {
            printerJob.print(doc, attributeSet);
            result = getSuccess(StringUtils.EMPTY);
        } catch (final PrintException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Подготовить задание для принтера.
     * 
     * @param attributeSet
     */
    private DocPrintJob prepare(final PrintRequestAttributeSet attributeSet) {
        DocPrintJob printerJob = null;
        PrintService service = selectPrintService(attributeSet);
        if (service != null) {
            printerJob = service.createPrintJob();
        }
        return printerJob;
    }

    /**
     * Получить объект документа для печати.
     * 
     * @return
     */
    private Doc getDocument() {
        URL fileUrl = getFileUrl();
        Doc doc = new SimpleDoc(fileUrl, javax.print.DocFlavor.URL.JPEG, null);
        info(doc);
        return doc;
    }

    private URL getFileUrl() {
        URL url = getUrl(getData());
        if (!url.getProtocol().equals("file")) {
            File tmpFile = getFileService().createTempFile(FILE_PREFIX, getFileSuffix(url));
            tmpFile = getNetworkService().download(url, tmpFile);
            url = getNetworkService().getUrl(tmpFile);
        }
        return url;
    }

    /**
     * Иницализировать параметры печати
     * 
     * @return
     */
    private PrintRequestAttributeSet getPrintRequestAttributes() {
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(getPrintArea());
        attributeSet.add(new Copies(1));
        attributeSet.add(OrientationRequested.PORTRAIT);
        return attributeSet;
    }

    /**
     * Участок печати.
     * 
     * @return
     */
    private MediaPrintableArea getPrintArea() {
        return new MediaPrintableArea(Printable.X,
                Printable.Y,
                Printable.WIDTH,
                Printable.HEIGHT,
                MediaPrintableArea.MM);
    }

    /**
     * Выбрать принтер для печати.
     * 
     * @return принтер
     * @param attributeSet
     */
    private PrintService selectPrintService(final PrintRequestAttributeSet attributeSet) {
        PrintService defaultService = getSystemPrintService().getDefault();
        return getSystemPrintService().showDialog(defaultService, attributeSet);
    }

    /**
     * URL по которому можно получить ШК.
     * 
     * @return url сервлета с параметром id-документа
     */
    private URL getUrl(final String spec) {
        return getNetworkService().getUrl(spec);
    }

    /**
     * Получить суффикс для файла из url
     * @param url
     * @return
     */
    private String getFileSuffix(final URL url){
        return url.getQuery().replace(QUERY_PARAMETER_ID, StringUtils.EMPTY);
    }

    private void info(final Doc doc) {
        try {
            info("DOC : \n " + doc.getPrintData());
        } catch (final IOException e) {
            error(e);
        }
    }

    private void info(final String msg) {
        LogService.getInstance().info(msg);
    }

    private void error(final Exception e) {
        LogService.getInstance().error(e);
    }

}
