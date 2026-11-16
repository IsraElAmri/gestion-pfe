-- Seed data (safe to run multiple times thanks to INSERT IGNORE)
USE gestion_pfe;

-- Students
INSERT IGNORE INTO etudiant(nom, prenom, email, classe) VALUES
('Ben Salah', 'Yassine', 'yassine.bensalah@ensit.tn', '4INFO-A'),
('Trabelsi', 'Mariem', 'mariem.trabelsi@ensit.tn', '4INFO-B'),
('Gharbi', 'Ahmed', 'ahmed.gharbi@ensit.tn', '4INFO-A');

-- Supervisors
INSERT IGNORE INTO encadreur(nom, prenom, grade, email) VALUES
('Bouaziz', 'Sami', 'Maitre Assistant', 'sami.bouaziz@ensit.tn'),
('Kammoun', 'Salma', 'Professeur', 'salma.kammoun@ensit.tn'),
('Haddad', 'Nour', 'Assistant', 'nour.haddad@ensit.tn');

-- Create 2 PFEs by selecting students by email (1 PFE per student)
INSERT IGNORE INTO pfe(titre, description, etat, date_soutenance, id_etudiant)
SELECT
  'Système de gestion des PFE',
  'Application Java (Swing/JDBC) pour gérer étudiants, encadreurs, PFE et soutenances.',
  'EN_COURS',
  NULL,
  e.id_etudiant
FROM etudiant e
WHERE e.email = 'yassine.bensalah@ensit.tn';

INSERT IGNORE INTO pfe(titre, description, etat, date_soutenance, id_etudiant)
SELECT
  'Application de suivi de soutenances',
  'Planification des soutenances, salles et notes avec historique.',
  'VALIDE',
  '2026-06-15',
  e.id_etudiant
FROM etudiant e
WHERE e.email = 'mariem.trabelsi@ensit.tn';

-- Link PFEs to encadreurs (many-to-many)
-- PFE 1: Yassine supervised by Sami + Salma
INSERT IGNORE INTO pfe_encadreur(id_pfe, id_encadreur)
SELECT p.id_pfe, enc.id_encadreur
FROM pfe p
JOIN etudiant e ON e.id_etudiant = p.id_etudiant
JOIN encadreur enc ON enc.email IN ('sami.bouaziz@ensit.tn', 'salma.kammoun@ensit.tn')
WHERE e.email = 'yassine.bensalah@ensit.tn';

-- PFE 2: Mariem supervised by Nour
INSERT IGNORE INTO pfe_encadreur(id_pfe, id_encadreur)
SELECT p.id_pfe, enc.id_encadreur
FROM pfe p
JOIN etudiant e ON e.id_etudiant = p.id_etudiant
JOIN encadreur enc ON enc.email = 'nour.haddad@ensit.tn'
WHERE e.email = 'mariem.trabelsi@ensit.tn';

-- Example soutenance for Mariem's PFE
INSERT IGNORE INTO soutenance(date, salle, note, id_pfe)
SELECT
  '2026-06-15 10:30:00',
  'B12',
  15.50,
  p.id_pfe
FROM pfe p
JOIN etudiant e ON e.id_etudiant = p.id_etudiant
WHERE e.email = 'mariem.trabelsi@ensit.tn';
