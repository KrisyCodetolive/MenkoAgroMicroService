-- Ajout de la permission CATALOGUE_GERER pour la gestion du référentiel
-- (types de catégorie et catégories de produits)
ALTER TABLE permission
    DROP CONSTRAINT IF EXISTS permission_module_check;

ALTER TABLE permission
    ADD CONSTRAINT permission_module_check
    CHECK (module IN ('VENTES','STOCK','PRODUCTION','CLIENTS','RAPPORTS','ADMINISTRATION','CATALOGUE'));

INSERT INTO permission (code, description, module) VALUES
    ('CATALOGUE_GERER', 'Créer, modifier et supprimer les types et catégories de produits', 'CATALOGUE');

-- Attribuer la permission à l'Administrateur uniquement
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.nom = 'Administrateur'
  AND p.code = 'CATALOGUE_GERER';