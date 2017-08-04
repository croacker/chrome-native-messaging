package ru.croc.chromenative.service;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

/**
 * Сервис работы с системными принтерами.
 *
 * @author agumenyuk
 * @since 04.08.2016 17:01
 */
public class SystemPrintService {

    /**
     * Статический экземпляр, замена DI
     */
    private static SystemPrintService instance;

    public static SystemPrintService getInstance() {
        if (instance == null) {
            instance = new SystemPrintService();
        }
        return instance;
    }

    /**
     * Получить принтер пумолчанию.
     * @return
     */
    public PrintService getDefault(){
        return PrintServiceLookup.lookupDefaultPrintService();
    }

    /**
     * Открыть диалог выбора принтера.
     * @param defaultService
     * @param attributeSet
     * @return
     */
    public PrintService select(final PrintService defaultService, final HashPrintRequestAttributeSet attributeSet) {
        PrintService service = defaultService;
        PrintService[] services = getAllPrintServices();
        if (services != null && services.length > 1) {
            service = ServiceUI.printDialog(null,
                    DialogLocation.X,
                    DialogLocation.Y,
                    services,
                    defaultService,
                    DocFlavor.INPUT_STREAM.JPEG,
                    attributeSet);
        }
        return service;
    }

    /**
     * Получить список принтеров.
     *
     * @return
     */
    private PrintService[] getAllPrintServices() {
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(new Copies(1));
        return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, attributeSet);
    }

    /**
     * Расположение диалога выбора принтера.
     */
    private static class DialogLocation {

        public static final int X = 200;

        public static final int Y = 200;
    }

}
