package com.croc.documentum.print;

/**
 *
 */
public class SystemInfoApplet  implements IMethod {

    private String data;

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        return getJavaVersion();
    }

}
