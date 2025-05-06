INSERT INTO product (title, description, photo_url, price, category, quantity)
VALUES (?, ?, ?, ?, ?, ?)
RETURNING *;
