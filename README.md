# MasterAnalytics

MasterAnalytics est une application de suivi des événements qui permet aux utilisateurs de visualiser des métriques sur un tableau de bord. Les clients peuvent créer différents sites sur lesquels ils peuvent suivre des événements et l'application fournit un script à intégrer dans le site du client pour visualiser les métriques sur le tableau de bord de l'application.

## Prérequis

- Node.js
- Java 17
- Maven

## Installation

1. Clonez le dépôt `https://github.com/HassenSamir/master-analytics` sur votre ordinateur.
2. Ouvrez une invite de commandes ou un terminal et accédez au répertoire racine du projet.
3. Installez les dépendances du frontend avec la commande `npm install` dans le dossier `frontend`.
4. Installez les dépendances du backend avec la commande `mvn install` dans le dossier `backend`.

## Configuration

1. Créez un fichier `.env` dans le dossier `backend/src/main/ressources` et ajoutez les variables d'environnement suivantes :
MONGO_DATABASE,
MONGO_USER,
MONGO_PASSWORD,
MONGO_CLUSTER,

2. Ouvrez le fichier `frontend/src/config.js` et remplacez API_URL=`http://localhost:8080` par l'URL de votre backend si vous utilisez un serveur distant ou un autre port.

## Exécution

1. Ouvrez une invite de commandes ou un terminal et accédez au dossier `backend`.
2. Exécutez la commande `mvn spring-boot:run` pour lancer le serveur de développement pour le backend.
3. Ouvrez une deuxième invite de commandes ou un deuxième terminal et accédez au dossier `frontend`.
4. Exécutez la commande `npm start` pour lancer le serveur de développement pour le frontend.
5. Accédez à l'URL `http://localhost:3000` pour accéder à l'application.

## Déploiement

1. Exécutez la commande `npm run build` dans le dossier `frontend` pour créer une version de production.
2. Copiez le contenu du dossier `frontend/build` dans le dossier `backend/src/main/resources/static`.
3. Exécutez la commande `mvn clean package` dans le dossier `backend` pour créer un fichier JAR.
4. Déployez le fichier JAR sur votre serveur. Vous pouvez le lancer avec la commande `java -jar masteranalytics-0.0.1-SNAPSHOT.jar`.

## Contact

Si vous avez des questions ou des commentaires, n'hésitez pas à nous contacter à l'adresse `shassen092@gmail.com`.