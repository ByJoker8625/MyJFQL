package de.byjoker.myjfql.network.session;

import com.fasterxml.jackson.databind.JsonNode;
import de.byjoker.myjfql.exception.NetworkException;
import de.byjoker.myjfql.util.Json;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionServiceImpl implements SessionService {

    private final List<Session> sessions;

    public SessionServiceImpl() {
        this.sessions = new ArrayList<>();
    }

    @Override
    public void openSession(@NotNull Session session) {
        if (getSession(session.getToken()) != null) {
            throw new NetworkException("Session token already taken!");
        }

        sessions.add(session);
    }

    @Override
    public void closeSession(@NotNull String token) {
        sessions.removeIf(session -> session.getToken().equals(token));
    }

    @Override
    public void closeSessions(@NotNull String userId) {
        sessions.removeIf(session -> session.getUserId().equals(userId));
    }

    @Override
    public void saveSession(@NotNull Session session) {
        sessions.set(sessions.indexOf(session), session);
    }

    @Override
    public Session getSession(@NotNull String token) {
        return sessions.stream().filter(session -> session.getToken().equals(token)).findFirst().orElse(null);
    }

    @NotNull
    @Override
    public List<Session> getSessionsByUserId(@NotNull String userId) {
        return sessions.stream().filter(session -> session.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Session> getSessions() {
        return sessions;
    }

    @Override
    public void loadAll() {
        loadAll(new File("sessions.json"));
    }

    @Override
    public void loadAll(File backend) {
        final JsonNode node = Json.read(backend);

        sessions.clear();
        node.forEach(entry -> sessions.add(new StaticSession(entry.get("token").asText(), entry.get("userId").asText(), entry.get("databaseId").asText(), Json.convert(entry.get("addresses")))));
    }

    @Override
    public void updateAll() {
        updateAll(new File("sessions.json"));
    }

    @Override
    public void updateAll(File backend) {
        Json.write(sessions.stream().filter(session -> session.getType() == SessionType.STATIC).collect(Collectors.toList()), backend);
    }

}
