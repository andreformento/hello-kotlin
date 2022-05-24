CREATE TABLE users (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    unique(email)
);

INSERT INTO users (id, name, email) VALUES
('6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07', 'Eddie', 'iron-maiden@evil.hell'),
('f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9', 'share', 'share@blah.io'),
('9f12c2b8-da0f-11ec-9d64-0242ac120002', 'User Without Org', 'user@without.org'),
('d54e6318-e288-4f83-aa8a-96a0eb4c7a62', 'Manage organizations', 'manage@org');

CREATE TABLE user_auth_password (
    user_id uuid PRIMARY KEY REFERENCES users(id),
    user_password VARCHAR(255) NOT NULL
);

INSERT INTO user_auth_password (user_id, user_password) VALUES
('6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07', 'pass'),
('f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9', 'pass'),
('9f12c2b8-da0f-11ec-9d64-0242ac120002', 'pass'),
('d54e6318-e288-4f83-aa8a-96a0eb4c7a62', 'pass');
