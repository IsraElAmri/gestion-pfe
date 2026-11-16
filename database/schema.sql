CREATE DATABASE IF NOT EXISTS gestion_pfe
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE gestion_pfe;

CREATE TABLE IF NOT EXISTS etudiant (
  id_etudiant INT AUTO_INCREMENT PRIMARY KEY,
  nom        VARCHAR(60) NOT NULL,
  prenom     VARCHAR(60) NOT NULL,
  email      VARCHAR(120) NOT NULL UNIQUE,
  classe     VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS encadreur (
  id_encadreur INT AUTO_INCREMENT PRIMARY KEY,
  nom          VARCHAR(60) NOT NULL,
  prenom       VARCHAR(60) NOT NULL,
  grade        VARCHAR(60) NOT NULL,
  email        VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS pfe (
  id_pfe          INT AUTO_INCREMENT PRIMARY KEY,
  titre           VARCHAR(150) NOT NULL,
  description     TEXT NULL,
  etat            VARCHAR(20) NOT NULL,
  date_soutenance DATE NULL,
  id_etudiant     INT NOT NULL UNIQUE,
  CONSTRAINT fk_pfe_etudiant
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS pfe_encadreur (
  id_pfe       INT NOT NULL,
  id_encadreur INT NOT NULL,
  PRIMARY KEY (id_pfe, id_encadreur),
  CONSTRAINT fk_pe_pfe
    FOREIGN KEY (id_pfe) REFERENCES pfe(id_pfe)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_pe_enc
    FOREIGN KEY (id_encadreur) REFERENCES encadreur(id_encadreur)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS soutenance (
  id_soutenance INT AUTO_INCREMENT PRIMARY KEY,
  date          DATETIME NOT NULL,
  salle         VARCHAR(40) NOT NULL,
  note          DECIMAL(5,2) NULL,
  id_pfe        INT NOT NULL UNIQUE,
  CONSTRAINT fk_soutenance_pfe
    FOREIGN KEY (id_pfe) REFERENCES pfe(id_pfe)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

