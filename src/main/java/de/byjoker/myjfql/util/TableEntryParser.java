package de.byjoker.myjfql.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.database.TableEntry;
import org.json.JSONObject;

import java.util.Collection;

public class TableEntryParser {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String stringify(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception ex) {
            return null;
        }
    }

    @Deprecated
    public static String stringifyLegacyTableEntries(Collection<TableEntry> entries, Object structure) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", RestCommandSender.ResponseType.RESULT);
        jsonObject.put("structure", structure);
        jsonObject.put("result", entries);

        return jsonObject.toString();
    }

    @Deprecated
    public static String stringifySingletonTableEntries(Collection<String> strings, Object structure, ResultType resultType) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "RESULT");
        jsonObject.put("structure", structure);
        jsonObject.put("result", strings);
        jsonObject.put("resultType", resultType);

        return jsonObject.toString();
    }

    public static String stringifyTableEntries(Collection<TableEntry> entries, Object structure, ResultType resultType) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"RESULT\",\"resultType\":\"").append(resultType).append("\"\"structure\":").append(stringify(structure)).append(",");

        if (entries.size() != 0) {
            jsonBuilder.append("\"result\":[");
            entries.forEach(column -> jsonBuilder.append(column.json()).append(","));
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1).append("]");
        } else {
            jsonBuilder.append("\"result\":[]");
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
