-- 기존 데이터 정리 (FK 역순)
DELETE FROM pos_orders_item;
DELETE FROM pos_pay;
DELETE FROM menu;
DELETE FROM menu_category;
DELETE FROM store;

ALTER TABLE pos_orders_item AUTO_INCREMENT = 1;
ALTER TABLE pos_pay AUTO_INCREMENT = 1;
ALTER TABLE menu AUTO_INCREMENT = 1;
ALTER TABLE menu_category AUTO_INCREMENT = 1;
ALTER TABLE store AUTO_INCREMENT = 1;

-- menu_category (4)
INSERT INTO menu_category (menu_category_name, is_deleted) VALUES
                                                               ('커피', false),
                                                               ('베버리지', false),
                                                               ('아이스블렌디드', false),
                                                               ('티 & 에이드', false);

-- menu (15)
INSERT INTO menu (menu_name, price, img_path, is_deleted, menu_category_idx) VALUES
                                                                                 ('아메리카노', 1500, '/uploads/menu/americano.jpg', false, 1),
                                                                                 ('카페라떼', 2500, '/uploads/menu/cafelatte.jpg', false, 1),
                                                                                 ('바닐라라떼', 3000, '/uploads/menu/vanillalatte.jpg', false, 1),
                                                                                 ('카라멜마끼아또', 3500, '/uploads/menu/caramelmacchiato.jpg', false, 1),
                                                                                 ('카푸치노', 2500, '/uploads/menu/cappuccino.jpg', false, 1),
                                                                                 ('녹차라떼', 3000, '/uploads/menu/greentealatte.jpg', false, 2),
                                                                                 ('초코라떼', 3000, '/uploads/menu/chocolatte.jpg', false, 2),
                                                                                 ('딸기스무디', 3500, '/uploads/menu/strawberrysmoothie.jpg', false, 3),
                                                                                 ('망고스무디', 3500, '/uploads/menu/mangosmoothie.jpg', false, 3),
                                                                                 ('복숭아아이스티', 2500, '/uploads/menu/peachicedtea.jpg', false, 4),
                                                                                 ('레몬에이드', 2500, '/uploads/menu/lemonade.jpg', false, 4),
                                                                                 ('자몽에이드', 3000, '/uploads/menu/grapefruitade.jpg', false, 4),
                                                                                 ('얼그레이티', 2000, '/uploads/menu/earlgrey.jpg', false, 4),
                                                                                 ('캐모마일티', 2000, '/uploads/menu/chamomile.jpg', false, 4),
                                                                                 ('히비스커스티', 2000, '/uploads/menu/hibiscus.jpg', false, 4);

-- store (5)
INSERT INTO store (store_name, address, address_detail, file_path, business, postcode, created_at, is_deleted) VALUES
                                                                                                                   ('강남점', '서울 강남구 테헤란로 123', '1층', '/uploads/store/gangnam.jpg', '123-45-67890', '06234', NOW(), false),
                                                                                                                   ('홍대점', '서울 마포구 와우산로 45', '2층', '/uploads/store/hongdae.jpg', '123-45-67891', '04057', NOW(), false),
                                                                                                                   ('잠실점', '서울 송파구 올림픽로 240', '1층', '/uploads/store/jamsil.jpg', '123-45-67892', '05551', NOW(), false),
                                                                                                                   ('신촌점', '서울 서대문구 연세로 14', '1층', '/uploads/store/sinchon.jpg', '123-45-67893', '03729', NOW(), false),
                                                                                                                   ('광화문점', '서울 종로구 새문안로 1', '1층', '/uploads/store/gwanghwamun.jpg', '123-45-67894', '03174', NOW(), false);

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