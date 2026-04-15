package com.example.qrcrowdmanagement.model;

public enum EntryStatus {
    ALLOWED,
    DENIED,

    /**
     * Legacy DB value (older schema/data). Treat as ALLOWED.
     */
    ENTERED,

    /**
     * Legacy DB value (older schema/data). Treat as ALLOWED.
     */
    EXITED
}

