# Backend

## Base de données

Le backend utilise MongoDB et contient 7 collections :

- `users` pour stocker tous les utilisateurs
- `sites` pour stocker tous les sites des utilisateurs, chaque utilisateur peut avoir un maximum de 3 sites et peut recevoir un maximum de 100 événements par jour pour chaque site.
- `roles` pour stocker les différents rôles des utilisateurs: ADMIN, MODERATOR ou USER. Lors de la création d'un compte, l'utilisateur obtient le rôle USER, pour lui donner un autre rôle, il faut le modifier directement dans la base de données.
- `event_click` pour stocker tous les événements de clic pour chaque site
- `event_page_change` pour stocker tous les événements de changement de page pour chaque site
- `event_resize` pour stocker tous les événements de redimensionnement de page pour chaque site
- `api_keys` qui stocke toutes les clés d'API pour chaque site. Cette clé d'API est utilisée pour créer des événements. Chaque fois qu'un événement est créé, le backend vérifie la validité de la clé d'API avant d'autoriser la création d'événement.

## Services

Le backend propose les services suivants :

- Authentification, inscription et connexion
- Création, récupération et suppression d'événements
- Récupération de métriques par site, utilisateur, date ou type d'événement
- CRUD pour les sites

## Utilisation

Pour utiliser l'API, veuillez vous référer à la documentation Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html). Vous y trouverez toutes les routes disponibles ainsi que les détails des paramètres à fournir pour chaque route.

## Auteur

Ce projet a été développé par HASSEN Samir.
