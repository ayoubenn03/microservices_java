# Microservices - Projet M2

Projet de microservices avec Spring Cloud (Eureka, Config Server, Gateway).

Chemin du projet : `C:\Users\ennou\dev\M2\microservices`

## Ordre de démarrage

Faut lancer dans cet ordre sinon ça marche pas (chacun avec `mvnw spring-boot:run` dans son dossier) :

1. eureka-server
2. config-server
3. api-gateway
4. product-service
5. book-service
6. loan-service

## Services

Tout passe par la gateway sur le port **8080**.

- **book-service** (port 8091) : gestion des livres
  - `GET /api/books`
  - `GET /api/books/{id}`
  - `GET /api/books/isbn/{isbn}`
  - `POST /api/books`
  - `PUT /api/books/{id}`
  - `DELETE /api/books/{id}`
  - `PATCH /api/books/{id}/decrement-stock`
  - `PATCH /api/books/{id}/increment-stock`

- **loan-service** (port 8092) : gestion des emprunts
  - `GET /api/loans`
  - `GET /api/loans/{id}`
  - `GET /api/loans/member/{memberName}`
  - `POST /api/loans`
  - `PATCH /api/loans/{id}/return`

## Tester

Voir `library.http` à la racine pour des exemples de requêtes (créer un livre, emprunter, tenter un 2e emprunt refusé, retourner, etc).
