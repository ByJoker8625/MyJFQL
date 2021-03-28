package org.jokergames.myjfql.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Janick
 */

public class Column {

    private long creation;
    private Map<String, Object> content;

    public Column() {
        this.content = new HashMap<>();
        this.creation = System.currentTimeMillis();
    }

    public Column(final Map<String, Object> content) {
        this.content = content;
        this.creation = System.currentTimeMillis();
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(final long creation) {
        this.creation = creation;
    }

    public Object getContent(final String key) {
        return content.get(key);
    }

    public void putContent(final String key, final Object o) {
        content.put(key, o);
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(final Map<String, Object> content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return creation == column.creation &&
                Objects.equals(content, column.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creation, content);
    }

    @Override
    public String toString() {
        return "Column{" +
                "creation=" + creation +
                ", content=" + content +
                '}';
    }
}
