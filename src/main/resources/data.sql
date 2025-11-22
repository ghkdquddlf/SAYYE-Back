-- =========================
-- Admin 테이블
-- =========================
INSERT INTO admins (admin_id, password)
VALUES ('admin01', 'password123'),
       ('admin02', 'password456');

-- =========================
-- Room 테이블
-- =========================
INSERT INTO rooms (room_name, location, capacity, description)
VALUES ('회의실 A', 1, 4, '작은 회의용'),
       ('회의실 B', 2, 8, '중간 규모 회의용'),
       ('회의실 C', 3, 12, '대규모 회의용');

-- =========================
-- Classes 테이블
-- =========================
INSERT INTO classes (className, start_date, end_date)
VALUES ('자바 기초', '2025-12-01', '2025-12-31'),
       ('스프링 부트', '2025-12-05', '2025-12-25'),
       ('데이터베이스', '2025-12-10', '2025-12-30');

-- =========================
-- Reservation 테이블
-- =========================
INSERT INTO reservations (classes_id, room_id, user_name, phone_last_number, start_time, end_time,
                          reservation_date)
VALUES (1, 1, '홍길동', '1234', '10:00:00', '12:00:00', '2025-12-01'),
       (2, 2, '김철수', '5678', '14:00:00', '16:00:00', '2025-12-05'),
       (3, 3, '이영희', '9012', '09:00:00', '11:00:00', '2025-12-10');
