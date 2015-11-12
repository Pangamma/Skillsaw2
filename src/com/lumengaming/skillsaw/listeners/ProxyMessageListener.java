package com.lumengaming.skillsaw.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.lumengaming.skillsaw.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Taylor
 */
public class ProxyMessageListener implements PluginMessageListener{
    private final Main plugin;

    public ProxyMessageListener(Main aThis) {
        this.plugin = aThis;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
          return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        Bukkit.broadcastMessage(subchannel);
        if (subchannel.equals("SomeSubChannel")) {
          // Use the code sample in the 'Response' sections below to read
          // the data.
        }
    }

}
