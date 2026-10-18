// CS499 Capstone enhancement: REST API  sits between the Android app and MySQL


const express = require('express');
const crypto = require('crypto');
const mysql = require('mysql2/promise');

const app = express();
app.use(express.json());

// Database connection pool
const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'project1_inventory',
    connectionLimit: 5
});

// Wraps async routes so a database error returns 500 instead of crashing the server
function route(handler) {
    return async (req, res) => {
        try {
            await handler(req, res);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    };
}


// Returns the salt for a username (the app needs it to derive the same key)
app.get('/users/:username', route(async (req, res) => {
    const [rows] = await pool.query('SELECT salt FROM users WHERE username = ?', [req.params.username]);
    if (rows.length === 0) {
        return res.status(404).json({ error: 'User not found' });
    }
    res.json({ salt: rows[0].salt });
}));

// Creates a user from the derived key and salt sent by the app
app.post('/users', route(async (req, res) => {
    const { username, hash, salt } = req.body;
    if (typeof username !== 'string' || typeof hash !== 'string' || typeof salt !== 'string'
            || username.length === 0 || username.length > 64) {
        return res.status(400).json({ error: 'Invalid user data' });
    }
    try {
        await pool.query('INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)',
            [username, hash, salt]);
        res.status(201).json({ created: true });
    } catch (err) {
        if (err.code === 'ER_DUP_ENTRY') {
            return res.status(409).json({ error: 'Username already taken' });
        }
        throw err;
    }
}));

// Compares the derived key from the app against the stored one
app.post('/login', route(async (req, res) => {
    const { username, hash } = req.body;
    if (typeof username !== 'string' || typeof hash !== 'string') {
        return res.status(400).json({ error: 'Invalid login data' });
    }
    const [rows] = await pool.query('SELECT password_hash FROM users WHERE username = ?', [username]);
    if (rows.length === 0) {
        return res.status(401).json({ valid: false });
    }
    const stored = Buffer.from(rows[0].password_hash);
    const given = Buffer.from(hash);
    const valid = stored.length === given.length && crypto.timingSafeEqual(stored, given);
    res.status(valid ? 200 : 401).json({ valid: valid });
}));


// Returns every item
app.get('/items', route(async (req, res) => {
    const [rows] = await pool.query('SELECT id, item_name AS name, quantity FROM inventory');
    res.json(rows);
}));

// Adds an item. The unique index on item_name means a repeated name adds to the existing row
app.post('/items', route(async (req, res) => {
    const { name, quantity } = req.body;
    if (typeof name !== 'string' || name.trim().length === 0 || name.length > 128
            || !Number.isInteger(quantity) || quantity < 0) {
        return res.status(400).json({ error: 'Invalid item data' });
    }
    await pool.query(
        'INSERT INTO inventory (item_name, quantity) VALUES (?, ?) ' +
        'ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)',
        [name, quantity]);
    res.status(201).json({ saved: true });
}));

// Sets an item's quantity
app.put('/items/:id', route(async (req, res) => {
    const { quantity } = req.body;
    if (!Number.isInteger(quantity) || quantity < 0) {
        return res.status(400).json({ error: 'Invalid quantity' });
    }
    const [result] = await pool.query('UPDATE inventory SET quantity = ? WHERE id = ?', [quantity, req.params.id]);
    if (result.affectedRows === 0) {
        return res.status(404).json({ error: 'Item not found' });
    }
    res.json({ updated: true });
}));

// Deletes an item
app.delete('/items/:id', route(async (req, res) => {
    await pool.query('DELETE FROM inventory WHERE id = ?', [req.params.id]);
    res.json({ deleted: true });
}));

const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => console.log('Inventory API running on port ' + PORT));
