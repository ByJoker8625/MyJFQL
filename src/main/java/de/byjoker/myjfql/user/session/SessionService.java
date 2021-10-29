package de.byjoker.myjfql.user.session;

import java.util.List;

public interface SessionService {

    void openSession(Session session);

    void saveSession(Session session);

    void closeSession(String token);

    void closeSessions(String userId);

    void collectExpiresSessions();

    boolean existsSession(String token);

    Session getSession(String token);

    List<Session> getSessionsByUserId(String userId);

    List<Session> getSessions();

}
