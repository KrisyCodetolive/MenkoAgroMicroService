-- Extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ─── CATALOGUE ────────────────────────────────────────────────────────────────

CREATE TABLE type_categorie (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom         VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE categorie_produit (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom              VARCHAR(100) NOT NULL,
    description      TEXT,
    type_categorie_id UUID NOT NULL REFERENCES type_categorie(id)
);

CREATE TABLE produit (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom                 VARCHAR(150) NOT NULL,
    description         TEXT,
    unite_base          VARCHAR(50) NOT NULL DEFAULT 'pièce',
    categorie_produit_id UUID NOT NULL REFERENCES categorie_produit(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE conditionnement (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id      UUID NOT NULL REFERENCES produit(id) ON DELETE CASCADE,
    libelle         VARCHAR(100) NOT NULL,
    quantite_base   INTEGER NOT NULL DEFAULT 1 CHECK (quantite_base > 0),
    prix_vente      NUMERIC(15,2) NOT NULL CHECK (prix_vente >= 0),
    est_par_defaut  BOOLEAN NOT NULL DEFAULT FALSE
);

-- ─── STOCK ────────────────────────────────────────────────────────────────────

CREATE TABLE stock (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id     UUID NOT NULL UNIQUE REFERENCES produit(id),
    quantite       NUMERIC(15,3) NOT NULL DEFAULT 0 CHECK (quantite >= 0),
    seuil_alerte   NUMERIC(15,3) NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE mouvement_stock (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stock_id    UUID NOT NULL REFERENCES stock(id),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('ENTREE', 'SORTIE')),
    quantite    NUMERIC(15,3) NOT NULL CHECK (quantite > 0),
    motif       VARCHAR(20) NOT NULL CHECK (motif IN ('PRODUCTION','VENTE','PERTE','ACHAT','AJUSTEMENT')),
    reference   VARCHAR(100),
    date        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── CLIENTS ──────────────────────────────────────────────────────────────────

CREATE TABLE client (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom         VARCHAR(150) NOT NULL,
    telephone   VARCHAR(20),
    email       VARCHAR(150),
    adresse     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE notification (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id   UUID NOT NULL REFERENCES client(id),
    canal       VARCHAR(10) NOT NULL CHECK (canal IN ('SMS', 'EMAIL')),
    objet       VARCHAR(200),
    contenu     TEXT NOT NULL,
    statut      VARCHAR(15) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE','ENVOYE','ECHEC')),
    envoye_a    TIMESTAMP
);

-- ─── VENTES ───────────────────────────────────────────────────────────────────

CREATE TABLE vente (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id      UUID NOT NULL REFERENCES client(id),
    montant_total  NUMERIC(15,2) NOT NULL DEFAULT 0,
    date_vente     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ligne_vente (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vente_id            UUID NOT NULL REFERENCES vente(id) ON DELETE CASCADE,
    produit_id          UUID NOT NULL REFERENCES produit(id),
    conditionnement_id  UUID NOT NULL REFERENCES conditionnement(id),
    quantite            NUMERIC(15,3) NOT NULL CHECK (quantite > 0),
    prix_unitaire       NUMERIC(15,2) NOT NULL,
    sous_total          NUMERIC(15,2) NOT NULL,
    qte_deduit_stock    NUMERIC(15,3) NOT NULL
);

CREATE TABLE recu (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vente_id        UUID NOT NULL UNIQUE REFERENCES vente(id),
    numero_recu     VARCHAR(50) NOT NULL UNIQUE,
    chemin_fichier  VARCHAR(500) NOT NULL,
    genere_a        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── PRODUCTION ───────────────────────────────────────────────────────────────

CREATE TABLE production (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produit_id  UUID NOT NULL REFERENCES produit(id),
    type        VARCHAR(20) NOT NULL CHECK (type IN ('AGRICOLE', 'ELEVAGE')),
    date_debut  DATE NOT NULL,
    date_fin    DATE,
    statut      VARCHAR(15) NOT NULL DEFAULT 'EN_COURS' CHECK (statut IN ('EN_COURS','TERMINEE','ABANDONNEE')),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    -- champs ProductionAgricole
    zone                VARCHAR(100),
    superficie_parcelle NUMERIC(10,2),
    -- champs ElevageBande
    reference           VARCHAR(50),
    nombre_animaux      INTEGER,
    type_animal         VARCHAR(100),
    taux_mortalite      NUMERIC(5,2)
);

CREATE TABLE etape_production (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    production_id   UUID NOT NULL REFERENCES production(id) ON DELETE CASCADE,
    type            VARCHAR(20) NOT NULL CHECK (type IN ('PREPARATION_SOL','PLANTATION','ENTRETIEN','RECOLTE')),
    date_realisation DATE NOT NULL,
    notes           TEXT
);

CREATE TABLE cout_production (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    production_id   UUID NOT NULL REFERENCES production(id) ON DELETE CASCADE,
    categorie       VARCHAR(20) NOT NULL CHECK (categorie IN ('INTRANTS','MAIN_OEUVRE','TRANSPORT','VETERINAIRE','ALIMENTATION','AUTRE')),
    libelle         VARCHAR(200) NOT NULL,
    montant         NUMERIC(15,2) NOT NULL CHECK (montant >= 0),
    date            DATE NOT NULL
);

-- ─── UTILISATEURS & SÉCURITÉ ──────────────────────────────────────────────────

CREATE TABLE role (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom         VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE permission (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200) NOT NULL,
    module      VARCHAR(20) NOT NULL CHECK (module IN ('VENTES','STOCK','PRODUCTION','CLIENTS','RAPPORTS','ADMINISTRATION'))
);

CREATE TABLE role_permission (
    role_id       UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE utilisateur (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom              VARCHAR(150) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id          UUID NOT NULL REFERENCES role(id),
    actif            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    dernier_acces    TIMESTAMP
);

CREATE TABLE journal_action (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    utilisateur_id UUID NOT NULL REFERENCES utilisateur(id),
    action       VARCHAR(100) NOT NULL,
    module       VARCHAR(20) NOT NULL CHECK (module IN ('VENTES','STOCK','PRODUCTION','CLIENTS','ADMINISTRATION')),
    entite       VARCHAR(100),
    entite_id    UUID,
    details      JSONB,
    date         TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── DONNÉES INITIALES ────────────────────────────────────────────────────────

INSERT INTO type_categorie (nom, description) VALUES
    ('AGRICOLE', 'Produits issus des cultures'),
    ('ELEVAGE', 'Produits issus de l''élevage');

INSERT INTO categorie_produit (nom, type_categorie_id) VALUES
    ('Cultures maraîchères',  (SELECT id FROM type_categorie WHERE nom = 'AGRICOLE')),
    ('Cultures annuelles',    (SELECT id FROM type_categorie WHERE nom = 'AGRICOLE')),
    ('Cultures pérennes',     (SELECT id FROM type_categorie WHERE nom = 'AGRICOLE')),
    ('Volaille',              (SELECT id FROM type_categorie WHERE nom = 'ELEVAGE')),
    ('Porcin',                (SELECT id FROM type_categorie WHERE nom = 'ELEVAGE')),
    ('Sous-produits élevage', (SELECT id FROM type_categorie WHERE nom = 'ELEVAGE'));

INSERT INTO permission (code, description, module) VALUES
    ('VENTE_VOIR',         'Consulter la liste des ventes',          'VENTES'),
    ('VENTE_CREER',        'Enregistrer une nouvelle vente',         'VENTES'),
    ('VENTE_SUPPRIMER',    'Supprimer une vente',                    'VENTES'),
    ('STOCK_VOIR',         'Consulter les niveaux de stock',         'STOCK'),
    ('STOCK_MODIFIER',     'Ajuster manuellement un stock',          'STOCK'),
    ('PRODUCTION_VOIR',    'Consulter les productions',              'PRODUCTION'),
    ('PRODUCTION_CREER',   'Créer une campagne de production',       'PRODUCTION'),
    ('COUT_ENREGISTRER',   'Saisir des coûts de production',         'PRODUCTION'),
    ('CLIENT_VOIR',        'Consulter la liste des clients',         'CLIENTS'),
    ('CLIENT_GERER',       'Créer et modifier des clients',          'CLIENTS'),
    ('RAPPORT_VOIR',       'Accéder aux rapports et rentabilité',    'RAPPORTS'),
    ('UTILISATEUR_GERER',  'Créer, modifier, désactiver des users',  'ADMINISTRATION');

INSERT INTO role (nom, description) VALUES
    ('Administrateur', 'Accès total au système'),
    ('Gérant',         'Consultation de tout, pas de configuration'),
    ('Commercial',     'Ventes et gestion clients'),
    ('Resp. Production', 'Productions, coûts et stocks');

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p WHERE r.nom = 'Administrateur';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'VENTE_VOIR','STOCK_VOIR','PRODUCTION_VOIR','CLIENT_VOIR','RAPPORT_VOIR'
) WHERE r.nom = 'Gérant';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'VENTE_VOIR','VENTE_CREER','CLIENT_VOIR','CLIENT_GERER'
) WHERE r.nom = 'Commercial';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'PRODUCTION_VOIR','PRODUCTION_CREER','COUT_ENREGISTRER','STOCK_VOIR','STOCK_MODIFIER'
) WHERE r.nom = 'Resp. Production';
