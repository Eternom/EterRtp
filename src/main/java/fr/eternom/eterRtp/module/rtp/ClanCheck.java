package fr.eternom.eterRtp.module.rtp;

import fr.eternom.eterClan.api.ClanApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Le terrain des clans (EterClan, facultatif) : un /rtp n'arrive ni dans un terrain, ni dans un chunk voisin. Classe à
 * part : l'API d'EterClan n'est chargée que si le plugin tourne. Thread principal, sans base.
 */
final class ClanCheck {

    private ClanCheck() {
    }

    static boolean nearClaim(Location location) {
        if (!Bukkit.getPluginManager().isPluginEnabled("EterClan")) {
            return false;
        }
        return ClanApi.get().map(api -> {
            for (int dx = -16; dx <= 16; dx += 16) {
                for (int dz = -16; dz <= 16; dz += 16) {
                    if (api.isClaimed(location.clone().add(dx, 0, dz))) {
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }
}
