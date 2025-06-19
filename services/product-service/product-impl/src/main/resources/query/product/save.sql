INSERT INTO product (title, description, price, quantity, created_at, popularity, photo_url_id)
VALUES (?, ?, ?, ?, ?, ?, ?)
RETURNING *;