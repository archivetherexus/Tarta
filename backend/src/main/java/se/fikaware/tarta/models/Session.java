package se.fikaware.tarta.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private static Map<String, Session> sessions = new HashMap<>();

    public String sessionID;

    public User user = null;

    Session(String sessionID) {
        this.sessionID = sessionID;
    }

    public static Session startSession() {
        var sessionID = UUID.randomUUID().toString();
        var session = new Session(sessionID);
        sessions.put(sessionID, session);
        return session;
    }

    public static Session continueSession(String sessionID) {
        return sessions.get(sessionID);
    }

    public static void stopAllSessions() {
        sessions.clear();
    }
}
