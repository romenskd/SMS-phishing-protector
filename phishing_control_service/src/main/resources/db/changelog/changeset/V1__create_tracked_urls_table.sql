CREATE TABLE tracked_urls (
    id BIGSERIAL PRIMARY KEY,
    url TEXT UNIQUE NOT NULL,
    is_safe BOOLEAN,
    last_accessed TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expiry_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


CREATE INDEX idx_tracked_urls_url ON tracked_urls (url);


CREATE INDEX idx_tracked_urls_expiry_at ON tracked_urls (expiry_at);


CREATE INDEX idx_tracked_urls_last_accessed ON tracked_urls (last_accessed);