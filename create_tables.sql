CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 INCREMENT BY 1 CACHE 10;
CREATE SEQUENCE IF NOT EXISTS doc_id_seq START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL DEFAULT nextval('user_id_seq'),
    email  VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name  VARCHAR(30) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT NOT NULL DEFAULT nextval('doc_id_seq'),
    owner_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    name VARCHAR(30) NOT NULL,
    UNIQUE(owner_id, name),
    PRIMARY KEY(id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ca_lists (
    user_id BIGINT NOT NULL,
    doc_id BIGINT NOT NULL,
    doc_name VARCHAR(30) NOT NULL,
    permissions VARCHAR(2) NOT NULL,
    PRIMARY KEY(user_id, doc_id),
    CONSTRAINT check_permissions CHECK (role='R' OR role='RW'),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_doc FOREIGN KEY (doc_id) REFERENCES documents(id)
);

CREATE TABLE IF NOT EXISTS roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(5) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT user_role FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT role_value CHECK (role='USER' OR role='ADMIN')
)
