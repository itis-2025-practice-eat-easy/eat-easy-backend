SELECT c.cart_id, c.user_id, c.is_blocked, c.created_at,
       cp.product_id, cp.quantity
FROM user_cart c
         LEFT JOIN cart_product cp ON c.cart_id = cp.cart_id
WHERE c.user_id = ?
ORDER BY c.created_at ASC