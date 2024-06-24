CREATE TABLE account (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    activated BOOLEAN NOT NULL,
    organization_id uuid REFERENCES organization(id)
);
