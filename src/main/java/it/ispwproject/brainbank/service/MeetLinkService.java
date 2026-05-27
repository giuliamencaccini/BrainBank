package it.ispwproject.brainbank.service;

import java.util.UUID;

/**
 * Servizio per la generazione di link Meet per le lezioni prenotate.
 * Attualmente genera link Jitsi — in futuro sostituibile con Google Meet API.
 */

public final class MeetLinkService {

    private static final String BASE = "https://meet.jit.si/brainbank-";

    private MeetLinkService() {}

    public static String generate() {
        return BASE + UUID.randomUUID().toString().substring(0, 8);
    }
}