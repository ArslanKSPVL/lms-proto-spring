INSERT INTO users (id, username, password, full_name, email, enabled)
VALUES (1, 'superadmin', '{bcrypt}$2a$10$Q9ra1d0fI6ZqL7OLGeqP2uQqeQxPqPzJ28I/2XvCkNzKYYj4b16kW', 'Super Admin', 'superadmin@example.com', true)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_SUPER_ADMIN')
ON DUPLICATE KEY UPDATE role = role;

