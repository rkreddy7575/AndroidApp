-- TrailBook schema v1

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE experiences (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    overview TEXT,
    destination VARCHAR(200),
    cover_image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    like_count INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_experiences_status_created ON experiences(status, created_at DESC);
CREATE INDEX idx_experiences_author ON experiences(author_id);
CREATE INDEX idx_experiences_destination ON experiences(destination);

CREATE TABLE timeline_entries (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    day_number INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    order_index INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE budget_items (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    category VARCHAR(100) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    notes TEXT
);

CREATE TABLE accommodations (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(300),
    cost DECIMAL(12, 2),
    notes TEXT
);

CREATE TABLE food_spots (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    cuisine VARCHAR(100),
    cost DECIMAL(12, 2),
    notes TEXT
);

CREATE TABLE transportations (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    mode VARCHAR(100) NOT NULL,
    details TEXT,
    cost DECIMAL(12, 2)
);

CREATE TABLE gallery_items (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    caption VARCHAR(300),
    order_index INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE videos (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    video_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500)
);

CREATE TABLE tips (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    content TEXT NOT NULL
);

CREATE TABLE packing_items (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    item VARCHAR(200) NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE comments (
    id UUID PRIMARY KEY,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE likes (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, experience_id)
);

CREATE TABLE bookmarks (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    experience_id UUID NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, experience_id)
);
