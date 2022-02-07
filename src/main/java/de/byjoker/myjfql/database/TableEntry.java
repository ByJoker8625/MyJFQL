package de.byjoker.myjfql.database;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.byjoker.myjfql.lang.Requirement;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.util.List;
import java.util.Map;

public interface TableEntry {

    Object select(String key);

    String selectStringify(String key);

    void insert(String key, Object value);

    void remove(String key);

    void compile();

    boolean contains(String key);

    boolean containsOrNotNullItem(String key);

    boolean matches(List<List<Requirement>> conditions);

    @JsonIgnore
    @JSONPropertyIgnore
    String json();

    Map<String, Object> getContent();

    void setContent(Map<String, Object> content);

    void applyContent(Map<String, Object> content);

    @JsonGetter(value = "creation")
    @JSONPropertyName("creation")
    long getCreatedAt();

    void setCreatedAt(long createdAt);

}
