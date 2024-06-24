CREATE TYPE role_description AS ENUM ('owner', 'admin');

CREATE TABLE organization_share (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id uuid REFERENCES users(id),
    organization_id uuid REFERENCES organization(id),
    organization_role role_description NOT NULL,
    unique(user_id, organization_id)
);
