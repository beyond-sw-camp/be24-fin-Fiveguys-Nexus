-- 기존 데이터 정리 (FK 역순)
DELETE FROM pos_orders_item;
DELETE FROM pos_pay;
DELETE FROM menu;
DELETE FROM menu_category;
DELETE FROM store;

-- pos_pay, pos_orders_item만 AUTO_INCREMENT 사용 (store/menu/menu_category는 idx 직접 지정)
ALTER TABLE pos_orders_item AUTO_INCREMENT = 1;
ALTER TABLE pos_pay AUTO_INCREMENT = 1;

-- menu_category (4) — idx 직접 지정
INSERT INTO menu_category (menu_category_idx, menu_category_name, is_deleted) VALUES
(1, '커피', false),
(2, '베버리지', false),
(3, '아이스블렌디드', false),
(4, '티 & 에이드', false);

-- menu (15) — idx 직접, imgPath 제거
INSERT INTO menu (menu_idx, menu_name, price, is_deleted, menu_category_idx) VALUES
(1, '아메리카노', 1500, false, 1),
(2, '카페라떼', 2500, false, 1),
(3, '바닐라라떼', 3000, false, 1),
(4, '카라멜마끼아또', 3500, false, 1),
(5, '카푸치노', 2500, false, 1),
(6, '녹차라떼', 3000, false, 2),
(7, '초코라떼', 3000, false, 2),
(8, '딸기스무디', 3500, false, 3),
(9, '망고스무디', 3500, false, 3),
(10, '복숭아아이스티', 2500, false, 4),
(11, '레몬에이드', 2500, false, 4),
(12, '자몽에이드', 3000, false, 4),
(13, '얼그레이티', 2000, false, 4),
(14, '캐모마일티', 2000, false, 4),
(15, '히비스커스티', 2000, false, 4);

-- store (5) — idx 직접, 부수 컬럼 제거
INSERT INTO store (store_idx, store_name, is_deleted) VALUES
(1, '강남점', false),
(2, '홍대점', false),
(3, '잠실점', false),
(4, '신촌점', false),
(5, '광화문점', false);

-- pos_pay (30건, 오늘 CURDATE, 시간/매장/method 분산)
INSERT INTO pos_pay (store_idx, method, paid_at, pay_amount) VALUES
-- 강남점 (10건)
(1, 'CARD', CONCAT(CURDATE(), ' 08:15:00'), 1500),
(1, 'CARD', CONCAT(CURDATE(), ' 09:20:00'), 5500),
(1, 'CASH', CONCAT(CURDATE(), ' 10:45:00'), 2500),
(1, 'CARD', CONCAT(CURDATE(), ' 11:30:00'), 7000),
(1, 'CARD', CONCAT(CURDATE(), ' 12:05:00'), 4500),
(1, 'CARD', CONCAT(CURDATE(), ' 12:40:00'), 6000),
(1, 'CARD', CONCAT(CURDATE(), ' 13:15:00'), 3000),
(1, 'CASH', CONCAT(CURDATE(), ' 15:30:00'), 5000),
(1, 'CARD', CONCAT(CURDATE(), ' 17:00:00'), 4000),
(1, 'CARD', CONCAT(CURDATE(), ' 19:20:00'), 3500),
-- 홍대점 (8건)
(2, 'CARD', CONCAT(CURDATE(), ' 09:00:00'), 2500),
(2, 'CARD', CONCAT(CURDATE(), ' 11:15:00'), 4000),
(2, 'CASH', CONCAT(CURDATE(), ' 12:30:00'), 5500),
(2, 'CARD', CONCAT(CURDATE(), ' 13:00:00'), 3500),
(2, 'CARD', CONCAT(CURDATE(), ' 14:45:00'), 7000),
(2, 'CARD', CONCAT(CURDATE(), ' 16:20:00'), 4500),
(2, 'CASH', CONCAT(CURDATE(), ' 18:00:00'), 3000),
(2, 'CARD', CONCAT(CURDATE(), ' 20:00:00'), 5000),
-- 잠실점 (6건)
(3, 'CARD', CONCAT(CURDATE(), ' 10:00:00'), 3000),
(3, 'CARD', CONCAT(CURDATE(), ' 11:45:00'), 4500),
(3, 'CASH', CONCAT(CURDATE(), ' 12:55:00'), 6000),
(3, 'CARD', CONCAT(CURDATE(), ' 14:20:00'), 2500),
(3, 'CARD', CONCAT(CURDATE(), ' 16:00:00'), 3500),
(3, 'CARD', CONCAT(CURDATE(), ' 18:30:00'), 5000),
-- 신촌점 (4건)
(4, 'CARD', CONCAT(CURDATE(), ' 11:00:00'), 2000),
(4, 'CASH', CONCAT(CURDATE(), ' 12:20:00'), 4000),
(4, 'CARD', CONCAT(CURDATE(), ' 14:00:00'), 3000),
(4, 'CARD', CONCAT(CURDATE(), ' 17:30:00'), 5500),
-- 광화문점 (2건)
(5, 'CARD', CONCAT(CURDATE(), ' 12:15:00'), 3500),
(5, 'CASH', CONCAT(CURDATE(), ' 15:00:00'), 4500);

-- pos_orders_item (~45건, 결제당 1~3개)
INSERT INTO pos_orders_item (pos_pay_idx, menu_idx, quantity) VALUES
-- 강남점 결제 1~10
(1, 1, 1),
(2, 1, 2), (2, 2, 1),
(3, 2, 1),
(4, 1, 1), (4, 4, 1), (4, 6, 1),
(5, 1, 1), (5, 3, 1),
(6, 2, 2), (6, 6, 1),
(7, 3, 1),
(8, 8, 1), (8, 1, 1),
(9, 7, 1), (9, 13, 1),
(10, 9, 1),
-- 홍대점 결제 11~18
(11, 2, 1),
(12, 11, 1), (12, 13, 1),
(13, 9, 1), (13, 1, 1),
(14, 4, 1),
(15, 1, 2), (15, 9, 1),
(16, 5, 1), (16, 11, 1),
(17, 7, 1),
(18, 4, 1), (18, 12, 1),
-- 잠실점 결제 19~24
(19, 6, 1),
(20, 2, 1), (20, 14, 1),
(21, 4, 1), (21, 12, 1),
(22, 1, 1), (22, 14, 1),
(23, 7, 1),
(24, 2, 1), (24, 11, 1),
-- 신촌점 결제 25~28
(25, 14, 1),
(26, 1, 1), (26, 12, 1),
(27, 6, 1),
(28, 8, 1), (28, 3, 1),
-- 광화문점 결제 29~30
(29, 3, 1),
(30, 9, 1);
