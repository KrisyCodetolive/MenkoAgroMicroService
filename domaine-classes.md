# Brouillon — Modèle de domaine Menko Agro

> Ce fichier décrit les classes métier (entités du domaine), leurs attributs, méthodes et relations.
> Il sert de base à la conception UML et au schéma de base de données.

---

## Table des matières

1. [Produit](#1-produit)
2. [Conditionnement](#2-conditionnement)
3. [TypeCategorie](#3-typecategorie)
4. [CategorieProduit](#4-categorieproduit)
5. [Production](#5-production-classe-abstraite)
6. [ProductionAgricole](#6-productionagricole)
7. [ElevageBande](#7-elevagebande)
8. [EtapeProduction](#8-etapeproduction)
9. [CoutProduction](#9-coutproduction)
10. [Stock](#10-stock)
11. [MouvementStock](#11-mouvementstock)
12. [Vente](#12-vente)
13. [LigneVente](#13-lignevente)
14. [Recu](#14-recu)
15. [Client](#15-client)
16. [Notification](#16-notification)
17. [Utilisateur](#17-utilisateur)
18. [Role](#18-role)
19. [Permission](#19-permission)
20. [RolePermission](#20-rolepermission)
21. [JournalAction](#21-journalaction)
22. [Relations entre classes](#22-relations-entre-classes)

---

## 1. Produit

> Représente un produit commercialisé par Menko Agro, qu'il soit agricole ou issu de l'élevage.
> Chaque produit doit avoir au minimum un Conditionnement. Par défaut, ce conditionnement est "A la pièce" avec quantiteBase = 1, créé automatiquement à la création du produit.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Nom du produit (ex : maïs, poulet de chair) |
| `description` | string | Description courte du produit |
| `uniteBase` | string | Unité minimale de comptage : **pièce** par défaut (kg, litre, pièce...) |
| `categorie` | CategorieProduit | Catégorie du produit |
| `conditionnements` | Conditionnement[] | Formats de vente disponibles (minimum 1) |
| `createdAt` | datetime | Date de création |
| `updatedAt` | datetime | Date de dernière modification |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `calculerMarge()` | decimal | Marge par unité de base : prix unitaire de base − coût moyen de production |
| `estDisponible()` | boolean | Vérifie si le stock en unités de base est > 0 |
| `conditionnementParDefaut()` | Conditionnement | Retourne le conditionnement avec quantiteBase = 1 |

---

## 2. Conditionnement

> Représente un format de vente pour un produit.
> Un produit peut avoir plusieurs formats (à la pièce, plateau, carton...).
> Le stock est toujours décompté en unités de base grâce au champ quantiteBase.
>
> Règle : tout produit a automatiquement un conditionnement par défaut "A la pièce" (quantiteBase = 1).

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `produit` | Produit | Produit auquel ce format appartient |
| `libelle` | string | Nom du format (ex : "A la pièce", "Plateau de 30", "Carton de 360") |
| `quantiteBase` | integer | Nombre d'unités de base contenues (ex : 30 pour un plateau de 30 œufs) |
| `prixVente` | decimal | Prix de vente pour ce format en FCFA |
| `estParDefaut` | boolean | Vrai si c'est le conditionnement de base (quantiteBase = 1) |

### Exemple concret

```
Produit : Œuf  (uniteBase = "pièce")

Conditionnement "A la pièce"   → quantiteBase: 1   → prixVente: 75 FCFA   ← par défaut
Conditionnement "Plateau"      → quantiteBase: 30  → prixVente: 2 000 FCFA
Conditionnement "Carton"       → quantiteBase: 360 → prixVente: 22 000 FCFA

Vente de 2 plateaux → stock déduit : 2 × 30 = 60 pièces
```

---

## 3. TypeCategorie

> Représente la grande famille d'un produit. Stocké en base de données : on peut en ajouter sans toucher au code.
> Exemples : AGRICOLE, ELEVAGE, TRANSFORME, AQUACOLE, INTRANT...

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Libellé du type (ex : AGRICOLE, ELEVAGE) |
| `description` | string \| null | Description optionnelle |

---

## 4. CategorieProduit

> Représente la sous-famille d'un produit, rattachée à un TypeCategorie.
> Exemples : "Cultures maraîchères", "Volaille", "Porcin", "Céréales transformées"...
> Extensible : on ajoute une ligne en base sans modifier le code.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Libellé de la catégorie (ex : Cultures maraîchères) |
| `typeCategorie` | TypeCategorie | Famille parente |
| `description` | string \| null | Description optionnelle |

---

## 5. Production *(classe abstraite)*

> Représente une campagne de production. Sert de base commune aux productions agricoles et aux bandes d'élevage. Ne s'instancie pas directement.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `produit` | Produit | Produit issu de cette production |
| `dateDebut` | date | Date de début de la production |
| `dateFin` | date \| null | Date de fin (null si en cours) |
| `statut` | enum | `EN_COURS`, `TERMINEE`, `ABANDONNEE` |
| `createdAt` | datetime | Date de création |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `calculerCoutTotal()` | decimal | Somme de tous les CoutProduction liés |
| `calculerRentabilite()` | decimal | Revenus totaux issus des ventes − coût total |
| `estEnCours()` | boolean | Retourne vrai si statut = EN_COURS |

---

## 6. ProductionAgricole

> Spécialise Production pour les cultures (maïs, manioc, tomate...). Ajoute la zone de production et les étapes de culture.

### Attributs *(en plus de Production)*

| Nom | Type | Description |
|---|---|---|
| `zone` | string | Zone géographique (ex : Taabo, Bongo) |
| `superficieParcelle` | decimal \| null | Surface en hectares |
| `etapes` | EtapeProduction[] | Liste des étapes de la campagne |

---

## 7. ElevageBande

> Spécialise Production pour l'élevage. Chaque bande est un lot d'animaux élevés ensemble du début à la fin, avec une référence unique.

### Attributs *(en plus de Production)*

| Nom | Type | Description |
|---|---|---|
| `reference` | string | Référence unique de la bande (ex : B-2024-001) |
| `nombreAnimaux` | integer | Nombre d'animaux au démarrage |
| `typeAnimal` | string | Ex : poulet de chair, poule pondeuse, porc |
| `tauxMortalite` | decimal | Calculé automatiquement en fin de bande |

---

## 8. EtapeProduction

> Représente une étape dans le cycle d'une ProductionAgricole (préparation du sol, plantation, entretien, récolte).

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `production` | ProductionAgricole | Production parente |
| `type` | enum | `PREPARATION_SOL`, `PLANTATION`, `ENTRETIEN`, `RECOLTE` |
| `dateRealisation` | date | Date à laquelle l'étape a été réalisée |
| `notes` | string \| null | Observations éventuelles |

---

## 9. CoutProduction

> Enregistre un coût spécifique lié à une production (intrant, main-d'œuvre, transport, etc.).

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `production` | Production | Production concernée |
| `categorie` | enum | `INTRANTS`, `MAIN_OEUVRE`, `TRANSPORT`, `VETERINAIRE`, `ALIMENTATION`, `AUTRE` |
| `libelle` | string | Description du coût (ex : achat engrais NPK) |
| `montant` | decimal | Montant en FCFA |
| `date` | date | Date de la dépense |

---

## 10. Stock

> Représente le niveau de stock d'un produit à un instant donné, toujours exprimé en unités de base.
> Déclenche des alertes quand le seuil minimum est atteint.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `produit` | Produit | Produit concerné (relation 1-1) |
| `quantite` | decimal | Quantité disponible en **unités de base** |
| `seuilAlerte` | decimal | Seuil en unités de base en dessous duquel une alerte est déclenchée |
| `updatedAt` | datetime | Dernière mise à jour |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `estSousAlerte()` | boolean | Retourne vrai si quantite <= seuilAlerte |
| `appliquerMouvement(mouvement)` | void | Met à jour la quantité selon un MouvementStock |

---

## 11. MouvementStock

> Trace chaque entrée ou sortie de stock, toujours en unités de base.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `stock` | Stock | Stock concerné |
| `type` | enum | `ENTREE`, `SORTIE` |
| `quantite` | decimal | Quantité en **unités de base** |
| `motif` | enum | `PRODUCTION`, `VENTE`, `PERTE`, `ACHAT`, `AJUSTEMENT` |
| `reference` | string \| null | Référence liée (ex : ID vente, ID production) |
| `date` | datetime | Date et heure du mouvement |

---

## 12. Vente

> Représente une transaction de vente entre Menko Agro et un client.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `client` | Client | Client acheteur |
| `lignes` | LigneVente[] | Produits achetés dans cette vente |
| `montantTotal` | decimal | Calculé à partir des lignes |
| `dateVente` | datetime | Date et heure de la vente |
| `recu` | Recu \| null | Reçu généré pour cette vente |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `calculerMontantTotal()` | decimal | Somme des sous-totaux de chaque LigneVente |
| `genererRecu()` | Recu | Crée et attache un Recu à cette vente |

---

## 13. LigneVente

> Représente un article dans une vente.
> Le conditionnement choisi détermine le prix et la quantité déduite du stock.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `vente` | Vente | Vente parente |
| `produit` | Produit | Produit vendu |
| `conditionnement` | Conditionnement | Format choisi lors de la vente (plateau, carton, pièce...) |
| `quantite` | decimal | Nombre de conditionnements vendus (ex : 2 plateaux) |
| `prixUnitaire` | decimal | Prix du conditionnement au moment de la vente (snapshot) |
| `sousTotal` | decimal | quantite × prixUnitaire |
| `qteDeduitStock` | decimal | quantite × conditionnement.quantiteBase — déduit automatiquement |

---

## 14. Recu

> Document PDF généré pour une vente. Contient toutes les informations imprimables.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `vente` | Vente | Vente associée |
| `numeroRecu` | string | Numéro lisible (ex : REC-2024-0042) |
| `cheminFichier` | string | Chemin ou URL vers le fichier PDF |
| `genereA` | datetime | Date et heure de génération |

---

## 15. Client

> Représente un client de Menko Agro. Peut recevoir des notifications et être relancé.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Nom complet ou raison sociale |
| `telephone` | string \| null | Numéro de téléphone |
| `email` | string \| null | Adresse e-mail |
| `adresse` | string \| null | Adresse physique |
| `createdAt` | datetime | Date d'enregistrement |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `historiqueVentes()` | Vente[] | Retourne toutes les ventes du client |
| `peutEtreRelance()` | boolean | Vérifie qu'il a au moins un contact (tél ou email) |

---

## 16. Notification

> Représente un message envoyé à un client (SMS ou e-mail), pour relance ou alerte stock.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `client` | Client | Destinataire |
| `canal` | enum | `SMS`, `EMAIL` |
| `objet` | string \| null | Objet du message (pour email) |
| `contenu` | string | Corps du message |
| `statut` | enum | `EN_ATTENTE`, `ENVOYE`, `ECHEC` |
| `envoyeA` | datetime \| null | Date et heure d'envoi effectif |

---

## 17. Utilisateur

> Représente une personne ayant accès à l'application. Un utilisateur a un seul rôle qui définit ce qu'il peut voir et faire.
> Si un utilisateur quitte l'entreprise, on le désactive — son historique d'actions reste intact.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Nom complet |
| `email` | string | Adresse e-mail — sert d'identifiant de connexion |
| `motDePasseHash` | string | Mot de passe chiffré, jamais stocké en clair |
| `role` | Role | Rôle assigné par l'administrateur |
| `actif` | boolean | Faux = compte désactivé, accès refusé |
| `createdAt` | datetime | Date de création du compte |
| `dernierAcces` | datetime \| null | Date de la dernière connexion |

### Méthodes

| Nom | Retour | Description |
|---|---|---|
| `aLaPermission(code)` | boolean | Vérifie si son rôle possède la permission demandée |
| `estActif()` | boolean | Retourne vrai si le compte est actif |

---

## 18. Role

> Représente un profil d'accès configurable par l'administrateur.
> Chaque rôle regroupe un ensemble de permissions. L'admin peut créer autant de rôles que nécessaire.
> Exemples : "Gérant", "Commercial", "Stagiaire", "Caissier"...

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `nom` | string | Nom du rôle (ex : Commercial, Gérant) |
| `description` | string \| null | Description du périmètre de ce rôle |
| `permissions` | Permission[] | Liste des permissions accordées (via RolePermission) |
| `createdAt` | datetime | Date de création |

---

## 19. Permission

> Représente une action ou une vue précise dans le système.
> Les permissions sont prédéfinies dans le code — seul l'admin décide lesquelles attribuer à chaque rôle.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `code` | string | Identifiant technique unique (ex : `VENTE_CREER`, `RAPPORT_VOIR`) |
| `description` | string | Libellé lisible (ex : "Peut créer une vente") |
| `module` | enum | `VENTES`, `STOCK`, `PRODUCTION`, `CLIENTS`, `RAPPORTS`, `ADMINISTRATION` |

### Exemples de permissions par module

| Module | Code | Description |
|---|---|---|
| VENTES | `VENTE_VOIR` | Consulter la liste des ventes |
| VENTES | `VENTE_CREER` | Enregistrer une nouvelle vente |
| VENTES | `VENTE_SUPPRIMER` | Supprimer une vente |
| STOCK | `STOCK_VOIR` | Consulter les niveaux de stock |
| STOCK | `STOCK_MODIFIER` | Ajuster manuellement un stock |
| PRODUCTION | `PRODUCTION_CREER` | Créer une campagne de production |
| PRODUCTION | `COUT_ENREGISTRER` | Saisir des coûts de production |
| RAPPORTS | `RAPPORT_VOIR` | Accéder aux rapports et à la rentabilité |
| ADMINISTRATION | `UTILISATEUR_GERER` | Créer, modifier, désactiver des utilisateurs |

---

## 20. RolePermission

> Table de liaison entre Role et Permission.
> Représente le fait qu'un rôle possède une permission donnée.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `role` | Role | Rôle concerné |
| `permission` | Permission | Permission accordée à ce rôle |

---

## 21. JournalAction

> Enregistre toutes les opérations critiques effectuées dans le système.
> Permet à l'administrateur de savoir qui a fait quoi, quand et sur quoi.
> Non modifiable — aucun utilisateur ne peut effacer le journal, pas même l'admin.

### Attributs

| Nom | Type | Description |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `utilisateur` | Utilisateur | Qui a effectué l'action |
| `action` | string | Code de l'action (ex : `VENTE_CREEE`, `STOCK_AJUSTE`, `USER_DESACTIVE`) |
| `module` | enum | `VENTES`, `STOCK`, `PRODUCTION`, `CLIENTS`, `ADMINISTRATION` |
| `entite` | string | Nom de la classe concernée (ex : "Vente", "Stock") |
| `entiteId` | UUID \| null | Identifiant de l'enregistrement concerné |
| `details` | JSON \| null | Snapshot des données avant/après modification |
| `date` | datetime | Horodatage précis de l'action |

### Exemple de lignes dans le journal

```
date                 utilisateur       action               entite   détails
─────────────────────────────────────────────────────────────────────────────
23/05/2026 14h32    Konan Serge       VENTE_CREEE          Vente    { montant: 45000 }
23/05/2026 15h10    Adjoua Marie      STOCK_AJUSTE         Stock    { avant: 500, après: 430 }
23/05/2026 16h45    Konan Serge       VENTE_SUPPRIMEE      Vente    { montant: 12000 }  ⚠️
23/05/2026 17h00    Admin             USER_DESACTIVE       User     { email: x@y.com }
```

---

## 22. Relations entre classes

```
── CATALOGUE ──────────────────────────────────────────────────────────────
TypeCategorie (1) ──── (N) CategorieProduit (1) ──── (N) Produit (1) ──── (N) Conditionnement

── PRODUCTION & STOCK ─────────────────────────────────────────────────────
Produit (1) ──── (1) Stock (1) ──── (N) MouvementStock
Produit (1) ──── (N) Production ◄── abstraite
                          ├── ProductionAgricole (1) ──── (N) EtapeProduction
                          ├── ElevageBande
                          └── (1) ──── (N) CoutProduction

── VENTES ─────────────────────────────────────────────────────────────────
Vente (1) ──── (N) LigneVente ──── (1) Conditionnement
Vente (N) ──── (1) Client (1) ──── (N) Notification
Vente (1) ──── (1) Recu

── UTILISATEURS & SÉCURITÉ ────────────────────────────────────────────────
Utilisateur (N) ──── (1) Role (N) ──── (N) Permission  [via RolePermission]
Utilisateur (1) ──── (N) JournalAction
```

### Tableau récapitulatif des relations

| Classe A | Relation | Classe B | Notes |
|---|---|---|---|
| TypeCategorie | 1 → N | CategorieProduit | Un type regroupe plusieurs catégories |
| CategorieProduit | 1 → N | Produit | Une catégorie contient plusieurs produits |
| Produit | 1 → N | Conditionnement | Minimum 1 (pièce par défaut) |
| Conditionnement | 1 → N | LigneVente | Le format choisi lors de la vente |
| Produit | 1 → 1 | Stock | Un produit = un stock (en unités de base) |
| Stock | 1 → N | MouvementStock | Chaque stock trace ses mouvements |
| Produit | 1 → N | Production | Un produit peut avoir plusieurs campagnes |
| Production | 1 → N | CoutProduction | Tous les coûts liés à la production |
| ProductionAgricole | 1 → N | EtapeProduction | Étapes propres à l'agricole |
| LigneVente | N → 1 | Vente | Plusieurs lignes forment une vente |
| Vente | N → 1 | Client | Une vente appartient à un client |
| Vente | 1 → 1 | Recu | Chaque vente peut générer un reçu |
| Client | 1 → N | Notification | Un client peut recevoir plusieurs notifications |
| Utilisateur | N → 1 | Role | Un utilisateur a un seul rôle |
| Role | N ↔ N | Permission | Via RolePermission |
| Utilisateur | 1 → N | JournalAction | Toutes les actions d'un utilisateur sont tracées |
