# Système de Gestion des Projets de Fin d'Études (PFE)

[![Java](https://img.shields.io/badge/Java-17%20%7C%208+-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-3776AB)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Application bureautique développée en Java Swing et JDBC pour administrer le cycle de vie académique des Projets de Fin d'Études (PFE), des soutenances, des étudiants et des encadreurs universitaires.

## Architecture

L'application repose sur le patron de conception **DAO (Data Access Object)** :
- **Couche Présentation** : Interface graphique Java Swing (`JFrame`, `JTable`, fenêtres modales) découpée par domaine métier (`EtudiantFrame`, `EncadreurFrame`, `PfeFrame`, `SoutenanceFrame`, `DashboardFrame`).
- **Couche Accès aux Données** : Objets DAO exécutant des requêtes SQL paramétrées (`PreparedStatement`) pour se prémunir des injections SQL.
- **Connexion Base de Données** : Connexion directe via `DriverManager.getConnection()`, lisant les paramètres depuis `src/main/resources/db.properties`.

## Modèle de Données

- **Étudiant** : Associé de manière unique à un PFE (relation 1:1).
- **Encadreur** : Associé aux PFE via la table de jointure `pfe_encadreur` (relation plusieurs-à-plusieurs, permettant le co-encadrement).
- **PFE** : Identifié par titre, description, date de soutenance et état (`PfeEtat`).
- **Soutenance** : Liée à un PFE (1:1), précisant la date, la salle et la note finale.

### États d'un PFE

L'énumération `PfeEtat` comprend 4 états :
- `EN_COURS` : Projet en cours d'élaboration par l'étudiant.
- `VALIDE` : Sujet validé pour planification de la soutenance.
- `SOUTENU` : Soutenance réalisée et note attribuée.
- `REFUSE` : Projet rejeté ou à réorienter.

## Démarrage Rapide

1. Créer la base de données :
   ```bash
   mysql -u root -p < schema.sql
   ```
2. Configurer les identifiants :
   Copier `src/main/resources/db.properties.example` vers `src/main/resources/db.properties` avec vos identifiants locaux.
3. Compiler et exécuter :
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="tn.pfe.gestion.Main"
   ```

## Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.
