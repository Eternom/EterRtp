# EterRtp

Téléportation aléatoire (`/rtp`) dans les mondes de **ce** serveur : à installer seulement là où l'on explore
(survie), pas sur un lobby. Sorti d'EterEssential pour pouvoir le placer serveur par serveur.
Document développeur, à tenir à jour avec le code.

## Prérequis

- **EterLib 1.10.0+** (`depend`) : base, Redis (obligatoire), langues et textes communs, menus (cadre, bouton Retour),
  durées lisibles et téléportation commune.

## Fonctionnement

- **Menu** (`RtpMenu`) : les mondes de `rtp.worlds` (4 au plus, icône, anneau `min-radius`–`max-radius` autour du
  spawn du monde, nom affiché dans lang/ `rtp.worlds.<monde>`), tête du joueur avec le délai restant en direct.
- **Recherche** (`RtpService`) : un endroit sûr au hasard dans l'anneau, chunks chargés en tâche de fond par Paper
  (pas de lag), `attempts` essais ; jamais sur lave, eau, feu, cactus... Une seule recherche à la fois par joueur.
- **Départ** : par la téléportation commune d'EterLib (combat, délai commun, attente). Le délai propre au `/rtp`
  (`rtp.cooldown`, 30 min) ne démarre que si le joueur part vraiment.
- **Délai** (`RtpCooldown`) : valable sur tout le réseau : clé Redis `rtp:cooldown:<uuid>` qui expire seule.
  Jamais en mémoire (sinon il suffirait de changer de serveur).

## Commande et permissions

| Commande | Permission | Rôle |
|---|---|---|
| `/rtp` (`/randomtp`, `/wild`) | `eterrtp.use` (tous) | Menu des mondes où partir au hasard |

`eterrtp.bypass.cooldown` (op) : pas de délai propre au `/rtp`. `eterrtp.admin` regroupe tout.
Bouton du bas du menu : `menus.rtp.back-command` (vide = fermer).

## API (pour les autres plugins)

`fr.eternom.eterRtp.api.RtpApi`, enregistrée dans les services de Bukkit au démarrage. Un autre plugin ne lit jamais le
délai dans Redis lui-même : il demande ici.

- `compileOnly("com.github.Eternom:EterRtp:<tag>")` (JitPack) et `softdepend: [EterRtp]` ;
- `RtpApi.get()` : vide si EterRtp n'est pas sur ce serveur (la fonction qui en dépend se désactive) ;
- `worlds()`, `cooldownSeconds(joueur)` (bloquant : hors du thread principal), `teleport(joueur, monde)` (comme le menu, avec le délai),
  `teleport(joueur, World, rayonMin, rayonMax)` (pour un portail par exemple : sans le délai du rtp, avec les règles
  d'EterLib).
