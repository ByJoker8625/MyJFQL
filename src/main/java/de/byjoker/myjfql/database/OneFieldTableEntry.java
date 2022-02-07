package de.byjoker.myjfql.database;

public class OneFieldTableEntry extends LegacyTableEntry {

    public OneFieldTableEntry(String field, String value) {
        this.insert(field, value);
    }

}
