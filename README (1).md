# readme-guide-JAVA
Guide détaillé pour notre projet (application Java) appelé Artizina sur le thème de l'art 
# ARTIZINA
Une plateforme desktop innovante dédiée à la promotion de l’ARTISANAT TUNISIEN, développée avec JAVA (JavaFX),PYTHON (Flask pour Face ID), JavaScript, CSS, et intégrant des APIs avancées (STRIPE, HUGGING FACE, DANDELION, CHATBASE, GOOGLE GEMINI, EDENAI) pour la gestion des utilisateurs, paiements, portfolios, candidatures, et un chatbot personnalisé. Le projet utilise JWT pour les sessions, two-factor authentication (2FA), et un serveur Flask pour l’authentification par Face ID.
# DESCRIPTION
ARTIZINA est une plateforme web réalisée dans le cadre du PROJET INTÉGRÉ WEB JAVA de la 3ÈME ANNÉE, CLASSE A44 à ESPRIT SCHOOL OF ENGINEERING, sous la direction du PROFESSEUR MOHAMED RIDHA BOULARES. Elle vise à promouvoir l’ARTISANAT TUNISIEN en offrant une gestion intuitive des utilisateurs, produits, événements, formations, partenariats, et créations. Construite avec JavaFX,MySQL, JavaScript, CSS, et Scene Builder, elle intègre des APIs avancées :

STRIPE pour les paiements (achats, dons).
HUGGING FACE pour évaluer les portfolios artistiques.
DANDELION pour analyser les lettres de motivation avec liens directs vers Wikipedia dans le frontend.
CHATBASE pour un chatbot frontend personnalisé.
Flask Server pour l’authentification via Face ID (comparaison des photos avec la base de données).
GOOGLE GEMINI et EDENAI pour l’analyse de contenu/images.
Google OAuth pour la connexion via Google.
Intelligence Artificielle pour des analyses avancées.
Systéme de fideletié pour reduire les prix et promouvoir les ventes .
Algorithme de regression linéaire (préduction des ventes futures des produits)
Prototype d’un modèle 3D du produit developpe dans le cadre du formation
Affichage de la localisation du lieu de formation sur une carte interactive.
Système de prédiction permettant d’évaluer si la formation correspond ou non aux besoins du client.
Affichage de la localisation du lieu de l’évènement sur une carte interactive.
Affichage de la météo d’aujourd’hui et prévision de la météo du 1er jour de l’évènement.
Dashboard interactive et dynamique même style qu’une Dashboard Power Bi.



# Fonctionnalités de sécurité :

Hachage des mots de passe avec BCrypt.
Two-factor authentication (2FA) via e-mail ou SMS.
E-mails pour bienvenue, réinitialisation de mot de passe, et notifications.
Sessions sécurisées avec JWT (JSON Web Tokens).
Espace personnel avec gestion des sessions.
Contact space avec notifications (cloche) et réponses directes via e-mail (ouvre Gmail/Outlook).
Statistiques des recherches, e-mails envoyés, et exportations en PDF/Excel.
Pagination des e-mails envoyés et des recherches.

Gestion des candidatures/partenariats :

CRUD pour les partenariats.
Candidatures avec portfolios (évalués par HUGGING FACE) et lettres de motivation (analysées par DANDELION).
Statut des partenariats mis à jour automatiquement selon les dates de début/fin (bouton "Postuler" grisé si expiré).
Exportation PDF personnalisée des candidatures (données du candidat, partenariat, et candidature).

Gestion des produits/commandes :

Crud pour les produits et commandes
Affichage des produits sous forme d’un store avec different choix de filtrage 
Exportation des commande en pdf(inclus prix HT,statut coloré,qr code)
Suivi des ventes et chiffre d’affaires via statistique interactives
Preduction des ventes

Gestion des formations/certificats :
CRUD pour les formations et les certificats
Affichage des formations sous forme de catalogue avec filtres par catégorie, durée ou niveau
Génération des certificats en PDF (inclus nom du participant, titre de la formation)
Suivi des inscriptions et taux de réussite via des statistiques interactives
Système de recommandation prédictif pour suggérer les formations adaptées au profil de l'utilisateur

Gestion des Evenements/Dons :

Crud pour les Evenements et les Dons.
Affichage des Evenements sous forme de grille avec progression bar, filtrage, Tri et recherche dynamique
Affichage de progrssion des dons par rapport à l’objectif avec des statistiques.

Gestion des Creation/Commentaire

Génération d’œuvres via formulaire ou import direct, prévisualisation en temps réel, prise en charge des fichiers multimédias (image, audio, vidéo), assignation à une catégorie existante, détection automatique du type de contenu, gestion des métadonnées (titre, description, tags), sauvegarde en brouillon ou publication immédiate, et attribution automatique à l’utilisateur connecté.





# Principales fonctionnalités

GESTION DES UTILISATEURS : Inscription avec e-mail de bienvenue, connexion via Face ID (serveur Flask), Google OAuth, ou mot de passe, déconnexion, espace personnel avec détails, gestion des sessions (JWT), 2FA, et réinitialisation de mot de passe par e-mail.
GESTION DES PRODUITS ARTISANAUX : store permettant aux clients de parcourir et sélectionner les produits artisanaux disponibles,panier pour consulter et gérer les produits ajoutés avant la validation de commande , paiement sécurisé via l’API Stripe et envoi automatique d’un e-mail de confirmation après chaque commande efféctués.
GESTION DES ÉVÈNEMENTS ET DONS : Organisation d’évènements et dons via STRIPE.
GESTION DES FORMATIONS ET CERTIFICATS : Cours en présentielle avec certificats en PDF.
GESTION DES PARTENARIATS ET CANDIDATURES : CRUD pour les partenariats, liste et candidature pour clients (portfolios évalués par HUGGING FACE, motivations analysées par DANDELION), pagination, export PDF/Excel, statistiques (candidatures, partenariats actifs).
GESTION DES CRÉATIONS ET COMMENTAIRES : Publication d’œuvres avec commentaires.
CONTACT SPACE : Envoi de messages avec notifications (cloche) et réponses via e-mail.
STATISTIQUES : Recherches, exportations en PDF/Excel . pagination.
Pagination.

# Rôles des utilisateurs

ADMIN : Gère tous les CRUD pour les utilisateurs, produits, événements, formations, partenariats, et créations.

ARTISAN : 
-Gère les CRUD des partenariats, les candidatures reçues, et publie des produits artisanaux et créations.
-suivre les ventes et le chiffres d’affaires grâce à des statistiques interactives et détaillées
-gestion de stock compléte avec alerte en cas de repture.
-exportation pdf des commandes
-Publication des Formations et Attribution des certificats.
-Ajout d’évènements.


CLIENT : 
-Consulte la liste des partenariats, postule à un partenariat, gère ses candidatures dans l’espace personnel.
-parcourir et sélectionner les produit artisanaux disponibles.
-consulter et gérer les commandes  ajoutés dans le panier  avant la validation de commande 
-Accès à la liste des formations proposées, avec des options de tri et de recherche, et réservation directe via la plateforme.
-Accès à la liste des évènements proposées, avec des options de tri et de recherche, et Faire des dons directe via la plateforme.



# TABLE DES MATIÈRES

INSTALLATION
UTILISATION
CONTRIBUTIONS
LICENCE

# INSTALLATION
Suivez ces étapes pour configurer ARTIZINA localement avec XAMPP, IntelliJ IDEA, et Scene Builder :

1. Clonez le dépôt depuis GitHub :
git clone https://github.com/feriel-belhaj/PIDEV.git
cd PIDEV

2. Placez le projet dans le dossier htdocs de XAMPP (par exemple, C:\xampp\htdocs\PIDEV).

3. Installez les dépendances Maven :

Ouvrez le projet dans IntelliJ IDEA.
Exécutez mvn clean install pour installer les dépendances définies dans pom.xml.


4. Configurez la base de données :

Démarrez MySQL depuis l’interface de XAMPP.
Exécutez les migrations Hibernate :mvn hibernate:run




5. Configurez le serveur Flask pour Face ID :

Installez Python et Flask :pip install flask opencv-python numpy


6. Exécutez le serveur Flask :python faceid_server.py




7. Démarrez l’application :

Exécutez la classe principale dans IntelliJ IDEA (tn.esprit.workshop.tests.JavaFx).


8. Utilisez Scene Builder pour modifier les interfaces JavaFX (.fxml).


# Prérequis

XAMPP (Apache et MySQL activés).
Java 17 ou supérieur.
MySQL 5.7 ou supérieur (inclus dans XAMPP).
Maven (inclus dans IntelliJ IDEA).
Node.js (pour les assets frontend).
Python 3.8+ (pour le serveur Flask).
Git.
IntelliJ IDEA.
Scene Builder.
Clés API pour STRIPE, HUGGING FACE, DANDELION, CHATBASE, GOOGLE GEMINI, EDENAI, Google OAuth.
Service SMTP (Gmail configuré).

Installation de Java (si hors XAMPP)

Windows : Téléchargez Java 17 depuis jdk.java.net.
Vérifiez l’installation :java -version



# UTILISATION

1. Accédez à l’application via intellige.
2. Créez un compte (un e-mail de bienvenue sera envoyé) ou connectez-vous avec :
Face ID (via webcam, photo envoyée au serveur Flask pour comparaison).
Google OAuth.
Mot de passe avec 2FA (code envoyé par e-mail/SMS).


3. Explorez les modules dans l’espace personnel, selon votre rôle :
ADMIN : Gère tous les CRUD pour utilisateurs, produits, événements, formations, partenariats, et créations.
ARTISAN : Gère les CRUD des partenariats, candidatures reçues, et publie produits artisanaux et créations.
CLIENT : Consulte la liste des partenariats, postule (bouton grisé si expiré), gère ses candidatures (portfolio via HUGGING FACE, motivation via DANDELION avec liens Wikipedia).


4. Autres modules :
Produits artisanaux : Achetez avec STRIPE.
Événements et dons : Participez ou faites un don via STRIPE.
Formations et certificats : Suivez des cours et recevez un certificat PDF.
Créations et commentaires : Interagissez via le chatbot CHATBASE.
Contact space : Envoyez un message (notifié par cloche), répondez via e-mail (ouvre Gmail/Outlook).
Statistiques : Consultez les recherches, e-mails envoyés, exportez en PDF/Excel avec pagination.


5. En cas de mot de passe oublié, recevez un code de réinitialisation par e-mail.
6. Utilisez IntelliJ IDEA et Scene Builder pour personnaliser le code ou l’interface.

# CONTRIBUTIONS
Nous remercions tous les contributeurs pour leur travail sur ARTIZINA, réalisé dans le cadre de la 3ÈME ANNÉE, CLASSE A44 à ESPRIT SCHOOL OF ENGINEERING, sous la direction du PROFESSEUR MOHAMED RIDHA BOULARES.
Contributeurs

FATMA ZAHRA CHAKROUN : Gestion des utilisateurs (inscription, connexion, Face ID, Google OAuth, 2FA, e-mails).
FERIEL BEL HADJ JAMMAR : Gestion des partenariats et candidatures (CRUD, analyse via Hugging Face/Dandelion, export PDF, statut automatique).
RAWEN MEZZI : Gestion des formations et certificats (cours, génération de certificats).
TAOUFIK KRID : Gestion des produits artisanaux (catalogue, paiements Stripe).
ANAS ALLAM : Gestion des événements et dons (organisation, paiements Stripe).
YOUSSEF FADDEOUI : Gestion des créations, commentaires, contact space, et intégration du chatbot Chatbase.

# Comment contribuer ?

1. Fork le projet sur GitHub : https://github.com/feriel-belhaj/PIDEVJAVA.
Clonez votre fork :git clone https://github.com/feriel-belhaj/PIDEVJAVA.git
cd PIDEVJAVA


2. Créez une nouvelle branche :git checkout -b votre-fonctionnalite


3. Faites vos modifications dans IntelliJ IDEA ou Scene Builder et validez :git add .
git commit -m "Ajout de votre-fonctionnalite"


4. Poussez vos modifications :git push origin votre-fonctionnalite


5. Soumettez une Pull Request sur GitHub en décrivant vos changements.

# LICENCE
Pas de licence pour notre projet.
# Mots-clés
JAVA, JAVAFX, SPRING, HIBERNATE, PYTHON, FLASK, ARTISANAT TUNISIEN, STRIPE PAYMENT API, HUGGING FACE API, DANDELION API, CHATBASE, FACE ID, GOOGLE GEMINI API, EDENAI API, GOOGLE OAUTH, JWT, 2FA, HACHAGE MOT DE PASSE, E-MAIL NOTIFICATION, DÉVELOPPEMENT WEB, ESPRIT SCHOOL OF ENGINEERING, JAVASCRIPT, CSS, SCENE BUILDER, INTELLIJ IDEA, XAMPP, GESTION DES PARTENARIATS, ESPACE PERSONNEL, CHATBOT FRONTEND, EXPORT PDF, STATISTIQUES, PAGINATION, systéme de fidélité,facture,regression lineaire,preduction, chiffre d’affaire,TVA,QR code , modèle 3D , openstreetmap, OpenWeatherMap.

