package de.byjoker.myjfql.lang;

import java.util.List;
import java.util.Map;

public interface CommandFormatter {

    Map<String, List<String>> formatCommand(String command);

    String formatString(List<String> strings);

    int formatInteger(List<String> strings);

    List<String> formatList(List<String> strings);


}
