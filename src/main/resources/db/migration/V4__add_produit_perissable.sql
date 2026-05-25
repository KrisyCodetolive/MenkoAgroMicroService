ALTER TABLE produit
    ADD COLUMN est_perissable     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN duree_conservation_jours INTEGER;