# Menko Agro API

API REST de gestion des activités de Menko Agro (productions agricoles, élevage, stocks, ventes, clients).

---

## Livrables attendus

Tout contributeur sur ce projet doit respecter les deux livrables suivants.

### Documentation API — Swagger

Chaque endpoint créé doit être documenté via les annotations Swagger/OpenAPI.
La documentation est accessible en local à l'adresse :

```
http://localhost:8080/swagger-ui/index.html
```

Annotations obligatoires sur chaque controller et endpoint :

```java
@Tag(name = "Produits", description = "Gestion du catalogue produits")
@Operation(summary = "Créer un produit", description = "Ajoute un nouveau produit au catalogue")
@ApiResponse(responseCode = "201", description = "Produit créé avec succès")
@ApiResponse(responseCode = "400", description = "Données invalides")
```

### Architecture Clean Code

Le code doit respecter la structure définie pour chaque module :

```
module/
├── domain/entity/       → entités JPA uniquement, pas de logique Spring
├── domain/repository/   → interfaces seulement, pas d'implémentation
├── application/service/ → logique métier, appelle les repositories
├── application/dto/     → entrées et sorties de l'API, jamais les entités brutes
├── infrastructure/      → implémentation des repositories avec JPA
└── presentation/        → controllers REST, appelle les services
```

Règles à respecter :
- Un controller n'appelle **jamais** directement un repository
- Un service ne retourne **jamais** une entité JPA directement — toujours un DTO
- Les entités JPA restent dans `domain/` et ne sortent pas vers `presentation/`
- Toute règle métier va dans `application/service/`, pas dans le controller

---

## Prérequis

Assure-toi d'avoir installé sur ta machine :

| Outil | Version minimale | Vérification |
|---|---|---|
| Java (JDK) | 17 | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Docker | 20+ | `docker --version` |
| Supabase CLI | 2.0+ | `supabase --version` |

---

## 1. Permissions Docker

Sur Linux, ton utilisateur doit avoir accès au daemon Docker.

```bash
sudo usermod -aG docker $USER
```

**Déconnecte-toi et reconnecte-toi** pour que le changement soit pris en compte.

Si tu veux un accès immédiat sans redémarrer la session :

```bash
sudo chmod 666 /var/run/docker.sock
```

> Cette commande est temporaire et se réinitialise au redémarrage.

---

## 2. Démarrer Supabase en local

Depuis le dossier `menko-agro-api/` :

```bash
supabase start
```

Le premier démarrage télécharge les images Docker — prévoir 2 à 5 minutes.

Une fois démarré, tu verras les informations de connexion :

```
API URL:     http://127.0.0.1:54331
DB URL:      postgresql://postgres:postgres@127.0.0.1:54332/postgres
Studio URL:  http://127.0.0.1:54333
```

> Les ports utilisés par ce projet sont **54331–54334** pour éviter les conflits
> avec d'autres instances Supabase sur la même machine.

### Arrêter Supabase

```bash
supabase stop
```

---

## 3. Lancer l'application Spring Boot

```bash
mvn clean spring-boot:run
```

L'application démarre sur **http://localhost:8080**.

Au premier démarrage, Flyway crée automatiquement toutes les tables et insère
les données initiales (catégories, rôles, permissions).

---

## 4. Vérifier que tout fonctionne

```bash
curl http://localhost:8080/actuator/health
```

Réponse attendue :

```json
{ "status": "UP" }
```

---

## 5. Accéder à Supabase Studio

Supabase Studio est une interface graphique pour explorer la base de données.

Ouvre dans ton navigateur : **http://127.0.0.1:54333**

---

## Structure du projet

```
src/main/java/com/menkoagro/api/
├── shared/                  → code commun (réponses API, gestion des erreurs)
└── modules/
    ├── auth/                → utilisateurs, rôles, permissions, journal d'audit
    ├── product/             → produits, catégories, conditionnements
    ├── stock/               → stocks et mouvements
    ├── production/          → campagnes agricoles et bandes d'élevage
    ├── sales/               → ventes, lignes de vente, reçus PDF
    └── customer/            → clients et notifications

src/main/resources/
├── application.yml          → configuration (DB, JWT, Flyway)
└── db/migration/
    └── V1__init_schema.sql  → création des tables + données initiales
```

Chaque module suit la structure **Clean Architecture** :

```
module/
├── domain/entity/       → entités JPA (modèle métier)
├── domain/repository/   → interfaces d'accès aux données
├── application/service/ → logique métier
├── application/dto/     → objets de transfert (entrée/sortie API)
├── infrastructure/      → implémentation JPA des repositories
└── presentation/        → controllers REST
```

---

## Variables d'environnement

Le fichier `application.yml` utilise des valeurs par défaut suffisantes pour le développement local.
Pour personnaliser, crée un fichier `.env` ou passe les variables au lancement :

| Variable | Défaut | Description |
|---|---|---|
| `JWT_SECRET` | `menko-agro-secret-key-...` | Clé de signature JWT — **à changer en production** |

```bash
JWT_SECRET=ma-cle-secrete mvn spring-boot:run
```

---

---

## NB — Workflow Git obligatoire

### Branche de travail

> Tout push doit être fait sur la branche `dev`. Ne jamais pousser directement sur `main`.

Créer la branche `dev` avant de commencer :

```bash
git checkout -b dev
git push -u origin dev
```

Travailler toujours depuis `dev` :

```bash
git checkout dev
# ... modifications ...
git add <fichiers>
git commit -m "feat(module): description courte"
git push origin dev
```


---

### Commit obligatoire après chaque feature

> Un commit doit être créé dès qu'une fonctionnalité est terminée et fonctionnelle.
> Ne pas attendre la fin de la phase pour commiter.

**Format du message de commit :**

```
<type>(<module>): <description courte>
```

| Type | Quand l'utiliser |
|---|---|
| `feat` | Nouvelle fonctionnalité |
| `fix` | Correction d'un bug |
| `refactor` | Réécriture sans changement de comportement |
| `test` | Ajout ou modification de tests |
| `docs` | Documentation uniquement |
| `chore` | Configuration, dépendances, build |

**Exemples de bons commits :**

```bash
git commit -m "feat(auth): ajout authentification JWT"
git commit -m "feat(product): CRUD catalogue produits avec conditionnements"
git commit -m "feat(stock): déduction automatique du stock à la vente"
git commit -m "fix(production): correction calcul rentabilité bande élevage"
git commit -m "docs(swagger): annotations OpenAPI sur ProductController"
```

**Ce qu'il ne faut pas faire :**

```bash
# Trop vague
git commit -m "update"
git commit -m "fix bug"
git commit -m "wip"

# Plusieurs features dans un seul commit
git commit -m "ajout produits, stock et ventes"
```

---

### Release obligatoire après chaque phase

A la fin de chaque phase, rédiger une release sur le dépôt Git qui présente :

- Le numéro et le nom de la phase terminée
- Ce qui a été implémenté
- Les fichiers créés ou modifiés
- Les migrations de base de données ajoutées (si applicable)
- Les éventuels points d'attention ou limitations connues

**Format de nommage des releases :**

```
v0.1.0 — Phase 0 : Mise en place de l'environnement
v0.2.0 — Phase 1 : Sécurité & Utilisateurs
v0.3.0 — Phase 2 : Catalogue Produits
...
```

**Exemple de description de release :**

```
## Phase 1 — Sécurité & Utilisateurs

### Implémenté
- Authentification JWT avec Spring Security
- Gestion des rôles et permissions configurables
- Journal d'audit des actions critiques (JournalAction)
- Endpoints : POST /auth/login, GET /auth/me

### Fichiers créés
- modules/auth/domain/entity/ — Utilisateur, Role, Permission, JournalAction
- modules/auth/application/service/ — AuthService, JwtService
- modules/auth/presentation/controller/ — AuthController
- db/migration/V2__auth_admin_user.sql

### Migrations
- V2 : création du premier compte administrateur

### Points d'attention
- Le JWT_SECRET doit être changé avant tout déploiement
```

---

## Stack technique

| Couche | Technologie |
|---|---|
| Framework | Spring Boot 3.4.5 |
| Langage | Java 17 |
| Base de données | PostgreSQL (via Supabase local) |
| ORM | Hibernate / Spring Data JPA |
| Migrations DB | Flyway |
| Sécurité | Spring Security + JWT |
| Build | Maven |
