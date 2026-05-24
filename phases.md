# Phases de réalisation — Menko Agro API

> Stack : Spring Boot (Java) + Supabase local (PostgreSQL + Storage)
> Architecture : Monolithe modulaire — Clean Architecture par module

---

## Vue d'ensemble

```
Phase 0 → Fondations techniques
Phase 1 → Sécurité & Utilisateurs   ← bloque tout si mal fait
Phase 2 → Catalogue Produits         ← brique centrale
Phase 3 → Gestion des Stocks         ← dépend Phase 2
Phase 4 → Production                 ← dépend Phase 2 + 3
Phase 5 → Clients & Notifications    ← indépendant
Phase 6 → Ventes & Reçus             ← dépend Phase 2 + 3 + 5
Phase 7 → Rapports & Statistiques    ← dépend tout
Phase 8 → Finalisation               ← tests, docs, sécurité
```

---

## Phase 0 — Mise en place de l'environnement
> Statut : 🔄 En cours

- [] Supabase en local via Docker Compose (PostgreSQL + Storage)
- [ ] Création du projet Spring Boot (Spring Initializr)
- [ ] Configuration JPA/Hibernate → connexion à la base Supabase
- [ ] Mise en place de Flyway (migrations versionnées)
- [ ] Structure des modules (packages Clean Architecture)
- [ ] Gestion globale des erreurs + format de réponse API uniforme

---

## Phase 1 — Sécurité & Utilisateurs
> Statut : ⏳ En attente

- [ ] Entités : Utilisateur, Role, Permission, RolePermission
- [ ] Authentification JWT (Spring Security)
- [ ] Middleware de vérification des permissions sur chaque endpoint
- [ ] JournalAction — enregistrement automatique des opérations critiques
- [ ] Endpoints : login, gestion utilisateurs et rôles (admin)
- [ ] Tests unitaires

---

## Phase 2 — Catalogue Produits
> Statut : ⏳ En attente

- [ ] Entités : TypeCategorie, CategorieProduit, Produit, Conditionnement
- [ ] CRUD complet avec validation
- [ ] Création automatique du conditionnement "pièce" à la création d'un produit
- [ ] Tests unitaires

---

## Phase 3 — Gestion des Stocks
> Statut : ⏳ En attente

- [ ] Entités : Stock, MouvementStock
- [ ] Création automatique d'un stock à 0 lors de la création d'un produit
- [ ] Logique entrées / sorties en unités de base
- [ ] Système d'alertes au passage sous le seuil minimum
- [ ] Tests unitaires

---

## Phase 4 — Production
> Statut : ⏳ En attente

- [ ] Entités : Production (abstraite), ProductionAgricole, ElevageBande, EtapeProduction, CoutProduction
- [ ] Enregistrement des coûts par campagne
- [ ] Calcul automatique du coût total et de la rentabilité
- [ ] Alimentation du stock à la fin d'une production (entrée de stock)
- [ ] Tests unitaires

---

## Phase 5 — Clients & Notifications
> Statut : ⏳ En attente

- [ ] Entités : Client, Notification
- [ ] CRUD clients
- [ ] Envoi SMS (Twilio) et Email (SMTP)
- [ ] Fonction de relance par filtre client
- [ ] Tests unitaires

---

## Phase 6 — Ventes & Reçus
> Statut : ⏳ En attente

- [ ] Entités : Vente, LigneVente, Recu
- [ ] Enregistrement d'une vente avec déduction automatique du stock
- [ ] Génération PDF du reçu (iText ou JasperReports)
- [ ] Sauvegarde du PDF dans Supabase Storage
- [ ] Historique des ventes
- [ ] Tests unitaires

---

## Phase 7 — Rapports & Statistiques
> Statut : ⏳ En attente

- [ ] Calcul des marges par produit
- [ ] Rentabilité par campagne de production
- [ ] Chiffre d'affaires par période
- [ ] Top clients, produits les plus vendus
- [ ] Endpoints lecture seule optimisés (requêtes SQL directes)

---

## Phase 8 — Finalisation
> Statut : ⏳ En attente

- [ ] Documentation API complète (Swagger / OpenAPI)
- [ ] Tests d'intégration end-to-end
- [ ] Revue de sécurité (injections, accès non autorisés)
- [ ] Optimisation des requêtes lentes
