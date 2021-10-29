package de.byjoker.myjfql.user.session;

import de.byjoker.jfql.util.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionServiceImpl implements SessionService {

    private final List<Session> sessions;

    public SessionServiceImpl() {
        sessions = new ArrayList<>();
    }

    @Override
    public void openSession(Session session) {
        if (existsSession(session.getToken())) {
            session.setToken(ID.generateMixed().toString());
            openSession(session);
            return;
        }

        saveSession(session);
    }

    @Override
    public void saveSession(Session session) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getToken().equals(session.getToken())) {
                sessions.set(i, session);
                return;
            }
        }

        sessions.add(session);
    }

    @Override
    public void closeSession(String token) {
        sessions.removeIf(session -> session.getToken().equals(token));
    }

    @Override
    public void closeSessions(String userId) {
        sessions.removeIf(session -> session.getUserId().equals(userId));
    }

    @Override
    public boolean existsSession(String token) {
        return sessions.stream().anyMatch(session -> session.getToken().equals(token));
    }

    @Override
    public Session getSession(String token) {
        return sessions.stream().filter(session -> session.getToken().equals(token)).findFirst().orElse(null);
    }

    @Override
    public List<Session> getSessionsByUserId(String userId) {
        return sessions.stream().filter(session -> session.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Session> getSessions() {
        return sessions;
    }

}
