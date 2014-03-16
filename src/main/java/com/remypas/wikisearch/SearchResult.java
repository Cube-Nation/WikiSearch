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
		    for (String messageLine : this.message.split("\n")) Bukkit.broadcastMessage(messageLine);
		}
		
		else if (this.recipients.isEmpty()) {
            for (String messageLine : this.message.split("\n")) this.sender.sendMessage(messageLine);
		}
		
		else {
			String listRecipients = "";
			
			for (CommandSender recipient : this.recipients) {
	            for (String messageLine : this.message.split("\n")) recipient.sendMessage(messageLine);
				listRecipients += recipient.getName() + ", ";
			}
			
			sender.sendMessage("Sent search results to " + 
					listRecipients.substring(0, listRecipients.length() - 2) + ".");
		}
	}
}
