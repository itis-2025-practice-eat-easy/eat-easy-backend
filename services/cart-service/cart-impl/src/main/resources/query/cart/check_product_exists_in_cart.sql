SELECT EXISTS (
    SELECT 1
    FROM cart_product
    WHERE cart_id = ? AND product_id = ?
);