-- Seed demo user and experiences
-- Register via API to obtain a live account; this seed user is for feed content display.
INSERT INTO users (id, username, email, password_hash, display_name, avatar_url, bio, created_at)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'demo',
    'demo@trailbook.app',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Demo Explorer',
    NULL,
    'Sharing structured travel experiences on TrailBook.',
    NOW()
);

INSERT INTO experiences (id, author_id, title, overview, destination, cover_image_url, status, like_count, comment_count, created_at, updated_at)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'Weekend in Kyoto',
    'A curated 3-day itinerary through temples, tea houses, and bamboo groves.',
    'Kyoto, Japan',
    'https://res.cloudinary.com/demo/image/upload/sample.jpg',
    'PUBLISHED',
    12,
    3,
    NOW(),
    NOW()
);

INSERT INTO timeline_entries (id, experience_id, day_number, title, description, order_index)
VALUES
    ('33333333-3333-3333-3333-333333333301', '22222222-2222-2222-2222-222222222222', 1, 'Arrival & Fushimi Inari', 'Early morning hike through the torii gates.', 0),
    ('33333333-3333-3333-3333-333333333302', '22222222-2222-2222-2222-222222222222', 2, 'Arashiyama Bamboo Grove', 'Rent a bike and explore the western districts.', 1);

INSERT INTO budget_items (id, experience_id, category, amount, currency, notes)
VALUES ('44444444-4444-4444-4444-444444444401', '22222222-2222-2222-2222-222222222222', 'Accommodation', 180.00, 'USD', 'Ryokan near Gion');

INSERT INTO tips (id, experience_id, content)
VALUES ('55555555-5555-5555-5555-555555555501', '22222222-2222-2222-2222-222222222222', 'Visit Fushimi Inari before 7 AM to avoid crowds.');
