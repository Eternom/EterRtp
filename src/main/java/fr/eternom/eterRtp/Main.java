package fr.eternom.eterRtp;

import fr.eternom.eterLib.EterLib;
import fr.eternom.eterLib.helper.message.Messages;
import fr.eternom.eterLib.helper.sql.Database;
import fr.eternom.eterRtp.listeners.Commands;
import fr.eternom.eterRtp.module.rtp.RtpCooldown;
import fr.eternom.eterRtp.module.rtp.RtpService;
import fr.eternom.eterRtp.module.rtp.RtpWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

/**
 * Téléportation aléatoire (/rtp) dans les mondes de CE serveur : à installer seulement sur les serveurs où l'on
 * explore (survie), pas sur un lobby.
 */
public final class Main extends JavaPlugin {

    /** Version minimale d'EterLib : cadre commun, durées lisibles, téléportation commune depuis 1.7.0. */
    private static final String REQUIRED_ETERLIB = "1.8.0";

    /** Préfixe des tables d'EterRtp dans la base commune : eterrtp_cooldowns. */
    private static final String TABLE_PREFIX = "eterrtp_";

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
        // Délai sans Redis d'avant 1.0.1 (Redis obligatoire depuis) : sa table est retirée, pas de table morte
        Database database = lib.database(TABLE_PREFIX);
        database.execute("DROP TABLE IF EXISTS " + database.table("cooldowns"));

        RtpCooldown cooldown = new RtpCooldown(lib.getRedis(),
                Duration.ofSeconds(Math.max(0, getConfig().getInt("rtp.cooldown", 1800))));
        rtp = new RtpService(this, cooldown, lib.getTeleports(), messages, lib.getServerName(),
                RtpWorld.load(getConfig().getConfigurationSection("rtp.worlds"), getLogger()), getConfig().getInt("rtp.attempts", 10),
                lib.backButton(getConfig().getString("menus.rtp.back-command", "")));

        new Commands(this);
    }

    public Messages getMessages() {
        return messages;
    }

    public RtpService getRtp() {
        return rtp;
    }
}
