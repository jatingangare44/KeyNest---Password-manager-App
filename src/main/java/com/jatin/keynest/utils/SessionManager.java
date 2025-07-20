package com.jatin.keynest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "KeyNestSession";
    private static final String LAST_ACTIVE = "last_active";
    private static final long TIMEOUT_DURATION = 60000; // 6 sec

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void updateLastActiveTime() {
        prefs.edit().putLong(LAST_ACTIVE, System.currentTimeMillis()).apply();
    }

    public boolean isSessionExpired() {
        long last = prefs.getLong(LAST_ACTIVE, 0);
        return System.currentTimeMillis() - last > TIMEOUT_DURATION;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
