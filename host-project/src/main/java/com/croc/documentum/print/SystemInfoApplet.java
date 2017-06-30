package com.croc.documentum.print;

/**
 *
 */
public class SystemInfoApplet  implements IMethod {

    public void init() {
    }

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public String getResult() {
        return getJavaVersion();
    }

}
