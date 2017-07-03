package com.croc.documentum.print;

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
    @Override
    public String getResult() {
        return null;
    }
}
