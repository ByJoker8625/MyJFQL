package de.byjoker.myjfql.network.util;

import java.util.Collections;
import java.util.List;

public class LegacySessionResult extends Response {

    private final List<String> structure = Collections.singletonList("Token");
    private final List<String> result;

    public LegacySessionResult(String token) {
        super(ResponseType.RESULT);
        this.result = Collections.singletonList(token);
    }

    public List<String> getStructure() {
        return structure;
    }

    public List<String> getResult() {
        return result;
    }
}
