-- =========================
-- Admin 테이블
-- =========================
-- 초기 데이터는 주석 처리 (회원가입 사용)
-- 회원가입 후 아래 UPDATE 문으로 MASTER 권한 부여

-- MASTER 계정 생성 방법:
-- 1. POST /admin/auth/signup 으로 계정 생성 (userId: master, password: password123)
-- 2. 아래 UPDATE 문의 주석을 제거하고 서버 재시작
-- UPDATE admins SET role = 'MASTER' WHERE user_id = 'master';

INSERT INTO admins (user_id, password, name, email, role, created_at, updated_at)
VALUES
    ('admin01', '$2a$10$5Gm3EqSqXvELLPkjVDqYHOvT9Y8QqH7TjKfYqVxC7gqF2X9BqJLF.', '관리자1', 'admin01@sesac.com', 'ADMIN', '2025-11-30 09:00:00', '2025-11-30 09:00:00'),
    ('admin02', '$2a$10$5Gm3EqSqXvELLPkjVDqYHOvT9Y8QqH7TjKfYqVxC7gqF2X9BqJLF.', '관리자2', 'admin02@sesac.com', 'ADMIN', '2025-11-30 09:05:00', '2025-11-30 09:05:00');

-- =========================
-- Room 테이블
-- =========================
INSERT INTO rooms (room_name, location, capacity, description, created_at, updated_at)
VALUES
    ('1층 회의실', 1, 8, '8인용 대형 테이블, 대형 모니터, 대형 화이트 보드, 자리마다 멀티탭 구비', '2025-11-30 09:10:00', '2025-11-30 09:10:00'),
    ('2층 회의실', 2, 6, '개방된 공간, 스탱바이미 2대 구비, 화이트 보드, 자리마다 멀티탭 구비', '2025-11-30 09:15:00', '2025-11-30 09:15:00');

-- =========================
-- Classes 테이블
-- =========================
INSERT INTO classes (class_name, start_date, end_date, created_at, updated_at)
VALUES
    ('자바 기초', '2025-12-01', '2025-12-31', '2025-11-30 09:25:00', '2025-11-30 09:25:00'),
    ('스프링 부트', '2025-12-05', '2025-12-25', '2025-11-30 09:30:00', '2025-11-30 09:30:00'),
    ('데이터베이스', '2025-12-10', '2025-12-30', '2025-11-30 09:35:00', '2025-11-30 09:35:00');

-- =========================
-- Reservation 테이블
-- =========================
INSERT INTO reservations (classes_id, room_id, user_name, phone_last_number, start_time, end_time,
                          reservation_date, created_at, updated_at, status)
VALUES
    (1, 1, '홍길동', '1234', '09:00:00', '11:00:00', '2025-12-01', '2025-11-30 10:00:00', '2025-11-30 10:00:00', 'RESERVED'),
    (2, 2, '김철수', '5678', '13:00:00', '15:00:00', '2025-12-05', '2025-11-30 10:05:00', '2025-11-30 10:05:00', 'CANCELED');

-- =========================
-- Notices 테이블
-- =========================
INSERT INTO notices (title, content, status, admins_id, created_at, updated_at)
VALUES
    ('시스템 점검 안내', '2025년 12월 31일 오전 2시부터 4시까지 시스템 점검이 예정되어 있습니다.', true, 1, '2025-11-30 11:00:00', '2025-11-30 11:00:00'),
    ('서비스 런칭 이벤트', '새로운 서비스 런칭을 기념하여 이벤트를 진행합니다. 많은 참여 부탁드립니다.', true, 1, '2025-11-30 11:05:00', '2025-11-30 11:05:00'),
    ('이용 약관 개정 안내', '개정된 이용 약관에 대한 안내입니다. 상세 내용은 공지사항을 확인해주세요.', false, 2, '2025-11-30 11:10:00', '2025-11-30 11:10:00');