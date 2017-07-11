package ru.croc.chromenative.service.hostmethod;

/**
 *
 */
public enum Methods {
    SYSTEM_INFO_APPLET("getSystemInfo" ,SystemInfoMethod.class),
    PRINT_ATTACHMENT_LIST_APPLET("printAttachments", PrintAttachmentListMethod.class),
    PRINT_APPLET("printBarcode", PrintBarcodeMethod.class);

    private Class appletClass;
    private String key;

    public Class getAppletClass() {
        return appletClass;
    }

    Methods(String key, Class appletClass) {
        this.appletClass = appletClass;
        this.key = key;
    }

    public static Methods get(String key){
        Methods result = null;
        for (Methods method: values()){
            if(method.key.equals(key)){
                result = method;
                break;
            }
        }
        return result;
    }

}
