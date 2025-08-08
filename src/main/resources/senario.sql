-- 지역 추가하기
INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), 'abcdef01-2345-6789-abcd-ef0123456789', 'R8F8B4021', 'atque', FALSE, 'velit', 'eligendi', 'aut',
        'ducimus');

-- 카테 고리 추가
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '6530a750-89b7-44af-aebc-0e008fbeccd7', '치킨');

-- 유저 추가
INSERT INTO p_user (created_at, created_by, updated_at, updated_by,
                    phone_number, user_role, nickname, real_name, username, email, password)
VALUES (now(), NULL, now(), NULL,
        '010-1111-2222', 'OWNER', 'OwnerKim', '김철수', 'owner_kim', 'owner.kim@example.com', 'hashed_pasㄴ');

INSERT INTO p_user (created_at, created_by, updated_at, updated_by,
                    phone_number, user_role, nickname, real_name, username, email, password)
VALUES (now(), NULL, now(), NULL,
        '010-3333-4444', 'CUSTOMER', 'CustomerLee', '이영희', 'customer_lee', 'customer.lee@example.com', 'hashed_pass');

-- 승인된 스토어 추가
INSERT INTO p_store (created_at, created_by, deleted_at, deleted_by, min_order_amount,
                     updated_at, updated_by, user_id, category_id, region_id,
                     store_id, phone_number, store_name, address, description, store_accept_status)
VALUES (now(), NULL, NULL, NULL, 5000,
        now(), NULL, 1, '6530a750-89b7-44af-aebc-0e008fbeccd7',
        'abcdef01-2345-6789-abcd-ef0123456789',
        '87654321-fedc-ba98-7654-3210fedcba98', '010-1234-5678', '교촌치킨', '서울특별시 강남구 테헤란로 123',
        '매일 신선한 재료로 만드는 치킨 전문점입니다.', 'APPROVE');

INSERT INTO p_store (created_at, created_by, deleted_at, deleted_by, min_order_amount,
                     updated_at, updated_by, user_id, category_id, region_id,
                     store_id, phone_number, store_name, address, description, store_accept_status)
VALUES (now(), NULL, NULL, NULL, 5000,
        now(), NULL, 1, '6530a750-89b7-44af-aebc-0e008fbeccd7',
        'abcdef01-2345-6789-abcd-ef0123456789',
        '87654321-fedc-ba98-7654-3210fedcba08', '010-1234-5978', 'BHC 치킨', '서울특별시 강남구 테헤란로 125',
        '매일 신선한 재료로 만드는 치킨 전문점입니다.', 'APPROVE');
-- 메뉴
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd56e1bb1-a709-4772-b7da-a1b2b88af9c1', '87654321-fedc-ba98-7654-3210fedcba98', '허니콤보',
        '꿀맛 허니 콤보',
        23000, FALSE);

-- 오더 만들기
INSERT INTO p_orders ("is_refundable", "created_at","total_price","updated_at","user_id", "orders_id", "store_id", "order_channel",
                      "order_status", "receipt_method", "payment_method", "delivery_address", "order_history", "request_message")
VALUES ('false', now(), 30000,now(), 1,'4b3b190c-6a1b-4f7f-8c3e-9c4c7b8d2a1f',
        '87654321-fedc-ba98-7654-3210fedcba98','ONLINE','COMPLETED',
        'DELIVERY', 'CREDIT_CARD', '우리집', '완료', '빨리와' );


-- 리뷰 넣기
INSERT INTO p_review ("created_at", "updated_at", "rating", "user_id", "b_order_id", "review_id", "store_id", "content")
VALUES (now(), now(), 5, 1, '4b3b190c-6a1b-4f7f-8c3e-9c4c7b8d2a1f', '7e2c9f5a-3d8e-4b6a-9a5c-1f0e2d3b4a5c',
        '87654321-fedc-ba98-7654-3210fedcba98', '맛있게 잘 먹었습니다.')


