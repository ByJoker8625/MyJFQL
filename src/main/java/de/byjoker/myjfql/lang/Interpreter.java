package de.byjoker.myjfql.lang;

import java.util.List;
import java.util.Map;

public interface Interpreter {

    Map<String, List<String>> interpretCommand(String command);

    String parseString(List<String> strings);

    int parseInteger(List<String> strings);

    List<String> parseList(List<String> strings);


}
