UPDATE product
SET quantity = quantity + v.delta
    FROM (VALUES (?, ?)) AS v(product_id, delta)
WHERE product.id = v.product_id
  AND product.quantity + v.delta >= 0;
