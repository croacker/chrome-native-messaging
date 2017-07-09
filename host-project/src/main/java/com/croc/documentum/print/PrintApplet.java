package com.croc.documentum.print;

import java.util.Collections;
import java.util.Map;

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
public class PrintApplet implements IMethod{

    private String data;

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return null;
    }

}
