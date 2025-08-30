-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create customers table
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    roles VARCHAR(50) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create movies table
CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100) NOT NULL,
    duration_min INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    poster_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create purchases table
CREATE TABLE purchases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    movie_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    payment_method VARCHAR(50),
    last4 VARCHAR(4),
    payment_note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT fk_purchase_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_purchase_movie FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- Create indexes
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_active ON customers(active);
CREATE INDEX idx_movies_active ON movies(active);
CREATE INDEX idx_movies_genre ON movies(genre);
CREATE INDEX idx_purchases_customer ON purchases(customer_id);
CREATE INDEX idx_purchases_movie ON purchases(movie_id);
CREATE INDEX idx_purchases_status ON purchases(status);

-- Insert seed data
-- Insert admin user (password: admin123)
INSERT INTO customers (id, first_name, last_name, email, phone, password_hash, roles, active) 
VALUES (
    uuid_generate_v4(),
    'Admin',
    'User',
    'admin@movietheater.com',
    '+1234567890',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- admin123
    'ADMIN',
    true
);

-- Insert regular user (password: user123)
INSERT INTO customers (id, first_name, last_name, email, phone, password_hash, roles, active) 
VALUES (
    uuid_generate_v4(),
    'John',
    'Doe',
    'user@movietheater.com',
    '+0987654321',
    '$2a$10$dXJ3SW6G7P9wuOCUc9a7Ze1.fQOCAmjvP2dVJsKwbFDM8MkUL8WsK', -- user123
    'USER',
    true
);


INSERT INTO movies (title, description, genre, duration_min, price, active) VALUES
('Spider-Man: Across the Spider-Verse', 'Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.', 'Action', 140, 12.50, true),
('Guardians of the Galaxy Vol. 3', 'Still reeling from the loss of Gamora, Peter Quill rallies his team to defend the universe and one of their own.', 'Sci-Fi', 150, 13.00, true),
('The Little Mermaid', 'A young mermaid makes a deal with a sea witch to trade her beautiful voice for human legs.', 'Family', 135, 11.00, true),
('Fast X', 'Dom Toretto and his family are targeted by the vengeful son of drug kingpin Hernan Reyes.', 'Action', 141, 13.50, true),
('Scream VI', 'The survivors of the Ghostface killings leave Woodsboro behind for a fresh start in New York City.', 'Horror', 123, 12.00, true),
('John Wick: Chapter 4', 'John Wick uncovers a path to defeating The High Table, but he must face off against a new enemy.', 'Action', 169, 14.00, true),
('Avatar: The Way of Water', 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora.', 'Sci-Fi', 192, 15.00, true),
('Top Gun: Maverick', 'After thirty years, Maverick is still pushing the envelope as a top naval aviator.', 'Action', 130, 13.00, true);