package com.croc.documentum.print;

import ru.croc.chromenative.HostApplication;

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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

/**
 *Конверсия:
 * <APPLET
 id="printApplet"
 CODE="com.croc.documentum.print.PrintApplet"
 ARCHIVE="/uht/_0/1bk3vhj10-dvoo/applets/scan/scan.jar"
 CODEBASE="/uht/applets/scan"
 width="1px"
 height="1px"
 MAYSCRIPT>
 <param name="printurl" value="/uht/barcodegen?documentId=0900029a808d4eb8"/>
 </APPLET>
 */
public class BarcodeApplet implements IMethod{

    String printUrl;
    public Doc doc;
    DocPrintJob printerJob;
    private PrintRequestAttributeSet printRequestAttributeSet;

    private String data;

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return null;
    }

    void print() {
        prep();
        if (printerJob != null) {
            System.out.println("Printer Name : " + printerJob.getPrintService());
            try {
                printerJob.print(doc, printRequestAttributeSet);
            } catch (final PrintException e) {
                HostApplication.getLOGGER().log(Level.INFO, e.getMessage());
            }
//            System.out.println("Done Printing.");
//            win.eval("printFinish();");
        } else {
//            win.eval("printCanceled();");
        }

    }

    void prep() {
        URL url = null;
        printRequestAttributeSet = new HashPrintRequestAttributeSet();
        // printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        final float printableWidth = 50f;
        final float printableHeight = 40f;
        printRequestAttributeSet.add(new MediaPrintableArea(0f, 0f, printableWidth, printableHeight, MediaPrintableArea.MM));
        printRequestAttributeSet.add(new Copies(1));
        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);

        try {
            url = new URL(printUrl);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }

        PrintService pservices = PrintServiceLookup.lookupDefaultPrintService();
        System.out.println(pservices.getName());

        DocFlavor flavor = javax.print.DocFlavor.INPUT_STREAM.JPEG;
        PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
        attr_set.add(new Copies(1));
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, attr_set);
        PrintService service = pservices;
        if (services != null && services.length > 1) {
            final int dialogLocationX = 200;
            final int dialogLocationY = 200;
            service = ServiceUI.printDialog(null, dialogLocationX, dialogLocationY, services, pservices, flavor, printRequestAttributeSet);
        }

        if (service != null) {
            doc = new SimpleDoc(url, javax.print.DocFlavor.URL.JPEG, null);
            try {
                System.out.println("DOC : \n " + doc.getPrintData());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            printerJob = service.createPrintJob();
        }

    }



}
