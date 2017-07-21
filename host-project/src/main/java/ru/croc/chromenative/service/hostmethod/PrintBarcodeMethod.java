package ru.croc.chromenative.service.hostmethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;

import ru.croc.chromenative.dto.PrintResult;
import ru.croc.chromenative.service.LogService;
import ru.croc.chromenative.service.MapperService;
import ru.croc.chromenative.util.StringUtils;

/**
 * Печать штрихкода, с выбором принтера для печати.
 * Конверсия: <APPLET id="printApplet"
 * CODE="com.croc.documentum.print.PrintApplet" ARCHIVE="/uht/_0/1bk3vhj10-dvoo/applets/scan/scan.jar"
 * CODEBASE="/uht/applets/scan" width="1px" height="1px" MAYSCRIPT>
 * <param name="printurl" value="/uht/barcodegen?documentId=0900029a808d4eb8"/> </APPLET>
 *
 * @author agumenyuk
 * @since 01.07.2016 17:01
 */
public class PrintBarcodeMethod extends AbstractMethod {

    /**
     * Параметры печати.
     */
    private static class Printable {

        private static final float X = 0f;

        private static final float Y = 0f;

        private static final float WIDTH = 50f;

        private static final float HEIGHT = 40f;
    }

    /**
     * Расположение диалога выбора принтера.
     */
    private static class DialogLocation {

        public static final int X = 200;

        public static final int Y = 200;
    }

    /**
     * Документ.
     */
    public Doc doc;

    /**
     * Задача для принтера.
     */
    private DocPrintJob printerJob;

    /**
     * Параметры печати.
     */
    private HashPrintRequestAttributeSet printRequestAttributeSet;

    @Override
    public String getResult() {
        PrintResult result;
        try {
            result = print();
        } catch (Exception e) {
            error(e.getMessage(), e);
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
        prepare();
        if (printerJob != null) {
            info("Printer Name : " + printerJob.getPrintService());
            result = printDocument();
            info("Done Printing.");
        } else {
            result = getError("printCanceled");// Отменено пользователем, на этапе выбора принтера в диалоге
        }
        return result;
    }

    /**
     * Печать документа.
     */
    private PrintResult printDocument() {
        PrintResult result;
        try {
            printerJob.print(doc, printRequestAttributeSet);
            result = getSuccess(StringUtils.EMPTY);
        } catch (PrintException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Подготовить окужение.
     */
    void prepare() {
        initHashPrintRequestAttributeSet();
        PrintService service = selectPrintService();
        if (service != null) {
            doc = new SimpleDoc(getUrl(), javax.print.DocFlavor.URL.JPEG, null);
            try {
                info("DOC : \n " + doc.getPrintData());
            } catch (IOException e) {
                error(e.getMessage(), e);
            }
            printerJob = service.createPrintJob();
        }
    }

    /**
     * Иницализировать параметры печати
     * 
     * @return
     */
    private HashPrintRequestAttributeSet initHashPrintRequestAttributeSet() {
        printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new MediaPrintableArea(Printable.X,
                Printable.Y,
                Printable.WIDTH,
                Printable.HEIGHT,
                MediaPrintableArea.MM));
        printRequestAttributeSet.add(new Copies(1));
        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
        return printRequestAttributeSet;
    }

    /**
     * Выбрать принтер для печати.
     * 
     * @return принтер
     */
    private PrintService selectPrintService() {
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        return showPrintDialog(defaultService);
    }

    /**
     * Получить список принтеров.
     * 
     * @return
     */
    private PrintService[] getAllPrintServices() {
        PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
        attr_set.add(new Copies(1));
        return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, attr_set);
    }

    /**
     * Отобразить диалог выбора принтера.
     * 
     * @param defaultService
     *            принтер поумолчанию
     * @return
     */
    private PrintService showPrintDialog(PrintService defaultService) {
        PrintService service = defaultService;
        PrintService[] services = getAllPrintServices();
        if (services != null && services.length > 1) {
            service = ServiceUI.printDialog(null,
                    DialogLocation.X,
                    DialogLocation.Y,
                    services,
                    defaultService,
                    DocFlavor.INPUT_STREAM.JPEG,
                    printRequestAttributeSet);
        }
        return service;
    }

    /**
     * URL по которому можно получить ШК.
     * 
     * @return url сервлета с параметром id-документа
     */
    private URL getUrl() {
        URL url;
        try {
            url = new URL(getData());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return url;
    }

    private void info(String msg) {
        LogService.getInstance().info(msg);
    }

    private void error(String msg, Exception e) {
        LogService.getInstance().error(msg, e);
    }

}
