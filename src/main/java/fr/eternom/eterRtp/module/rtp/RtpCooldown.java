package fr.eternom.eterRtp.module.rtp;

import fr.eternom.eterLib.helper.cache.RedisCache;

import java.time.Duration;
import java.util.UUID;

/**
 * Délai propre à /rtp (en plus du délai commun des téléportations), valable sur tout le réseau : une clé Redis qui
 * expire seule (jamais en mémoire, sinon il suffirait de changer de serveur). Appels bloquants : hors du thread principal.
 */
public class RtpCooldown {

    private final RedisCache redis;
    private final Duration cooldown;

    public RtpCooldown(RedisCache redis, Duration cooldown) {
        this.redis = redis;
        this.cooldown = cooldown;
    }

    /** Secondes restantes avant le prochain /rtp, 0 s'il est possible. */
    public long remainingSeconds(UUID player) {
        long until = redis.get(key(player)).map(Long::parseLong).orElse(0L);
        long remaining = until - System.currentTimeMillis();
        return remaining <= 0 ? 0 : (remaining + 999) / 1000;
    }

    public void start(UUID player) {
        if (cooldown.isZero()) {
            return;
        }
        long until = System.currentTimeMillis() + cooldown.toMillis();
        redis.set(key(player), String.valueOf(until), cooldown);
    }

    private static String key(UUID player) {
        return "rtp:cooldown:" + player;
    }
}
