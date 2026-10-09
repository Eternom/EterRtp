package fr.eternom.eterRtp.api;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Ce qu'EterRtp offre aux autres plugins : la téléportation aléatoire et son délai. Un autre plugin ne lit jamais le
 * délai dans Redis lui-même : il demande ici (la règle reste écrite à un seul endroit).
 *
 * <pre>
 *     // build.gradle.kts : compileOnly("com.github.Eternom:EterRtp:&lt;tag&gt;") ; plugin.yml : softdepend: [EterRtp]
 *     RtpApi.get().ifPresent(rtp -> rtp.teleport(player, world, 500, 3000));
 * </pre>
 * Absent (EterRtp non installé sur ce serveur) : get() est vide, la fonction qui en dépend est désactivée.
 */
public interface RtpApi {

    /** L'API d'EterRtp si le plugin tourne sur ce serveur. */
    static Optional<RtpApi> get() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(RtpApi.class));
    }

    /** Les mondes que /rtp propose sur ce serveur (config rtp.worlds). */
    List<String> worlds();

    /** Délai restant avant le prochain /rtp du joueur, en secondes (0 : libre ou dispensé). Bloquant (Redis). */
    long cooldownSeconds(Player player);

    /**
     * Comme /rtp dans le menu : un monde de rtp.worlds, avec le délai du rtp et les règles d'EterLib (combat, attente).
     * Thread principal ; le joueur est prévenu (délai, échec).
     */
    void teleport(Player player, String world);

    /**
     * Pour un autre plugin (ex : un portail) : un endroit sûr au hasard dans world, entre minRadius et maxRadius blocs
     * de son spawn, sans le délai du rtp (les règles d'EterLib s'appliquent). Thread principal.
     */
    void teleport(Player player, World world, int minRadius, int maxRadius);
}
