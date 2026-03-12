## 🔥 Coup de Feu

Coup de Feu est une application Android innovante conçue pour les cuisines professionnelles de haute performance. Elle résout le paradoxe de l'interaction numérique en milieu stérile : permettre aux cuisiniers d'accéder aux données vitales (bons, timers, spécificités) sans jamais toucher l'écran, garantissant ainsi une hygiène et un flux de travail irréprochables.

##  Auteurs

- Ilyas Bounoua

- Anas El-Houdi

## Concept et Problématique

En plein "coup de feu", un cuisinier ne peut pas manipuler une tablette classique (mains sales, occupées ou humides). Notre solution propose une interface "Hands-free" basée sur des interactions gestuelles et une architecture réactive pour synchroniser instantanément le Chef de Cuisine et ses Chefs de Partie.

## Architecture Technique

L'application repose sur les technologies Android les plus modernes :

- MVVM Pattern : Séparation stricte entre la logique métier (ViewModel) et l'affichage.

- Reactive Data Flow : Utilisation de StateFlow et collectAsState() pour des mises à jour en temps réel sans rechargement de page.

- Persistance des données : Implémentation de SavedStateHandle avec sérialisation personnalisée pour restaurer l'état de la cuisine (commandes, progression) même après un arrêt du processus.

## Fonctionnalités Principales

### 1. Vue Poste (Sofia)

Interface destinée aux Chefs de Partie (ex: Station Poisson).

Filtrage Intelligent : L'utilisateur ne voit que les tickets concernant sa station.

Haute Lisibilité : Design à haut contraste avec numéros de table surdimensionnés (56sp) pour une lecture à plus de 2 mètres.

Priorisation : Code couleur dynamique (Rouge pour l'urgence, Bleu pour le poste).

### 2. Vue Chef (Marc)

Interface de coordination pour le Chef de Cuisine.

Suivi en temps réel : Visualisation de l'avancement des 3 pôles (Froid, Poisson, Viande) pour chaque table via des jauges progressives.

Statistiques de service : Monitoring des tables actives, des tickets critiques et du temps moyen d'envoi.

### 3. Interaction Sans Contact (Air Gestures)

Simulation de Gestes : Système de validation par balayage aérien (simulé via des boutons dédiés).

Feedback Massif : Affichage d'un overlay plein écran ("GESTE DÉTECTÉ") pour confirmer l'action visuellement sans retour haptique.

### 4. Résilience et État

Sauvegarde Automatique : Grâce au versioning des données (DATA_VERSION), l'état de la brigade est conservé et protégé contre les crashs ou les mises à jour de données obsolètes.