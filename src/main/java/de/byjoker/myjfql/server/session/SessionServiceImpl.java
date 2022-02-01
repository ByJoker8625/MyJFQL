package de.byjoker.myjfql.server.session;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.util.FileFactory;
import de.byjoker.myjfql.util.IDGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionServiceImpl implements SessionService {

    private final List<Session> sessions;
    private final FileFactory factory;

    public SessionServiceImpl() {
        sessions = new ArrayList<>();
        factory = new FileFactory();
    }

    @Override
    public void openSession(Session session) {
        if (existsSession(session.getToken())) {
            session.setToken(IDGenerator.generateMixed(25));
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
    public void collectExpiresSessions() {
        sessions.removeIf(Session::isExpired);
    }

    @Override
    public boolean existsSession(String token) {
        return sessions.stream().anyMatch(session -> session.getToken().equals(token) && !session.isExpired());
    }

    @Override
    public Session getSession(String token) {
        return sessions.stream().filter(session -> session.getToken().equals(token) && !session.isExpired()).findFirst().orElse(null);
    }

    @Override
    public List<Session> getSessionsByUserId(String userId) {
        return sessions.stream().filter(session -> session.getUserId().equals(userId) && !session.isExpired()).collect(Collectors.toList());
    }

    @Override
    public List<Session> getSessions() {
        return sessions.stream().filter(session -> !session.isExpired()).collect(Collectors.toList());
    }

    @Override
    public void loadAll() {
        loadAll(new File("sessions.json"));
    }

    @Override
    public void loadAll(File space) {
        if (MyJFQL.getInstance().getConfig().memorySessions())
            return;

        sessions.clear();

        try {
            final JSONObject jsonObject = factory.load(space);
            final JSONArray jsonSessions = jsonObject.getJSONArray("sessions");

            for (int i = 0; i < jsonSessions.length(); i++) {
                final JSONObject jsonSession = jsonSessions.getJSONObject(i);

                final String userId = jsonSession.getString("userId");
                final String token = jsonSession.getString("token");

                if (userId.contains("%") || userId.contains("#") || userId.contains("'")) {
                    MyJFQL.getInstance().getConsole().logWarning("UserId used unauthorized characters in the id!");
                } else {
                    if (!MyJFQL.getInstance().getUserService().existsUser(userId)) {
                        MyJFQL.getInstance().getConsole().logWarning("Unknown userId in session!");
                    } else if (token.contains("%") || token.contains("#") || token.contains("'")) {
                        MyJFQL.getInstance().getConsole().logWarning("Token used unauthorized characters in the id!");
                    } else {
                        sessions.add(new Session(token, userId, jsonSession.getString("databaseId"), jsonSession.getString("address"), jsonSession.getLong("open"), jsonSession.getLong("expire")));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateAll() {
        updateAll(new File("sessions.json"));
    }

    @Override
    public void updateAll(File space) {
        if (MyJFQL.getInstance().getConfig().memorySessions())
            return;

        List<Session> sessions = new ArrayList<>(getSessions());
        sessions.removeIf(session -> session.getUserId().equals("%CONSOLE%"));

        try {
            factory.save(space, new JSONObject().put("sessions", sessions));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
