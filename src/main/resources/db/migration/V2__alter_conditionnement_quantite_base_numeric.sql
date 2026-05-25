-- Passage de quantite_base de INTEGER à NUMERIC(10,3)
-- pour supporter les produits vendus en fractions d'unité (ex: 1.5 kg)
ALTER TABLE conditionnement
    ALTER COLUMN quantite_base TYPE NUMERIC(10,3) USING quantite_base::NUMERIC(10,3),
    ALTER COLUMN quantite_base SET DEFAULT 1.000;