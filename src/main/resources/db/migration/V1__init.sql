-- V1__init.sql
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS wallets (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    wallet_id VARCHAR(255) NOT NULL UNIQUE,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    wallet_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    type VARCHAR(10) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    reference VARCHAR(255) NOT NULL UNIQUE,
    external_reference VARCHAR(255),
    gateway VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    description TEXT,
    failure_reason TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);