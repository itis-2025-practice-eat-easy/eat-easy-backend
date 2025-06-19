SELECT c.id, c.title
FROM category c
         JOIN product_category pc ON c.id = pc.category_id
WHERE pc.product_id = ?;
