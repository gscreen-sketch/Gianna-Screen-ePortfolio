-- CS499 Capstone enhancement: Centralized MySQL database

CREATE DATABASE IF NOT EXISTS project1_inventory;
USE project1_inventory;

-- Stores PBKDF2 derived key and its salt
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(64) NOT NULL PRIMARY KEY,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL
);

-- Blocks Duplicate Names
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(128) NOT NULL,
    quantity INT NOT NULL,
    UNIQUE INDEX idx_item_name (item_name)
);
