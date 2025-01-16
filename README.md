# Comic application

## Déscription

L'application de comics est destinée aux fans de bandes dessinées pour rechercher, visualiser et gérer leurs comics. Ils peuvent même recevoir des recommandations basées sur leur bibliothèque personnelle.

L'application basée sur Java, utilise Swing pour son interface utilisateur et FlatLaf pour un look moderne. L'application inclut également une base de données pour la création et la gestion de tables.

## Fonctionnalités

- Rechercher des comics par titre, avoir des informations sur ce titre (auteurs, personnages…)
- Rechercher des personnages, avoir des informations sur ce personnage et la liste des titres dans lesquels il apparaît
- Avoir des recommandations générales sur des comics
- Création d'un compte personnel
- Gestion de  bibliothèque de comics (comics que je possède, que je suis en train de lire et que j’ai terminé de lire)
- Avoir des recommandations de lecture basées sur sa propre bibliothèque


## Visuels
A ajouter

## Installation

### Prérequis

- Java Development Kit (JDK) 22 ou version ultérieure
- Git
- Maven

### Etapes

1. Cloner le dépot:
    ```sh
    git clone https://devops.telecomste.fr/prinfo/2024-25/info4.git
    cd info4
    ```

2. Construire le projet:
    ```sh
    mvn clean install
    ```

3. Lancer l'application:
    ```sh
    java -jar .\target\ComicAPP-1.0-SNAPSHOT.jar           
    ```

## Usage

Pour démarrer l'application, il suffit de lancer  le fichier `AppMain`. L'application appliquera le look moderne de FlatLaf, créera les tables nécessaires dans la base de données et démarrera l'interface utilisateur.