package se.fikaware.tarta.models;

import java.util.HashMap;
import java.util.Map;

public class Session {
    static Map<String, Session> sessions = new HashMap<>();

    public String sessionID;

    Session(String sessionID) {
        this.sessionID = sessionID;
    }

    public static Session startSession() {
        String sessionID = "ok123";
        var session = new Session(sessionID);
        sessions.put(sessionID, session);
        return session;
    }
}
