-- ══════════════════════════════
--  BRAINBANK DATABASE — SCHEMA
-- ══════════════════════════════

DROP SCHEMA IF EXISTS brainbankdb;
CREATE SCHEMA brainbankdb;
USE brainbankdb;

-- ══════════════════════════════
--  TABLES
-- ══════════════════════════════

CREATE TABLE brainbankdb.user (
    id INT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'TUTOR', 'ADMIN') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.tutor_detail (
    user_id INT NOT NULL,
    bio VARCHAR(500),
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.subject (
    id INT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.tutor_subject (
    tutor_id INT NOT NULL,
    subject_id INT NOT NULL,
    PRIMARY KEY (tutor_id, subject_id),
    FOREIGN KEY (tutor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (subject_id)
        REFERENCES subject(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.time_slot (
    id INT AUTO_INCREMENT,
    tutor_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    reserved_until DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tutor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.booking (
    id INT AUTO_INCREMENT,
    student_id INT NOT NULL,
    tutor_id INT NOT NULL,
    subject_id INT NOT NULL,
    slot_id INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING',
    meet_link VARCHAR(200),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (tutor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (subject_id)
        REFERENCES subject(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (slot_id)
        REFERENCES time_slot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.activity (
    id INT AUTO_INCREMENT,
    tutor_id INT NOT NULL,
    student_id INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (tutor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (student_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.progress (
    id INT AUTO_INCREMENT,
    tutor_id INT NOT NULL,
    student_id INT NOT NULL,
    notes VARCHAR(500) NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (tutor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (student_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brainbankdb.student_favourite_tutor (
    student_id INT NOT NULL,
    tutor_id INT NOT NULL,
    PRIMARY KEY (student_id, tutor_id),
    FOREIGN KEY (student_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (tutor_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════
--  INDEX
-- ══════════════════════════════

CREATE INDEX idx_booking_student ON brainbankdb.booking (student_id);
CREATE INDEX idx_booking_tutor ON brainbankdb.booking (tutor_id);
CREATE INDEX idx_timeslot_tutor_date ON brainbankdb.time_slot (tutor_id, date);
CREATE INDEX idx_user_email ON brainbankdb.user (email);

-- ══════════════════════════════
--  STORED PROCEDURE
-- ══════════════════════════════

DELIMITER $$

DROP PROCEDURE IF EXISTS brainbankdb.login$$
CREATE PROCEDURE brainbankdb.login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(100),
    OUT p_id INT,
    OUT p_name VARCHAR(100),
    OUT p_surname VARCHAR(100),
    OUT p_role VARCHAR(20)
)
BEGIN
    SELECT id, name, surname, role
    INTO p_id, p_name, p_surname, p_role
    FROM user
    WHERE email = p_email
      AND password = p_password;

    IF p_role IS NULL THEN
        SET p_role = 'NOT_FOUND';
    END IF;
END$$

DROP PROCEDURE IF EXISTS brainbankdb.reserve_slot$$
CREATE PROCEDURE brainbankdb.reserve_slot(
    IN  p_slot_id INT,
    IN  p_minutes INT,
    OUT p_success  BOOLEAN
)
BEGIN
    UPDATE time_slot
    SET reserved_until = DATE_ADD(NOW(), INTERVAL p_minutes MINUTE)
    WHERE id = p_slot_id
      AND available = TRUE
      AND (reserved_until IS NULL OR reserved_until < NOW());
    SET p_success = (ROW_COUNT() > 0);
END$$

DROP PROCEDURE IF EXISTS brainbankdb.release_slot$$
CREATE PROCEDURE brainbankdb.release_slot(IN p_slot_id INT)
BEGIN
    UPDATE time_slot SET reserved_until = NULL
    WHERE id = p_slot_id AND available = TRUE;
END$$

DELIMITER ;

-- ══════════════════════════════
--  USERS MYSQL
-- ══════════════════════════════

DROP USER IF EXISTS 'bb_login'@'localhost';
CREATE USER 'bb_login'@'localhost' IDENTIFIED BY 'bb_login';
GRANT EXECUTE ON PROCEDURE brainbankdb.login TO 'bb_login'@'localhost';
GRANT SELECT ON brainbankdb.user TO 'bb_login'@'localhost';
GRANT INSERT ON brainbankdb.user TO 'bb_login'@'localhost';
GRANT INSERT ON brainbankdb.tutor_detail TO 'bb_login'@'localhost';
GRANT INSERT ON brainbankdb.tutor_subject TO 'bb_login'@'localhost';
GRANT SELECT ON brainbankdb.tutor_subject TO 'bb_login'@'localhost';
GRANT SELECT ON brainbankdb.subject TO 'bb_login'@'localhost';

DROP USER IF EXISTS 'bb_student'@'localhost';
CREATE USER 'bb_student'@'localhost' IDENTIFIED BY 'bb_student';
GRANT EXECUTE ON PROCEDURE brainbankdb.login TO 'bb_student'@'localhost';
GRANT EXECUTE ON PROCEDURE brainbankdb.reserve_slot TO 'bb_student'@'localhost';
GRANT EXECUTE ON PROCEDURE brainbankdb.release_slot TO 'bb_student'@'localhost';
GRANT SELECT ON brainbankdb.subject TO 'bb_student'@'localhost';
GRANT SELECT ON brainbankdb.time_slot TO 'bb_student'@'localhost';
GRANT SELECT ON brainbankdb.tutor_detail TO 'bb_student'@'localhost';
GRANT SELECT ON brainbankdb.tutor_subject TO 'bb_student'@'localhost';
GRANT SELECT, INSERT ON brainbankdb.booking TO 'bb_student'@'localhost';
GRANT SELECT ON brainbankdb.user TO 'bb_student'@'localhost';
GRANT UPDATE ON brainbankdb.time_slot TO 'bb_student'@'localhost';
GRANT SELECT, UPDATE ON brainbankdb.activity TO 'bb_student'@'localhost';
GRANT UPDATE ON brainbankdb.booking TO 'bb_student'@'localhost';
GRANT SELECT, INSERT, DELETE ON brainbankdb.student_favourite_tutor TO 'bb_student'@'localhost';
GRANT UPDATE (email) ON brainbankdb.user TO 'bb_student'@'localhost';

DROP USER IF EXISTS 'bb_tutor'@'localhost';
CREATE USER 'bb_tutor'@'localhost' IDENTIFIED BY 'bb_tutor';
GRANT EXECUTE ON PROCEDURE brainbankdb.login TO 'bb_tutor'@'localhost';
GRANT SELECT, INSERT, UPDATE ON brainbankdb.time_slot TO 'bb_tutor'@'localhost';
GRANT SELECT ON brainbankdb.booking TO 'bb_tutor'@'localhost';
GRANT SELECT ON brainbankdb.user TO 'bb_tutor'@'localhost';
GRANT SELECT ON brainbankdb.subject TO 'bb_tutor'@'localhost';
GRANT SELECT ON brainbankdb.tutor_detail TO 'bb_tutor'@'localhost';
GRANT SELECT, INSERT, UPDATE ON brainbankdb.activity TO 'bb_tutor'@'localhost';
GRANT SELECT, INSERT, UPDATE ON brainbankdb.progress TO 'bb_tutor'@'localhost';
GRANT UPDATE (email) ON brainbankdb.user TO 'bb_tutor'@'localhost';
GRANT DELETE ON brainbankdb.activity TO 'bb_tutor'@'localhost';
GRANT DELETE ON brainbankdb.time_slot TO 'bb_tutor'@'localhost';

DROP USER IF EXISTS 'bb_admin'@'localhost';
CREATE USER 'bb_admin'@'localhost' IDENTIFIED BY 'bb_admin';
GRANT ALL PRIVILEGES ON brainbankdb.* TO 'bb_admin'@'localhost';

FLUSH PRIVILEGES;

-- ══════════════════════════════
--  BRAINBANK — TEST DATA
--  inserted to verify the correct functioning the application features.
-- ══════════════════════════════
USE brainbankdb;
-- ══════════════════════════════
--  USERS
--  password: 'password123'
-- ══════════════════════════════

INSERT INTO user (name, surname, email, password, role) VALUES
-- Students
('Emma',      'Rossi',     'emma@test.com',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT'),
('Marco',     'Verdi',     'marco@test.com',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT'),
('Giulia',    'Bianchi',   'giulia@test.com',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT'),
('Luca',      'Marino',    'luca@test.com',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT'),
-- Tutors
('Gabriele',  'Conti',     'gabriele@test.com',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
('Sofia',     'Ferrari',   'sofia@test.com',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
('Alessandro','Romano',    'ale@test.com',       'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
('Chiara',    'Esposito',  'chiara@test.com',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
('Matteo',    'Ricci',     'matteo@test.com',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
('Valentina', 'Lombardi',  'valentina@test.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TUTOR'),
-- Admin
('Admin',     'BrainBank', 'admin@test.com',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN');


-- ══════════════════════════════
--  TUTOR DETAILS
-- ══════════════════════════════

INSERT INTO tutor_detail (user_id, bio) VALUES
(5,  'Laurea magistrale in Matematica, 6 anni di esperienza in analisi e algebra'),
(6,  'Dottoranda in Fisica teorica, specializzata in meccanica e termodinamica'),
(7,  'Ingegnere informatico, esperto di programmazione e basi di dati'),
(8,  'Laurea in Ingegneria Gestionale, specializzata in statistica e ricerca operativa'),
(9,  'Dottore in Fisica, esperienza in fisica 1 e 2 e laboratori'),
(10, 'Laurea in Matematica applicata, specializzata in geometria e analisi');

-- ══════════════════════════════
--  SUBJECTS
-- ══════════════════════════════

INSERT INTO subject (name) VALUES
('Analisi 1'),           -- 1
('Analisi 2'),           -- 2
('Fisica 1'),            -- 3
('Fisica 2'),            -- 4
('Geometria'),           -- 5
('Statistica'),          -- 6
('Programmazione C'),    -- 7
('Algebra'),             -- 8
('Basi di Dati'),        -- 9
('Ricerca Operativa'),   -- 10
('Chimica'),             -- 11
('Elettrotecnica'),      -- 12
('Fondamenti di Informatica'), -- 13
('Controlli Automatici');      -- 14

-- ══════════════════════════════
--  SUBJECT FOR TUTOR
-- ══════════════════════════════

INSERT INTO tutor_subject (tutor_id, subject_id) VALUES
-- Gabriele: matematica
(5, 1), (5, 2), (5, 5), (5, 8), (5, 6),
-- Sofia: fisica
(6, 3), (6, 4), (6, 11), (6, 12),
-- Alessandro: informatica
(7, 7), (7, 9), (7, 13),
-- Chiara: gestionale
(8, 6), (8, 10), (8, 9),
-- Matteo: fisica e analisi
(9, 3), (9, 4), (9, 1), (9, 2),
-- Valentina: matematica applicata
(10, 1), (10, 2), (10, 5), (10, 8), (10, 14);

-- ══════════════════════════════
--  FUTURE SLOTS
-- ══════════════════════════════

INSERT INTO time_slot (tutor_id, date, start_time, end_time, available) VALUES
-- Gabriele
(5, '2026-06-12', '09:00:00', '11:00:00', TRUE),
(5, '2026-06-12', '14:00:00', '16:00:00', TRUE),
(5, '2026-06-13', '09:00:00', '11:00:00', TRUE),
(5, '2026-06-14', '10:00:00', '12:00:00', TRUE),
-- Sofia
(6, '2026-06-12', '10:00:00', '12:00:00', TRUE),
(6, '2026-06-13', '14:00:00', '16:00:00', TRUE),
(6, '2026-06-15', '09:00:00', '11:00:00', TRUE),
-- Alessandro
(7, '2026-06-12', '15:00:00', '17:00:00', TRUE),
(7, '2026-06-14', '09:00:00', '11:00:00', TRUE),
-- Chiara
(8, '2026-06-13', '10:00:00', '12:00:00', TRUE),
(8, '2026-06-15', '14:00:00', '16:00:00', TRUE),
-- Matteo
(9, '2026-06-12', '11:00:00', '13:00:00', TRUE),
(9, '2026-06-14', '14:00:00', '16:00:00', TRUE),
-- Valentina
(10, '2026-06-13', '09:00:00', '11:00:00', TRUE),
(10, '2026-06-15', '10:00:00', '12:00:00', TRUE);


-- ══════════════════════════════
--  CONFIRMED BOOKING
-- ══════════════════════════════

INSERT INTO booking (student_id, tutor_id, subject_id, slot_id, status, meet_link) VALUES
(1, 5, 1, 1, 'CONFIRMED', 'https://meet.jit.si/brainbank-emma-analisi1'),
(2, 5, 8, 2, 'CONFIRMED', 'https://meet.jit.si/brainbank-marco-algebra'),
(3, 6, 3, 5, 'CONFIRMED', 'https://meet.jit.si/brainbank-giulia-fisica1'),
(4, 7, 7, 8, 'CONFIRMED', 'https://meet.jit.si/brainbank-luca-progc');

-- update booked slot
UPDATE time_slot SET available = FALSE WHERE id IN (1, 2, 5, 8);

-- ══════════════════════════════
--  ACTIVITY
-- ══════════════════════════════

INSERT INTO activity (tutor_id, student_id, description, completed, created_at) VALUES
-- Gabriele → Emma
(5, 1, 'Esercizi pagine 45-50 sul libro di Analisi 1', TRUE,  NOW() - INTERVAL 5 DAY),
(5, 1, 'Ripasso teoremi di convergenza delle serie', FALSE, NOW() - INTERVAL 3 DAY),
(5, 1, 'Preparare domande per la prossima lezione', FALSE, NOW() - INTERVAL 1 DAY),
-- Gabriele → Marco
(5, 2, 'Svolgere esercizi di Algebra capitolo 3', FALSE, NOW() - INTERVAL 2 DAY),
(5, 2, 'Dimostrare il teorema di Cayley-Hamilton', FALSE, NOW() - INTERVAL 1 DAY),
-- Sofia → Giulia
(6, 3, 'Ripasso leggi di Newton', TRUE,  NOW() - INTERVAL 4 DAY),
(6, 3, 'Esercizi su moto uniformemente accelerato', FALSE, NOW() - INTERVAL 2 DAY),
-- Alessandro → Luca
(7, 4, 'Implementare lista concatenata in C', FALSE, NOW() - INTERVAL 3 DAY),
(7, 4, 'Studiare i puntatori capitolo 5', TRUE,  NOW() - INTERVAL 5 DAY);

-- ══════════════════════════════
--  PROGRESS
-- ══════════════════════════════

INSERT INTO progress (tutor_id, student_id, notes) VALUES
(5, 1, 'Emma mostra ottima comprensione degli argomenti di Analisi 1. Ha completato gli esercizi assegnati con pochi errori. Da approfondire: dimostrazioni dei teoremi di convergenza.'),
(5, 2, 'Marco è molto motivato e partecipa attivamente. Buona base teorica di algebra lineare, deve esercitarsi maggiormente sulle dimostrazioni.'),
(6, 3, 'Giulia ha una buona intuizione per la fisica. Deve consolidare le basi matematiche per affrontare i problemi più complessi.'),
(7, 4, 'Luca sta progredendo bene con la programmazione in C. Ha compreso i puntatori, deve fare più pratica con le strutture dati.');

-- ════════════════════════════════════
--  PAST SLOT
--  Inserted for display purposes only
-- ════════════════════════════════════

INSERT INTO time_slot (tutor_id, date, start_time, end_time, available) VALUES
-- Gabriele — past used slot
(5, '2026-05-20', '09:00:00', '11:00:00', FALSE),  -- 16 — Analisi 1 con Emma
(5, '2026-05-22', '14:00:00', '16:00:00', FALSE),  -- 17 — Algebra con Emma
(5, '2026-05-28', '10:00:00', '12:00:00', FALSE),  -- 18 — Geometria con Emma
-- Gabriele — past unused slot
(5, '2026-05-15', '09:00:00', '11:00:00', TRUE),   -- 19 — non utilizzato
(5, '2026-05-18', '14:00:00', '16:00:00', TRUE),   -- 20 — non utilizzato
-- Sofia — past used slot (then cancelled)
(6, '2026-05-21', '10:00:00', '12:00:00', FALSE),  -- 21 — Fisica 1 con Emma (cancellata)
-- Matteo — past used slot (then cancelled)
(9, '2026-05-25', '09:00:00', '11:00:00', FALSE);  -- 22 — Analisi 2 con Emma (cancellata)

-- ════════════════════════════════════
--  PAST BOOKING
--  Inserted for display purposes only
-- ════════════════════════════════════

-- PAST CONFIRMED BOOKING — Emma with Gabriele
INSERT INTO booking (student_id, tutor_id, subject_id, slot_id, status, meet_link, created_at) VALUES
(1, 5, 1, 16, 'CONFIRMED', 'https://meet.jit.si/brainbank-past001', '2026-05-19 10:00:00'),
(1, 5, 8, 17, 'CONFIRMED', 'https://meet.jit.si/brainbank-past002', '2026-05-21 10:00:00'),
(1, 5, 5, 18, 'CONFIRMED', 'https://meet.jit.si/brainbank-past003', '2026-05-27 10:00:00');

-- CANCELLED BOOKINGS — Emma
--  Inserted for display purposes only, to populate the cancelled bookings tab in the student booking history view.
INSERT INTO booking (student_id, tutor_id, subject_id, slot_id, status, meet_link, created_at) VALUES
(1, 6, 3, 21, 'CANCELLED', 'https://meet.jit.si/brainbank-past004', '2026-05-20 10:00:00'),
(1, 9, 2, 22, 'CANCELLED', 'https://meet.jit.si/brainbank-past005', '2026-05-24 10:00:00');