CREATE TABLE candidates
(
    id            SERIAL PRIMARY KEY,
    name         VARCHAR NOT NULL,
    description   VARCHAR NOT NULL,
    creation_date TIMESTAMP,
    file_id       INT REFERENCES files(id)
);