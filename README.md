# EterRtp

Téléportation aléatoire (`/rtp`) dans les mondes de **ce** serveur : à installer seulement là où l'on explore
(survie), pas sur un lobby. Sorti d'EterEssential pour pouvoir le placer serveur par serveur.
Document développeur, à tenir à jour avec le code.

## Prérequis

- **EterLib 1.7.0+** (`depend`) : base, Redis (facultatif), langues et textes communs, menus (cadre, bouton Retour),
  durées lisibles et téléportation commune.

## Fonctionnement

- **Menu** (`RtpMenu`) : les mondes de `rtp.worlds` (4 au plus, icône, anneau `min-radius`–`max-radius` autour du
  spawn du monde, nom affiché dans lang/ `rtp.worlds.<monde>`), tête du joueur avec le délai restant en direct.
- **Recherche** (`RtpService`) : un endroit sûr au hasard dans l'anneau, chunks chargés en tâche de fond par Paper
  (pas de lag), `attempts` essais ; jamais sur lave, eau, feu, cactus... Une seule recherche à la fois par joueur.
- **Départ** : par la téléportation commune d'EterLib (combat, délai commun, attente). Le délai propre au `/rtp`
  (`rtp.cooldown`, 30 min) ne démarre que si le joueur part vraiment.
- **Délai** (`RtpCooldown`) : valable sur tout le réseau. Avec Redis : clé `rtp:cooldown:<uuid>` qui expire seule ;
  sans Redis : table `eterrtp_cooldowns`. Jamais en mémoire (sinon il suffirait de changer de serveur).

## Commande et permissions

| Commande | Permission | Rôle |
|---|---|---|
| `/rtp` (`/randomtp`, `/wild`) | `eterrtp.use` (tous) | Menu des mondes où partir au hasard |

`eterrtp.bypass.cooldown` (op) : pas de délai propre au `/rtp`. `eterrtp.admin` regroupe tout.
Bouton du bas du menu : `menus.rtp.back-command` (vide = fermer).
