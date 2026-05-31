ALTER TABLE cout_production
    ADD COLUMN etape_id UUID NULL
        REFERENCES etape_production(id) ON DELETE SET NULL;