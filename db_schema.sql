-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

-- Months Table
CREATE TABLE months (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    starting_balance DOUBLE PRECISION NOT NULL,
    current_balance DOUBLE PRECISION NOT NULL,
    saving_goal DOUBLE PRECISION DEFAULT 0,
    currency VARCHAR(5) NOT NULL,
    UNIQUE(user_id, name)
);

-- Incomes Table
CREATE TABLE incomes (
    id SERIAL PRIMARY KEY,
    month_id INT NOT NULL REFERENCES months(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION NOT NULL,
    description TEXT,
    category VARCHAR(50),
    date DATE NOT NULL
);

-- Expenses Table
CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    month_id INT NOT NULL REFERENCES months(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION NOT NULL,
    description TEXT,
    category VARCHAR(50),
    date DATE NOT NULL
);

