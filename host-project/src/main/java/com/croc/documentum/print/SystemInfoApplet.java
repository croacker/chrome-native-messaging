package com.croc.documentum.print;

import java.util.Collections;
import java.util.Map;

/**
 *
 */
public class SystemInfoApplet  implements IMethod {

    private Map<String, String> arguments = Collections.EMPTY_MAP;

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public void init(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String getResult() {
        return getJavaVersion();
    }

}
