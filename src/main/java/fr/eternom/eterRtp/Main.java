package fr.eternom.eterRtp;

import fr.eternom.eterLib.EterLib;
import fr.eternom.eterLib.helper.message.Messages;
import fr.eternom.eterRtp.listeners.Commands;
import fr.eternom.eterLib.helper.cache.Cooldowns;
import fr.eternom.eterRtp.module.rtp.RtpService;
import fr.eternom.eterRtp.module.rtp.RtpWorld;
import fr.eternom.eterRtp.api.RtpApi;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

/**
 * Téléportation aléatoire (/rtp) dans les mondes de CE serveur : à installer seulement sur les serveurs où l'on
 * explore (survie), pas sur un lobby.
 */
public final class Main extends JavaPlugin {

    /** Version minimale d'EterLib : délais partagés (Cooldowns) depuis 1.10.0. */
    private static final String REQUIRED_ETERLIB = "1.10.0";

    private Messages messages;
    private RtpService rtp;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // En premier : vérifie la version d'EterLib (un EterLib < 1.3.0 n'a pas requireVersion, d'où le catch)
        try {
            if (!EterLib.requireVersion(this, REQUIRED_ETERLIB)) {
                return;
            }
        } catch (LinkageError tooOld) {
            getLogger().severe("EterLib " + REQUIRED_ETERLIB + " ou plus récent est nécessaire.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        EterLib lib = EterLib.get();
        messages = lib.messages(this, "en_us", "fr_fr");
        // Délai propre au /rtp, sur tout le réseau (clé Redis rtp:cooldown:<uuid>)
        Cooldowns cooldown = new Cooldowns(lib.getRedis(), "rtp:cooldown",
                Duration.ofSeconds(Math.max(0, getConfig().getInt("rtp.cooldown", 1800))));
        rtp = new RtpService(this, cooldown, lib.getTeleports(), messages, lib.getServerName(),
                RtpWorld.load(getConfig().getConfigurationSection("rtp.worlds"), getLogger()), getConfig().getInt("rtp.attempts", 10),
                lib.backButton(getConfig().getString("menus.rtp.back-command", "")));

        // API pour les autres plugins (RtpApi.get())
        getServer().getServicesManager().register(RtpApi.class, rtp, this, ServicePriority.Normal);

        new Commands(this);
    }

    public Messages getMessages() {
        return messages;
    }

    public RtpService getRtp() {
        return rtp;
    }
}
