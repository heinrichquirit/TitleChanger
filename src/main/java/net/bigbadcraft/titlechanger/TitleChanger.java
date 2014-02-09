package main.java.net.bigbadcraft.titlechanger;

import java.util.List;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

public class TitleChanger extends JavaPlugin {
	
	private final ChatColor G = ChatColor.GREEN;
	private final ChatColor R = ChatColor.RED;
	private final ChatColor W = ChatColor.WHITE;
	
	private List<String> reservedWords;
	private List<String> groups;
	
	private int titleLength;
	
	private final String guide = "Use /title set <title> or /title clear";
	
	private Chat chat = null;
	
	public void onEnable() {
		
		saveDefaultConfig();
		
		reservedWords = getConfig().getStringList("reserved-words");
		groups = getConfig().getStringList("player-groups");
		
		titleLength = getConfig().getInt("title-length");
		
		getCommand("title").setExecutor(this);
		
		setupChat();
	}
	
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
	
	public boolean onCommand(CommandSender cs, Command cmd, String lbl, String[] args) {
		
		if (!(cs instanceof Player)) {
			cs.sendMessage("Use this command in-game.");
			return true;
		}
		
		Player player = (Player) cs;
		
		if (cmd.getName().equalsIgnoreCase("title")) {
		
			if (!player.hasPermission("titlechanger.use")) {
				player.sendMessage(R + "You do not have permission.");
				return true;
			}
		
			if (args.length == 0) {
				player.sendMessage(G + guide);
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("set")) {
					player.sendMessage(R + guide);
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					chat.setPlayerPrefix(player, "");
					player.sendMessage(G + "You've cleared your title.");
				}
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("set")) {
					
					String title = ChatColor.translateAlternateColorCodes('&', args[1]);
					int strippedLength = ChatColor.stripColor(title).length();
					
					if (isReserved(title)) {
						player.sendMessage(R + "Your title cannot contain the following words: " + Joiner.on(", ").join(reservedWords));
						return true;
					}
						
					if (strippedLength <= titleLength) {
						
						for (World worlds : Bukkit.getWorlds()) {
							chat.setPlayerPrefix(worlds.getName(), player.getName(), W + "[" + title + W + "] " + getGroupColor(player));
						}
					
						player.sendMessage(G + "You've set your title to " + W + "[" + title + W + "]");
					} else {
						player.sendMessage(R + "Title must be " + titleLength + " characters or less.");
					}
				}
			}
		}
		return true;
	}
	
	private ChatColor getGroupColor(Player player) {
		for (String group : chat.getPlayerGroups(player)) {
			for (String names : groups) {
				String[] values = names.split(":");
				if (group.equals(values[0])) {
					return ChatColor.valueOf(values[1].toUpperCase());
				}
			}
		}
		return null;
	}
	
	private boolean isReserved(String string) {
		for (String s : reservedWords) {
			if (string.contains(s)) {
				return true;
			}
		}
		return false;
	}
}
