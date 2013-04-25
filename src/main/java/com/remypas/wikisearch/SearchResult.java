package com.remypas.wikisearch;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class SearchResult extends BukkitRunnable {

	private final CommandSender sender;
	private final List<CommandSender> recipients;
	private final String message;
	
	public SearchResult(CommandSender sender, List<CommandSender> recipients,
			String message) {
		this.sender = sender;
		this.recipients = recipients;
		this.message = message;
	}

	public void run() {
		if (this.recipients == null) {
			Bukkit.broadcastMessage(this.message);
		}
		
		else if (this.recipients.isEmpty()) {
			this.sender.sendMessage(this.message);
		}
		
		else {
			String listRecipients = "";
			
			for (CommandSender recipient : this.recipients) {
				recipient.sendMessage(this.message);
				listRecipients += recipient.getName() + ", ";
			}
			
			sender.sendMessage("Sent search results to " + 
					listRecipients.substring(0, listRecipients.length() - 2) + ".");
		}
	}
}
