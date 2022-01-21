package de.byjoker.myjfql.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.database.Column;
import org.json.JSONObject;

import java.util.Collection;

public class JsonColumnParser {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String stringify(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception ex) {
            return null;
        }
    }

    @Deprecated
    public static String stringifyLegacyColumns(Collection<Column> columns, Object structure) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", RestCommandSender.ResponseType.RESULT);
        jsonObject.put("structure", structure);
        jsonObject.put("result", columns);

        return jsonObject.toString();
    }

    public static String stringifySingletonColumn(Collection<String> strings, Object structure) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "RESULT");
        jsonObject.put("structure", structure);
        jsonObject.put("result", strings);

        return jsonObject.toString();
    }

    public static String stringifyCompiledColumns(Collection<Column> columns, Object structure) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"RESULT\",\"structure\":").append(stringify(structure)).append(",");

        if (columns.size() != 0) {
            jsonBuilder.append("\"result\":[");
            columns.forEach(column -> jsonBuilder.append(column.json()).append(","));
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1).append("]");
        } else {
            jsonBuilder.append("\"result\":[]");
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
