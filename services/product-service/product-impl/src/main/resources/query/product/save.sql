INSERT INTO product (title, description, photo_url, price, quantity, created_at, popularity)
VALUES (?, ?, ?, ?, ?, ?, ?)
RETURNING *;