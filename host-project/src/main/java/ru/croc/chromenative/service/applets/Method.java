package ru.croc.chromenative.service.applets;

import com.croc.documentum.print.PrintApplet;
import com.croc.documentum.print.PrintAttachmentListApplet;
import com.croc.documentum.print.SwingTestApplet;
import com.croc.documentum.print.SystemInfoApplet;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public enum Method {
    SYSTEM_INFO_APPLET("getSystemInfo" ,SystemInfoApplet.class),
    SWING_TEST_APPLET("swingTestApplet" ,SwingTestApplet.class),
    PRINT_ATTACHMENT_LIST_APPLET("printAttachmentListApplet", PrintAttachmentListApplet.class),
    PRINT_APPLET("printApplet", PrintApplet.class);

    private Class appletClass;
    private String key;

    public Class getAppletClass() {
        return appletClass;
    }

    Method(String key, Class appletClass) {
        this.appletClass = appletClass;
        this.key = key;
    }

    public static Method get(String key){
        Method result = null;
        for (Method method: values()){
            if(method.key.equals(key)){
                result = method;
                break;
            }
        }
        return result;
    }

}
