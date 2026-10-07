package fr.eternom.eterRtp.listeners;

import fr.eternom.eterRtp.Main;
import fr.eternom.eterRtp.module.rtp.RtpCommand;
import org.bukkit.command.PluginCommand;

import java.util.List;
import java.util.Objects;

public class Commands {

    public Commands(Main main) {
        PluginCommand rtp = Objects.requireNonNull(main.getCommand("rtp"), "Commande absente du plugin.yml : rtp");
        rtp.setExecutor(new RtpCommand(main.getRtp(), main.getMessages()));
        rtp.setTabCompleter((sender, command, label, args) -> List.of());
    }
}
