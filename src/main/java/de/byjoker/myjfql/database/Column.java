package de.byjoker.myjfql.database;

import de.byjoker.myjfql.lang.Requirement;

import java.util.List;
import java.util.Map;

public interface Column {

    Object getItem(String key);

    String getStringifyItem(String key);

    void setItem(String key, Object value);

    void removeItem(String key);

    boolean containsItem(String key);

    boolean containsOrNotNullItem(String key);

    boolean matches(List<List<Requirement>> conditions);

    Map<String, Object> getContent();

    void setContent(Map<String, Object> content);

    long getCreatedAt();

}
