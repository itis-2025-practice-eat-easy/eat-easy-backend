SELECT p.* FROM product p
JOIN product_category pc ON p.id = pc.product_id
WHERE pc.category_id = ?