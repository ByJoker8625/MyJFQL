package de.byjoker.myjfql.core.lang;

import java.util.List;
import java.util.Map;

public interface Formatter {

    Map<String, List<String>> formatCommand(String command);

    String formatString(List<String> strings);

    int formatInteger(List<String> strings);

    List<String> formatList(List<String> strings);


}
