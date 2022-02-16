package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.byjoker.myjfql.lang.Requirement;

import java.util.List;
import java.util.Map;

public interface TableEntry {

    Object select(String key);

    String selectStringify(String key);

    TableEntry append(String key, Object value);

    void insert(String key, Object value);

    void remove(String key);

    boolean contains(String key);

    boolean containsOrNotNullItem(String key);

    boolean matches(List<List<Requirement>> conditions);

    Map<String, Object> getContent();

    void setContent(Map<String, Object> content);

    void applyContent(Map<String, Object> content);

    @JsonProperty(value = "creation")
    long getCreatedAt();

    void setCreatedAt(long createdAt);

}
