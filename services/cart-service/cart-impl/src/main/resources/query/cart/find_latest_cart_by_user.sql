SELECT cart_id
FROM user_cart
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 1