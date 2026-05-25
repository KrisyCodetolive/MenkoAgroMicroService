# Fiche Technique Frontend — Menko Agro API

**Base URL** : `http://localhost:8080/api/v1`  
**Swagger** : `http://localhost:8080/api/v1/swagger-ui.html` (profil dev uniquement)  
**Authentification** : Bearer Token JWT dans le header `Authorization`

---

## Authentification

### Connexion
```
POST /auth/login
Body : { "username": "string", "password": "string" }
Response : { "token": "eyJ...", "type": "Bearer", "username": "...", "roles": [...] }
```
Stocker le token en `localStorage` ou `sessionStorage`. L'envoyer dans chaque requête :
```
Authorization: Bearer <token>
```

### Inscription
```
POST /auth/register
Body : { "username", "password", "email", "nom", "prenom", "roleId" }
```

---

## Module Catalogue (Produits)

### Types de catégorie (select fixe)
```
GET /catalogue/types
→ [{ id, nom, description }]
```
Utiliser pour peupler le select "Type" lors de la création de catégorie.

### Catégories de produit
```
GET /catalogue/categories              → toutes les catégories
GET /catalogue/categories?typeId=uuid  → filtrées par type
POST /catalogue/types                  → créer un type
POST /catalogue/categories             → créer une catégorie
PUT  /catalogue/types/{id}
PUT  /catalogue/categories/{id}
DELETE /catalogue/types/{id}
DELETE /catalogue/categories/{id}
```

### Produits
```
GET  /produits                        → liste (+ filtre ?categorieId=uuid)
GET  /produits/{id}
POST /produits                        → créer
PUT  /produits/{id}                   → modifier
DELETE /produits/{id}
```

**Champs clés d'un produit :**
```json
{
  "id": "uuid",
  "nom": "Gombo",
  "uniteBase": "kg",
  "categorieId": "uuid",
  "nomCategorie": "Légumes",
  "typeCategorie": "AGRICOLE",
  "estPerissable": true,
  "dureeConservationJours": 3,
  "conditionnements": [
    { "id": "uuid", "libelle": "Sac 5kg", "quantiteBase": 5.000, "prixVente": 2500, "estParDefaut": true }
  ]
}
```

### Conditionnements (unités de vente)
```
POST   /produits/{id}/conditionnements
PUT    /produits/{id}/conditionnements/{condId}
DELETE /produits/{id}/conditionnements/{condId}
PATCH  /produits/{id}/conditionnements/{condId}/defaut  → définir par défaut
```

---

## Module Stock

### Consulter les stocks
```
GET /stocks                    → tous les stocks
GET /stocks/{id}
GET /stocks/produit/{produitId} → stock d'un produit précis
GET /stocks/alertes            → produits sous le seuil d'alerte (quantite <= seuilAlerte)
GET /stocks/peremption         → produits périssables avec jours restants avant expiration
```

**Réponse péremption :**
```json
{
  "produitId": "uuid",
  "nomProduit": "Gombo",
  "quantite": 120.000,
  "dureeConservationJours": 3,
  "dernierEntreeDate": "2024-05-20",
  "dateExpirationEstimee": "2024-05-23",
  "joursRestants": 1,
  "estExpire": false
}
```
**Logique affichage** : `joursRestants < 0` → rouge "Expiré" | `0–2` → orange | `> 2` → vert.

### Historique des mouvements
```
GET /stocks/{id}/mouvements
→ [{ type: "ENTREE|SORTIE", quantite, motif: "PRODUCTION|VENTE|ACHAT|PERTE|AJUSTEMENT", reference, date }]
```

### Ajustement manuel
```
POST /stocks/{id}/ajustement
Body : { "type": "ENTREE|SORTIE", "quantite": 50.0, "motif": "ACHAT|PERTE|AJUSTEMENT", "reference": "optionnel" }
```
Motifs autorisés manuellement : `ACHAT`, `PERTE`, `AJUSTEMENT` (jamais `PRODUCTION` ou `VENTE` — automatiques).

### Modifier le seuil d'alerte
```
PATCH /stocks/{id}/seuil?seuilAlerte=100.0
```

---

## Module Production

### Références enum (charger au démarrage)
```
GET /productions/etapes/types
→ [{ "code": "PREPARATION_SOL", "libelle": "Préparation du sol" }, ...]

GET /productions/couts/categories
→ [{ "code": "ALIMENTATION", "libelle": "Alimentation animale" }, ...]
```

### Créer une production
```
POST /productions/agricoles
Body : { "produitId", "dateDebut", "zone"?, "superficieParcelle"? }

POST /productions/elevages
Body : { "produitId", "dateDebut", "reference"?, "nombreAnimaux"?, "typeAnimal"? }
```

### Suivre une production
```
GET /productions                        → liste (filtre ?statut=EN_COURS|TERMINEE|ABANDONNEE ou ?produitId=uuid)
GET /productions/{id}
```

**Champs clés :**
```json
{
  "type": "AGRICOLE|ELEVAGE",
  "statut": "EN_COURS|TERMINEE|ABANDONNEE",
  "coutTotal": 255000,
  "etapes": [...],   // AGRICOLE uniquement
  "couts": [...]
}
```

### Clôturer / Abandonner
```
PATCH /productions/{id}/cloturer
Body : { "quantiteProduite": 480.0, "dateFin"?, "tauxMortalite"? }
→ Alimente automatiquement le stock (ENTREE/PRODUCTION)

PATCH /productions/{id}/abandonner
→ Aucun impact sur le stock
```

### Étapes (AGRICOLE uniquement)
```
GET    /productions/{id}/etapes
POST   /productions/{id}/etapes
Body : { "type": "PREPARATION_SOL|PLANTATION|ENTRETIEN|RECOLTE", "dateRealisation", "notes"? }
PUT    /productions/{id}/etapes/{etapeId}
DELETE /productions/{id}/etapes/{etapeId}
```

### Coûts
```
GET    /productions/{id}/couts
POST   /productions/{id}/couts
Body : { "categorie": "ALIMENTATION", "libelle": "Achat aliment", "montant": 125000, "date": "2024-01-16" }
DELETE /productions/{id}/couts/{coutId}
```

---

## Module Ventes

### Créer une vente
```
POST /ventes
Body :
{
  "clientId": "uuid",
  "lignes": [
    {
      "produitId": "uuid",
      "conditionnementId": "uuid",
      "quantite": 3
    }
  ]
}
```
Le système :
1. Calcule `sousTotal = quantite × prixVente du conditionnement`
2. Déduit le stock automatiquement (`qteDeduitStock = quantite × quantiteBase`)
3. Génère un reçu PDF

### Consulter les ventes
```
GET /ventes              → toutes
GET /ventes?clientId=uuid → par client
GET /ventes/{id}
```

**Champs clés :**
```json
{
  "clientId": "uuid",
  "nomClient": "Konan Adjoua",
  "montantTotal": 7500,
  "dateVente": "2024-03-15",
  "lignes": [{ "nomProduit", "libelleConditionnement", "quantite", "prixUnitaire", "sousTotal" }],
  "recu": { "id", "numeroRecu", "cheminFichier", "genereA" }
}
```

### Télécharger le reçu PDF
```
GET /ventes/{id}/recu/pdf
→ Blob PDF — afficher dans un <iframe> ou forcer le téléchargement
```

---

## Module Clients

```
GET    /clients
GET    /clients/{id}
POST   /clients
Body : { "nom", "telephone"?, "email"?, "adresse"? }
PUT    /clients/{id}
DELETE /clients/{id}
```

---

## Module Utilisateurs & RBAC

### Rôles et permissions
```
GET /roles               → liste des rôles
GET /roles/{id}
GET /permissions         → toutes les permissions disponibles
```

### Utilisateurs
```
GET  /users
GET  /users/{id}
PUT  /users/{id}/role    → changer le rôle d'un utilisateur
```

**Permissions disponibles :**
| Code | Module | Description |
|---|---|---|
| `CATALOGUE_GERER` | CATALOGUE | Créer/modifier/supprimer types et catégories |
| `STOCK_VOIR` | STOCK | Consulter les stocks |
| `STOCK_MODIFIER` | STOCK | Ajustements manuels, seuil |
| `PRODUCTION_VOIR` | PRODUCTION | Voir les productions |
| `PRODUCTION_CREER` | PRODUCTION | Créer, suivre, clôturer |
| `COUT_ENREGISTRER` | PRODUCTION | Enregistrer des coûts |
| `VENTES` | VENTES | Créer et consulter les ventes |
| `CLIENTS` | CLIENTS | Gérer les clients |
| `RAPPORTS` | RAPPORTS | Accès aux rapports |
| `ADMINISTRATION` | ADMINISTRATION | Gestion utilisateurs |

---

## Gestion des erreurs

Toutes les erreurs suivent le même format :
```json
{
  "success": false,
  "message": "Description de l'erreur",
  "data": null
}
```

| Code HTTP | Cas |
|---|---|
| `400` | Données invalides (validation) |
| `401` | Token manquant ou expiré → rediriger vers login |
| `403` | Permission insuffisante → afficher message d'accès refusé |
| `404` | Ressource introuvable |
| `409` | Conflit (doublon, contrainte métier) |
| `422` | Erreur métier (stock insuffisant, production déjà clôturée) |

---

## Bonnes pratiques d'intégration

1. **Charger les enums au démarrage** : `GET /productions/etapes/types` et `GET /productions/couts/categories` → stocker en state global (Redux / Pinia / Zustand)

2. **Sélection de conditionnement dans une vente** : charger `GET /produits/{id}` pour obtenir les conditionnements, puis afficher le prix et calculer le total côté frontend avant soumission

3. **Badge périssable** : sur chaque carte produit, vérifier `estPerissable` et afficher `dureeConservationJours`

4. **Dashboard alertes** : combiner `GET /stocks/alertes` (stock bas) + `GET /stocks/peremption` (expiration) pour une vue synthétique

5. **Flux production → stock** : après clôture (`PATCH /cloturer`), rafraîchir le stock du produit concerné (`GET /stocks/produit/{produitId}`)