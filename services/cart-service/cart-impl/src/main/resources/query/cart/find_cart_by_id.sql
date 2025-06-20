SELECT cart_id, user_id, is_blocked, created_at
FROM user_cart
WHERE cart_id = ?