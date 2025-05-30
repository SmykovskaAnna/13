INSERT INTO users (username, email, password) VALUES
                                                  ('user', 'user@example.com', 'hashed_password_1'),
                                                  ('user2', 'user2@example.com', 'hashed_password_2');

INSERT INTO products (name, description, price, stock) VALUES
                                                           ('Smartphone', 'Smartphone with a good camera', 2500.90, 10),
                                                           ('Laptop', 'Powerful gaming laptop', 6000.50, 5),
                                                           ('Wireless Headphones', 'Noise-cancelling over-ear headphones', 1200.00, 15),
                                                           ('Smartwatch', 'Fitness smartwatch with heart rate monitor', 900.00, 20),
                                                           ('Tablet', '10-inch tablet with high resolution display', 3200.75, 8);

INSERT INTO orders (user_id, order_date, total) VALUES
                                                    (1, NOW(), 2500.90),
                                                    (2, NOW(), 6000.50);