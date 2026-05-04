-- ============================================================
-- Nexus 더벤티 대규모 테스트 더미데이터 (1500개 매장)
-- 실행 순서: FK 의존성 기준 정렬
-- ============================================================

-- ============================================================
-- 0. 기존 데이터 삭제 (FK 역순) + 설정
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE pos_orders_item;
TRUNCATE TABLE pos_pay;
TRUNCATE TABLE pos_store_inventory;
TRUNCATE TABLE store_notification;
TRUNCATE TABLE head_notification;
TRUNCATE TABLE head_income;
TRUNCATE TABLE news_summary;
TRUNCATE TABLE waste_log;
TRUNCATE TABLE report;
TRUNCATE TABLE delivery;
TRUNCATE TABLE orders_item;
TRUNCATE TABLE orders;
TRUNCATE TABLE menu_item;
TRUNCATE TABLE menu;
TRUNCATE TABLE menu_category;
TRUNCATE TABLE store_inventory;
TRUNCATE TABLE head_inventory;
TRUNCATE TABLE inventory_movement;
TRUNCATE TABLE danger;
TRUNCATE TABLE product;
TRUNCATE TABLE category;
TRUNCATE TABLE store;
TRUNCATE TABLE user;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. User (본사 관리자 2명 + 가맹점주 1500명)
-- ============================================================
-- 비밀번호: 모두 'password123' BCrypt 인코딩
INSERT INTO user (email, password, user_name, tel, role, is_deleted) VALUES
('admin@theventi.co.kr', '{bcrypt}$2b$10$lvGJMgshX87086lRcSRGT.NXXzXyKXIXc2PgpAgAeK10LKeWfW2MS', '김본사', '010-1234-5678', 'ADMIN', false),
('manager01@theventi.co.kr', '{bcrypt}$2b$10$lvGJMgshX87086lRcSRGT.NXXzXyKXIXc2PgpAgAeK10LKeWfW2MS', '이정우', '010-2345-6789', 'MANAGER', false);

-- 가맹점주 1500명 (user_idx 3 ~ 1502)
INSERT INTO user (email, password, user_name, tel, role, is_deleted)
WITH RECURSIVE
  r1 AS (SELECT 1 AS a UNION ALL SELECT a+1 FROM r1 WHERE a < 100),
  r2 AS (SELECT 0 AS b UNION ALL SELECT b+1 FROM r2 WHERE b < 14),
  seq AS (SELECT a + b * 100 AS n FROM r1 CROSS JOIN r2)
SELECT
  CONCAT('store', LPAD(n, 4, '0'), '@theventi.co.kr'),
  '{bcrypt}$2b$10$lvGJMgshX87086lRcSRGT.NXXzXyKXIXc2PgpAgAeK10LKeWfW2MS',
  CONCAT(
    ELT(1 + (n % 10), '김','이','박','최','정','한','윤','장','임','오'),
    ELT(1 + ((n DIV 10) % 15), '민수','유진','수아','지민','서준','하은','도윤','예린','시우','채원','준혁','소영','태현','나은','현우')
  ),
  CONCAT('010-', LPAD(1000 + (n % 9000), 4, '0'), '-', LPAD(1000 + ((n * 7) % 9000), 4, '0')),
  'STORE',
  false
FROM seq
ORDER BY n;

-- ============================================================
-- 2. Store (가맹점 1500개)
-- 분포: 2023~2026-04 1450개 + 2026-05(이번 달) 45개 + 2026-05-04(오늘) 5개
-- 삭제(폐점): n%50=0 인 매장 약 29개
-- ============================================================
INSERT INTO store (user_idx, store_name, address, address_detail, file_path, business, postcode, created_at, closed_at, is_deleted)
WITH RECURSIVE
  r1 AS (SELECT 1 AS a UNION ALL SELECT a+1 FROM r1 WHERE a < 100),
  r2 AS (SELECT 0 AS b UNION ALL SELECT b+1 FROM r2 WHERE b < 14),
  seq AS (SELECT a + b * 100 AS n FROM r1 CROSS JOIN r2),
  store_base AS (
    SELECT
      n,
      n + 2 AS user_idx,
      CONCAT('더벤티 ',
        ELT(1 + (n % 30),
          '강남','서초','홍대','신촌','건대','잠실','서울역','종로','이태원','여의도',
          '목동','수원','분당','일산','용인','부천','안산','안양','남양주','화성',
          '판교','광교','동탄','김포','인천','부평','송도','해운대','서면','대구'),
        CAST(CEIL(n / 30) AS CHAR), '호점') AS store_name,
      CONCAT(
        ELT(1 + (n % 10), '서울특별시','서울특별시','서울특별시','서울특별시','서울특별시',
          '경기도','경기도','경기도','인천광역시','부산광역시'),
        ' ',
        ELT(1 + (n % 25),
          '강남구 강남대로','서초구 서초대로','마포구 양화로','서대문구 신촌로','광진구 능동로',
          '송파구 올림픽로','종로구 종로','중구 을지로','강서구 공항대로','영등포구 여의대로',
          '성동구 왕십리로','동대문구 천호대로','용산구 이태원로','강동구 천호대로','관악구 관악로',
          '동작구 동작대로','금천구 시흥대로','구로구 디지털로','노원구 동일로','도봉구 도봉로',
          '수원시 영통구 광교로','성남시 분당구 판교로','고양시 일산동구 중앙로','용인시 수지구 포은대로','부천시 부천로'),
        ' ', n) AS address,
      CONCAT(n % 5 + 1, '층 ', n % 10 + 1, '호') AS address_detail,
      CONCAT('/uploads/store/store', n, '.jpg') AS file_path,
      CONCAT(LPAD(100 + (n % 900), 3, '0'), '-', LPAD(10 + ((n * 3) % 90), 2, '0'), '-', LPAD(10000 + n, 5, '0')) AS business,
      10000 + (n % 90000) AS postcode,
      CASE
        WHEN n > 1495 THEN CAST('2026-05-04 09:00:00' AS DATETIME)
        WHEN n > 1450 THEN DATE_ADD('2026-05-01 09:00:00', INTERVAL ((n - 1451) % 3) DAY)
        ELSE DATE_ADD('2023-01-01 09:00:00', INTERVAL FLOOR((n - 1) * 1216 / 1450) DAY)
      END AS created_at
    FROM seq
  )
SELECT
  user_idx, store_name, address, address_detail, file_path, business, postcode,
  created_at,
  CASE WHEN n % 50 = 0 AND n <= 1450 THEN DATE_ADD(created_at, INTERVAL 90 DAY) ELSE NULL END,
  CASE WHEN n % 50 = 0 AND n <= 1450 THEN true ELSE false END
FROM store_base
ORDER BY n;

-- ============================================================
-- 3. Category (원자재 카테고리)
-- ============================================================
INSERT INTO category (category_name, is_deleted) VALUES
('원두/커피', false),
('유제품', false),
('시럽/소스', false),
('과일/퓨레', false),
('티/파우더', false),
('포장재', false),
('기타', false);

-- ============================================================
-- 4. Product (원자재 29개)
-- ============================================================
INSERT INTO product (category_idx, product_name, product_unit, max_stock, min_stock, unit_price, danger_days, is_deleted) VALUES
(1, '에스프레소 원두', 'kg', 50, 10, 15000, '30', false),
(1, '디카페인 원두', 'kg', 30, 5, 18000, '30', false),
(2, '우유', 'L', 100, 20, 2500, '7', false),
(2, '저지방우유', 'L', 50, 10, 2800, '7', false),
(2, '오트밀크', 'L', 40, 8, 4500, '14', false),
(2, '휘핑크림', 'L', 30, 5, 6000, '5', false),
(3, '바닐라시럽', '병(750ml)', 20, 4, 8500, '180', false),
(3, '카라멜시럽', '병(750ml)', 20, 4, 8500, '180', false),
(3, '헤이즐넛시럽', '병(750ml)', 15, 3, 8500, '180', false),
(3, '카라멜소스', '병(500ml)', 15, 3, 7000, '90', false),
(3, '초코소스', '병(500ml)', 15, 3, 6500, '90', false),
(4, '딸기퓨레', 'kg', 20, 4, 12000, '14', false),
(4, '망고퓨레', 'kg', 20, 4, 13000, '14', false),
(4, '복숭아퓨레', 'kg', 20, 4, 11000, '14', false),
(4, '레몬즙', 'L', 15, 3, 9000, '30', false),
(4, '자몽농축액', 'L', 15, 3, 10000, '30', false),
(5, '녹차파우더', 'kg', 10, 2, 25000, '180', false),
(5, '초코파우더', 'kg', 15, 3, 12000, '180', false),
(5, '얼그레이티백', '박스(100개입)', 10, 2, 15000, '365', false),
(5, '캐모마일티백', '박스(100개입)', 8, 2, 14000, '365', false),
(5, '히비스커스티백', '박스(100개입)', 8, 2, 14000, '365', false),
(6, '라지컵(24oz)', '박스(500개)', 10, 2, 35000, '999', false),
(6, '레귤러컵(16oz)', '박스(500개)', 10, 2, 28000, '999', false),
(6, '아이스컵 뚜껑', '박스(500개)', 10, 2, 18000, '999', false),
(6, '빨대', '박스(1000개)', 8, 2, 12000, '999', false),
(6, '캐리어(2구)', '박스(200개)', 8, 2, 15000, '999', false),
(7, '얼음', 'kg', 200, 50, 500, '1', false),
(7, '설탕', 'kg', 20, 5, 2000, '365', false),
(7, '연유', 'kg', 10, 2, 8000, '30', false);

-- ============================================================
-- 5. MenuCategory
-- ============================================================
INSERT INTO menu_category (menu_category_name, is_deleted) VALUES
('커피', false),
('베버리지', false),
('아이스블렌디드', false),
('티 & 에이드', false);

-- ============================================================
-- 6. Menu (더벤티 대표 메뉴 15개)
-- ============================================================
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

-- ============================================================
-- 7. MenuItem (메뉴별 원자재 레시피)
-- ============================================================
INSERT INTO menu_item (menu_idx, product_idx, quantity, menu_unit) VALUES
(1, 1, 20, 'g'), (1, 27, 200, 'g'), (1, 22, 1, 'ea'),
(2, 1, 20, 'g'), (2, 3, 300, 'ml'), (2, 27, 150, 'g'), (2, 22, 1, 'ea'),
(3, 1, 20, 'g'), (3, 3, 300, 'ml'), (3, 7, 20, 'ml'), (3, 27, 150, 'g'), (3, 22, 1, 'ea'),
(4, 1, 20, 'g'), (4, 3, 300, 'ml'), (4, 7, 15, 'ml'), (4, 10, 15, 'ml'), (4, 27, 150, 'g'), (4, 22, 1, 'ea'),
(5, 1, 20, 'g'), (5, 3, 250, 'ml'), (5, 22, 1, 'ea'),
(6, 12, 80, 'g'), (6, 3, 200, 'ml'), (6, 27, 200, 'g'), (6, 22, 1, 'ea'),
(7, 13, 80, 'g'), (7, 3, 200, 'ml'), (7, 27, 200, 'g'), (7, 22, 1, 'ea'),
(8, 14, 50, 'g'), (8, 19, 1, 'ea'), (8, 27, 200, 'g'), (8, 22, 1, 'ea'),
(9, 15, 40, 'ml'), (9, 28, 15, 'g'), (9, 27, 200, 'g'), (9, 22, 1, 'ea'),
(10, 16, 50, 'ml'), (10, 28, 15, 'g'), (10, 27, 200, 'g'), (10, 22, 1, 'ea'),
(11, 17, 15, 'g'), (11, 3, 350, 'ml'), (11, 27, 150, 'g'), (11, 22, 1, 'ea'),
(12, 18, 25, 'g'), (12, 3, 350, 'ml'), (12, 11, 10, 'ml'), (12, 27, 150, 'g'), (12, 22, 1, 'ea'),
(13, 19, 2, 'ea'), (13, 22, 1, 'ea'),
(14, 20, 2, 'ea'), (14, 22, 1, 'ea'),
(15, 21, 2, 'ea'), (15, 22, 1, 'ea');

-- ============================================================
-- 8. Danger (위험 발주 설정)
-- ============================================================
INSERT INTO danger (ratio, period) VALUES (30, 7);

-- ============================================================
-- 9. HeadInventory (본사 재고 - CRITICAL 7개, LOW 8개, NORMAL 14개)
-- 대시보드 위험 재고 목록 테스트: 15개 위험 항목 (첫 페이지 4 + 두번째 10 + 나머지 1)
-- ============================================================
INSERT INTO head_inventory (product_idx, count, status, manufactured_date) VALUES
(1,  500, 'NORMAL',   '2026-04-20 08:00:00'),
(2,  200, 'NORMAL',   '2026-04-20 08:00:00'),
(3,    5, 'CRITICAL', '2026-04-27 06:00:00'),
(4,    8, 'LOW',      '2026-04-27 06:00:00'),
(5,    5, 'LOW',      '2026-04-25 06:00:00'),
(6,    2, 'CRITICAL', '2026-04-26 06:00:00'),
(7,  100, 'NORMAL',   '2026-03-15 08:00:00'),
(8,  100, 'NORMAL',   '2026-03-15 08:00:00'),
(9,   80, 'NORMAL',   '2026-03-15 08:00:00'),
(10,  60, 'NORMAL',   '2026-03-20 08:00:00'),
(11,  60, 'NORMAL',   '2026-03-20 08:00:00'),
(12,   1, 'CRITICAL', '2026-04-25 08:00:00'),
(13, 100, 'NORMAL',   '2026-04-25 08:00:00'),
(14,   3, 'LOW',      '2026-04-25 08:00:00'),
(15,   2, 'LOW',      '2026-04-15 08:00:00'),
(16,   2, 'LOW',      '2026-04-15 08:00:00'),
(17,   1, 'LOW',      '2026-02-10 08:00:00'),
(18,  60, 'NORMAL',   '2026-02-10 08:00:00'),
(19,  50, 'NORMAL',   '2026-01-05 08:00:00'),
(20,   1, 'LOW',      '2026-01-05 08:00:00'),
(21,   1, 'LOW',      '2026-01-05 08:00:00'),
(22,   1, 'CRITICAL', '2026-04-01 08:00:00'),
(23,  50, 'NORMAL',   '2026-04-01 08:00:00'),
(24,   1, 'CRITICAL', '2026-04-01 08:00:00'),
(25,   1, 'CRITICAL', '2026-04-01 08:00:00'),
(26,   1, 'CRITICAL', '2026-04-01 08:00:00'),
(27, 1000,'NORMAL',   '2026-04-29 05:00:00'),
(28, 100, 'NORMAL',   '2026-01-10 08:00:00'),
(29,  50, 'NORMAL',   '2026-04-10 08:00:00');

-- ============================================================
-- 10. StoreInventory (대표 매장 10개, 매장당 10개 품목)
-- ============================================================
INSERT INTO store_inventory (store_idx, product_idx, count, status, manufactured_date, avg_stock)
WITH RECURSIVE
  stores AS (SELECT 1 AS s UNION ALL SELECT s+1 FROM stores WHERE s < 10),
  prods  AS (SELECT 1 AS p UNION ALL SELECT p+1 FROM prods WHERE p < 10)
SELECT
  s,
  LEAST(p * 3 - 2, 29),
  10 + ((s * p * 7) % 40),
  CASE
    WHEN s % 3 = 0 AND p % 4 = 0 THEN 'CRITICAL'
    WHEN s % 2 = 0 AND p % 3 = 0 THEN 'LOW'
    ELSE 'NORMAL'
  END,
  DATE_ADD('2026-04-01 08:00:00', INTERVAL (s + p) DAY),
  15 + ((s * p * 3) % 30)
FROM stores CROSS JOIN prods;

-- ============================================================
-- 11. PosStoreInventory (StoreInventory 미러링)
-- ============================================================
INSERT INTO pos_store_inventory (store_idx, product_idx, count, status, manufactured_date)
WITH RECURSIVE
  stores AS (SELECT 1 AS s UNION ALL SELECT s+1 FROM stores WHERE s < 10),
  prods  AS (SELECT 1 AS p UNION ALL SELECT p+1 FROM prods WHERE p < 10)
SELECT
  s,
  LEAST(p * 3 - 2, 29),
  10 + ((s * p * 7) % 40),
  CASE
    WHEN s % 3 = 0 AND p % 4 = 0 THEN 'CRITICAL'
    WHEN s % 2 = 0 AND p % 3 = 0 THEN 'LOW'
    ELSE 'NORMAL'
  END,
  DATE_ADD('2026-04-01 08:00:00', INTERVAL (s + p) DAY)
FROM stores CROSS JOIN prods;

-- ============================================================
-- 12. 발주 임시 테이블 (260건)
-- 분포:
--   n=1~30   : 오늘(05-04) 30건 — OrdersKPI 테스트
--   n=31~90  : 지난주(04-27~05-03) 60건 — WeeklyChart 테스트
--   n=91~140 : 2주전(04-20~04-26) 50건
--   n=141~160: 4월 초중순 20건
--   n=161~180: 3월 20건 — DangerStats 테스트
--   n=181~200: 2월 20건
--   n=201~220: 1월 20건
--   n=221~240: 12월 20건
--   n=241~260: 11월 20건
-- 이상발주(danger): n%12=0 → 약 21건 (CONFIRMED/APPROVE/REJECT 균등 분배)
-- ============================================================
CREATE TEMPORARY TABLE tmp_orders AS
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n+1 FROM seq WHERE n < 260)
SELECT
  n,
  1 + ((n * 137) % 1500) AS store_id,
  50000 + ((n * 7919) % 450001) AS price,
  CASE WHEN n % 3 < 2 THEN 'AUTO' ELSE 'MANUAL' END AS order_type,
  CASE
    -- 이상 발주 (n%12=0): 상태별 균등 분배
    WHEN n % 12 = 0 THEN
      CASE
        WHEN n % 36 = 0  THEN 'CONFIRMED'
        WHEN n % 36 = 12 THEN 'APPROVE'
        ELSE 'REJECT'
      END
    -- 오늘 발주 (n=1~30)
    WHEN n <= 22 THEN 'APPROVE'
    WHEN n <= 25 THEN 'CONFIRMED'
    WHEN n <= 27 THEN 'WAITING'
    WHEN n <= 29 THEN 'REJECT'
    WHEN n = 30 THEN 'CANCELLED'
    -- 나머지: 소수 모듈로로 상태 분배
    WHEN n % 13 = 0 THEN 'REJECT'
    WHEN n % 17 = 0 THEN 'CANCELLED'
    ELSE 'APPROVE'
  END AS order_status,
  IF(n % 12 = 0, true, false) AS is_danger,
  CASE
    WHEN n <= 30  THEN DATE_ADD('2026-05-04', INTERVAL (7 + n % 10) HOUR)
    WHEN n <= 38  THEN DATE_ADD('2026-05-03', INTERVAL (7 + (n - 30) % 12) HOUR)
    WHEN n <= 46  THEN DATE_ADD('2026-05-02', INTERVAL (7 + (n - 38) % 12) HOUR)
    WHEN n <= 54  THEN DATE_ADD('2026-05-01', INTERVAL (7 + (n - 46) % 12) HOUR)
    WHEN n <= 62  THEN DATE_ADD('2026-04-30', INTERVAL (7 + (n - 54) % 12) HOUR)
    WHEN n <= 70  THEN DATE_ADD('2026-04-29', INTERVAL (7 + (n - 62) % 12) HOUR)
    WHEN n <= 78  THEN DATE_ADD('2026-04-28', INTERVAL (7 + (n - 70) % 12) HOUR)
    WHEN n <= 90  THEN DATE_ADD('2026-04-27', INTERVAL (7 + (n - 78) % 12) HOUR)
    WHEN n <= 140 THEN DATE_ADD(DATE_ADD('2026-04-20', INTERVAL ((n - 91) % 7) DAY), INTERVAL (7 + n % 12) HOUR)
    WHEN n <= 160 THEN DATE_ADD(DATE_ADD('2026-04-01', INTERVAL ((n - 141) % 19) DAY), INTERVAL (7 + n % 12) HOUR)
    WHEN n <= 180 THEN DATE_ADD(DATE_ADD('2026-03-01', INTERVAL ((n - 161) % 28) DAY), INTERVAL (7 + n % 12) HOUR)
    WHEN n <= 200 THEN DATE_ADD(DATE_ADD('2026-02-01', INTERVAL ((n - 181) % 28) DAY), INTERVAL (7 + n % 12) HOUR)
    WHEN n <= 220 THEN DATE_ADD(DATE_ADD('2026-01-01', INTERVAL ((n - 201) % 28) DAY), INTERVAL (7 + n % 12) HOUR)
    WHEN n <= 240 THEN DATE_ADD(DATE_ADD('2025-12-01', INTERVAL ((n - 221) % 28) DAY), INTERVAL (7 + n % 12) HOUR)
    ELSE DATE_ADD(DATE_ADD('2025-11-01', INTERVAL ((n - 241) % 28) DAY), INTERVAL (7 + n % 12) HOUR)
  END AS created_at
FROM seq;

-- ============================================================
-- 13. Orders (발주서 260건)
-- ============================================================
INSERT INTO orders (store_idx, price, order_type, order_status, is_danger, created_at)
SELECT store_id, price, order_type, order_status, is_danger, created_at
FROM tmp_orders ORDER BY n;

-- ============================================================
-- 14. OrdersItem (발주 품목 - 발주당 2개씩 = 520건)
-- ============================================================
-- 첫 번째 품목
INSERT INTO orders_item (orders_idx, product_idx, count)
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n+1 FROM seq WHERE n < 260)
SELECT n, 1 + (n % 29), 3 + (n % 18)
FROM seq;

-- 두 번째 품목
INSERT INTO orders_item (orders_idx, product_idx, count)
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n+1 FROM seq WHERE n < 260)
SELECT n, 1 + ((n + 13) % 29), 2 + (n % 13)
FROM seq;

-- ============================================================
-- 15. Delivery (배송)
-- APPROVE 상태 발주에 대해서만 배송 생성
-- 상태 분포:
--   오늘 발주: READY(10) / START(7) / DELIVERYING(5)
--   지난주 발주: DELAY / DELIVERYING / START / DELIVERED 혼합
--   2주전: DELAY / DELIVERED
--   이전: DELIVERED (일부 DELAY)
-- DELAY 총 16건 (지연 배송 무한스크롤 테스트)
-- ============================================================
INSERT INTO delivery (orders_idx, delivery_status, delay_reason, dep_date, est_des_date, deliveryed_date)
SELECT
  n,
  CASE
    WHEN n <= 10 THEN 'READY'
    WHEN n <= 17 THEN 'START'
    WHEN n <= 22 THEN 'DELIVERYING'
    WHEN n <= 90 THEN
      CASE
        WHEN n % 7 = 0 THEN 'DELAY'
        WHEN n % 7 = 1 THEN 'DELIVERYING'
        WHEN n % 7 = 2 THEN 'START'
        ELSE 'DELIVERED'
      END
    WHEN n <= 140 THEN
      CASE WHEN n % 7 = 0 THEN 'DELAY' ELSE 'DELIVERED' END
    WHEN n IN (141, 142) THEN 'DELAY'
    ELSE 'DELIVERED'
  END,
  CASE
    WHEN (n > 22 AND n <= 90 AND n % 7 = 0)
      OR (n > 90 AND n <= 140 AND n % 7 = 0)
      OR n IN (141, 142)
    THEN ELT(1 + (n % 5),
      '물류센터 차량 고장으로 인한 배송 지연',
      '교통 정체로 인한 배송 지연',
      '물량 과다로 인한 배송 지연',
      '배송 기사 부족으로 인한 지연',
      '악천후로 인한 배송 지연')
    ELSE NULL
  END,
  DATE_ADD(created_at, INTERVAL 4 HOUR),
  DATE_ADD(created_at, INTERVAL 1 DAY),
  CASE
    WHEN n <= 22 THEN DATE_ADD(created_at, INTERVAL 1 DAY)
    WHEN (n <= 90 AND n % 7 = 0)
      OR (n > 90 AND n <= 140 AND n % 7 = 0)
      OR n IN (141, 142)
    THEN DATE_ADD(created_at, INTERVAL 3 DAY)
    ELSE DATE_ADD(created_at, INTERVAL 22 HOUR)
  END
FROM tmp_orders
WHERE order_status = 'APPROVE'
ORDER BY n;

-- ============================================================
-- 16. HeadIncome (본사 수익 - APPROVE 발주 기준)
-- ============================================================
INSERT INTO head_income (store_idx, orders_idx, price, status)
SELECT store_id, n, price, true
FROM tmp_orders
WHERE order_status = 'APPROVE'
ORDER BY n;

DROP TEMPORARY TABLE tmp_orders;

-- ============================================================
-- 17. PosPay (POS 결제 - 최근 1주 매출 100건)
-- ============================================================
INSERT INTO pos_pay (store_idx, method, paid_at, pay_amount)
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n+1 FROM seq WHERE n < 100)
SELECT
  1 + (n % 5),
  CASE WHEN n % 4 = 0 THEN 'CASH' ELSE 'CARD' END,
  DATE_ADD(DATE_ADD('2026-05-04', INTERVAL -(n % 7) DAY), INTERVAL (8 + n % 12) HOUR),
  1500 + ((n * 500) % 8000)
FROM seq
ORDER BY n;

-- ============================================================
-- 18. PosOrdersItem (POS 주문 항목)
-- ============================================================
INSERT INTO pos_orders_item (pos_pay_idx, menu_idx, quantity)
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n+1 FROM seq WHERE n < 100)
SELECT n, 1 + (n % 15), 1 + (n % 3)
FROM seq;

-- ============================================================
-- 19. WasteLog (폐기 기록)
-- ============================================================
INSERT INTO waste_log (store_idx, product_idx, quantity, amount_loss, waste_date, waste_reason) VALUES
(1,  3,  5, 12500, '2026-04-20 22:00:00', '유통기한 만료'),
(2,  6,  2, 12000, '2026-04-22 22:00:00', '유통기한 만료'),
(3, 12,  1, 12000, '2026-04-25 22:00:00', '변질 (냉장고 온도 이상)'),
(4,  3, 10, 25000, '2026-04-15 22:00:00', '냉장 보관 실패 (정전)'),
(5, 14,  2, 22000, '2026-04-21 22:00:00', '유통기한 만료'),
(1, 12,  3, 36000, '2026-05-01 22:00:00', '변질'),
(2,  3,  8, 20000, '2026-04-28 22:00:00', '유통기한 만료'),
(3,  6,  3, 18000, '2026-04-30 22:00:00', '유통기한 만료'),
(4,  5,  2,  9000, '2026-05-02 22:00:00', '유통기한 만료'),
(5,  3,  4, 10000, '2026-04-23 22:00:00', '유통기한 만료');

-- ============================================================
-- 20. NewsSummary (뉴스 요약)
-- ============================================================
INSERT INTO news_summary (store_idx, summary_date, target, summary_title, summary_contents, url, category) VALUES
(NULL, '2026-04-29 07:00:00', 'HQ', '커피 원두 국제 시세 상승 전망',
 '브라질 가뭄으로 아라비카 원두 선물 가격이 전주 대비 8% 상승했습니다. 향후 2-3개월간 원두 수급 불안정이 예상되며, 선제적 재고 확보를 권장합니다.',
 'https://example.com/news/coffee-price-2026', 'RISK'),
(NULL, '2026-04-29 07:00:00', 'HQ', '식품위생법 개정안 국회 통과',
 '1회용 컵 보증금제가 2026년 7월부터 전면 시행됩니다. 매장당 보증금 관리 시스템 도입이 필요하며, POS 시스템 업데이트가 요구됩니다.',
 'https://example.com/news/cup-deposit-law', 'RISK'),
(NULL, '2026-04-28 07:00:00', 'HQ', '프랜차이즈 가맹점 상생 지원 정책 발표',
 '공정거래위원회가 프랜차이즈 가맹본부의 가맹점 지원 의무를 강화하는 시행령을 발표했습니다.',
 'https://example.com/news/franchise-policy', 'RISK'),
(1, '2026-04-29 07:00:00', 'STORE', '강남역 일대 대규모 축제 개최 예정',
 '5월 1-5일 강남역 일대에서 "강남 페스티벌 2026"이 개최됩니다. 예상 방문객 50만명으로, 매출 증가가 예상됩니다.',
 'https://example.com/news/gangnam-festival', 'LOCAL_EVENT'),
(1, '2026-04-29 07:00:00', 'STORE', '서울 강남구 내일 기온 30도 돌파 예상',
 '4월 30일 서울 강남구 최고기온 31도로 예보되었습니다. 아이스 음료 및 스무디 수요 급증이 예상됩니다.',
 'https://example.com/weather/gangnam-0430', 'WEATHER'),
(2, '2026-04-29 07:00:00', 'STORE', '홍대 앞 도로 공사로 유동인구 감소 예상',
 '마포구 양화로 160 앞 도로 보수 공사가 5/1-5/15 진행됩니다. 유동인구 20-30% 감소가 예상됩니다.',
 'https://example.com/news/hongdae-construction', 'TRAFFIC'),
(2, '2026-04-28 07:00:00', 'STORE', '홍대 버스킹 축제 5월 개최',
 '5월 매주 토요일 홍대 걷고싶은거리에서 버스킹 축제가 열립니다. 주말 매출 증가가 예상됩니다.',
 'https://example.com/news/hongdae-busking', 'LOCAL_EVENT'),
(4, '2026-04-29 07:00:00', 'STORE', '건국대학교 축제 기간 매출 증가 예상',
 '건국대학교 대동제가 5/7-5/9 개최됩니다. 작년 동기간 매출이 평소 대비 180% 증가했습니다.',
 'https://example.com/news/konkuk-festival', 'LOCAL_EVENT'),
(5, '2026-04-29 07:00:00', 'STORE', '잠실 롯데월드 연휴 특별 이벤트',
 '어린이날 연휴(5/3-5/6) 롯데월드 방문객 대폭 증가 예상. 잠실역 일대 유동인구 증가에 따른 매출 상승이 기대됩니다.',
 'https://example.com/news/jamsil-lotteworld', 'LOCAL_EVENT');

-- ============================================================
-- 21. HeadNotification (본사 알림)
-- ============================================================
INSERT INTO head_notification (type, is_read, created_at) VALUES
('CRITICAL',  true,  '2026-04-22 07:05:00'),
('APPROVE',   true,  '2026-04-22 07:10:00'),
('DELAY',     true,  '2026-04-24 11:00:00'),
('DELIVERED', true,  '2026-04-25 15:05:00'),
('LOW',       false, '2026-04-28 07:00:00'),
('CRITICAL',  false, '2026-04-29 07:00:00'),
('ABNORMAL',  false, '2026-04-29 07:05:00'),
('DELAY',     false, '2026-05-01 08:00:00'),
('CRITICAL',  false, '2026-05-02 07:00:00'),
('APPROVE',   false, '2026-05-03 09:00:00'),
('LOW',       false, '2026-05-04 07:00:00');

-- ============================================================
-- 22. StoreNotification (가맹점 알림)
-- ============================================================
INSERT INTO store_notification (store_idx, type, is_read, created_at) VALUES
(1, 'DELIVERED', true,  '2026-04-25 09:50:00'),
(1, 'APPROVE',   true,  '2026-04-28 10:00:00'),
(1, 'DELIVERED', false, '2026-05-04 10:00:00'),
(2, 'CRITICAL',  true,  '2026-04-28 07:00:00'),
(2, 'LOW',       false, '2026-04-29 07:00:00'),
(2, 'CRITICAL',  false, '2026-04-29 07:00:00'),
(3, 'APPROVE',   true,  '2026-04-20 15:00:00'),
(3, 'APPROVE',   false, '2026-05-03 09:00:00'),
(4, 'DELAY',     true,  '2026-04-24 11:00:00'),
(4, 'CRITICAL',  false, '2026-04-29 07:00:00'),
(5, 'APPROVE',   true,  '2026-04-18 17:00:00'),
(5, 'REJECT',    true,  '2026-04-25 10:00:00');

-- ============================================================
-- 23. Report (리포트)
-- ============================================================
INSERT INTO report (report_title, report_file_path, created_at) VALUES
('2026년 3월 월간 매출 리포트', '/uploads/report/monthly_sales_202603.pdf', '2026-04-01 09:00:00'),
('2026년 3월 재고 현황 리포트', '/uploads/report/inventory_202603.pdf', '2026-04-01 09:30:00'),
('2026년 3월 폐기 분석 리포트', '/uploads/report/waste_analysis_202603.pdf', '2026-04-02 10:00:00'),
('2026년 4월 1주차 주간 리포트', '/uploads/report/weekly_202604_w1.pdf', '2026-04-07 09:00:00'),
('2026년 4월 2주차 주간 리포트', '/uploads/report/weekly_202604_w2.pdf', '2026-04-14 09:00:00'),
('2026년 4월 3주차 주간 리포트', '/uploads/report/weekly_202604_w3.pdf', '2026-04-21 09:00:00'),
('2026년 4월 4주차 주간 리포트', '/uploads/report/weekly_202604_w4.pdf', '2026-04-28 09:00:00');
