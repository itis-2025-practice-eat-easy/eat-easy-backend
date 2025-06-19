SELECT EXISTS (
    SELECT 1
    FROM user_cart
    WHERE user_id = ? AND is_blocked = false
)
