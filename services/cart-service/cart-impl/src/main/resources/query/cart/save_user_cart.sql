INSERT INTO user_cart (user_id)
VALUES (?)
RETURNING cart_id;