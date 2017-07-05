package com.croc.documentum.print;

import java.util.Map;

/**
 * Created by agumenyuk on 28.06.2017.
 */
public interface IMethod {

    void init(Map<String, String> arguments);

    String getResult();

}
