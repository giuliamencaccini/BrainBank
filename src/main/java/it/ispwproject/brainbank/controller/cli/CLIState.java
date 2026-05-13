package it.ispwproject.brainbank.controller.cli;

public enum CLIState {

    // ── Comuni ──────────────────────────────────────────────────────────
    INIZIALE,
    LOGIN,
    REGISTRAZIONE,
    USCITA,

    // ── Student ─────────────────────────────────────────────────────────
    DASHBOARD_STUDENT,
    BOOK_LESSON,
    VIEW_BOOKINGS,
    CANCEL_BOOKING,
    VIEW_TODO,

    // ── Tutor ────────────────────────────────────────────────────────────
    DASHBOARD_TUTOR,
    SET_AVAILABILITY,
    VIEW_SLOTS,
    MANAGE_STUDENTS,
    ASSIGN_ACTIVITY,
    MONITOR_PROGRESS,

    // ── Admin ────────────────────────────────────────────────────────────
    DASHBOARD_ADMIN,
    REPORT_STATISTICS
}