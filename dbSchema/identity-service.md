1. Organizations Table

CREATE TABLE organizations (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
org_uuid VARCHAR(64) UNIQUE NOT NULL,
name VARCHAR(255) NOT NULL,
status VARCHAR(30) NOT NULL,        -- ACTIVE, SUSPENDED
plan_type VARCHAR(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_org_uuid ON organizations(org_uuid);


=====================================================================================================


2. Users Table

   CREATE TABLE users (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   user_uuid VARCHAR(64) UNIQUE NOT NULL,
   org_id BIGINT NOT NULL,
   email VARCHAR(255) NOT NULL,
   password_hash VARCHAR(255) NOT NULL,
   status VARCHAR(30) NOT NULL,        -- ACTIVE, LOCKED
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT fk_users_org
   FOREIGN KEY (org_id) REFERENCES organizations(id)
   );

CREATE UNIQUE INDEX uk_user_email_org ON users(email, org_id);
CREATE INDEX idx_users_org_id ON users(org_id);

=====================================================================================================

3. Roles Table

CREATE TABLE roles (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
role_name VARCHAR(100) UNIQUE NOT NULL,
description VARCHAR(255)
);

Examples:
----------
ROLE_ORG_ADMIN
ROLE_FRAUD_ANALYST
ROLE_AUDITOR
ROLE_OPS_USER

======================================================================================================

4. Permissions Table

CREATE TABLE permissions (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
permission_key VARCHAR(150) UNIQUE NOT NULL
);

Examples:
----------
RULE_READ
RULE_WRITE
ALERT_VIEW
USER_CREATE


======================================================================================================

5. Role_Permissions Table

CREATE TABLE role_permissions (
role_id BIGINT,
permission_id BIGINT,
PRIMARY KEY(role_id, permission_id),

    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

======================================================================================================

6. User_Roles Table

CREATE TABLE user_roles (
user_id BIGINT,
role_id BIGINT,
PRIMARY KEY(user_id, role_id),

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);

======================================================================================================

7. API Keys Table (Used by ingestion-engine and other services to authenticate and authorize requests)

CREATE TABLE api_keys (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
key_uuid VARCHAR(64) UNIQUE NOT NULL,
org_id BIGINT NOT NULL,
key_hash VARCHAR(255) NOT NULL,
status VARCHAR(30) NOT NULL,        -- ACTIVE, REVOKED
rate_limit_per_min INT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (org_id) REFERENCES organizations(id)
);

CREATE INDEX idx_api_keys_org ON api_keys(org_id);

=======================================================================================================

8. Audit Logs Table

CREATE TABLE audit_logs (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
org_id BIGINT,
user_id BIGINT,
action VARCHAR(150),
metadata JSON,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_org ON audit_logs(org_id);