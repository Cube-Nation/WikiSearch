package com.remypas.wikisearch;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.rosaloves.bitlyj.Bitly.Provider;

public class SearchCommandExecutor implements CommandExecutor {
	
	private BukkitScheduler scheduler;
	private Provider urlShortener;
	private WikiSearchConfig config;
	private WikiSearchPlugin plugin;
	
	public SearchCommandExecutor(Provider urlShortener, WikiSearchConfig config, WikiSearchPlugin plugin) {
		this.scheduler = Bukkit.getScheduler();
		this.urlShortener = urlShortener;
		this.config = config;
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
		CommandSender recipient = sender;
		String wikiName = "default";
		String searchTerms = "";

		for (String arg : args) {
			arg = arg.toLowerCase();
			
			if (arg.equals("?") || arg.equals("help")) {
				sender.sendMessage(cmd.getDescription());
				sender.sendMessage(cmd.getUsage().replaceFirst("<command>", cmdName));
				sender.sendMessage(config.listWikis());
				return true;
			}
			
			else if (arg.equals("-bro")) {
				recipient = null;
			}
			
			else if (arg.startsWith("-to:")) {
				String playerName = arg.replaceFirst("-to:", "");
				Player player = Bukkit.getPlayer(playerName);
				
				if (player == null) {
					sender.sendMessage("No online player matching \"" + playerName + "\" could be found.");
					return true;
				} 
				
				else {
					recipient = player;
				}
			}
			
			else if (arg.startsWith("-via:")) {
				String wikiNameSpecified = arg.replaceFirst("-via:", "");
				
				if (this.config.getUrlFormat(wikiNameSpecified) == null) {
					sender.sendMessage("There's no available wiki called \"" + wikiNameSpecified + "\".");
					sender.sendMessage(config.listWikis());
					return true;
				} 
				
				else if (!(sender.hasPermission("wikisearch.searchvia.*") || sender.hasPermission("wikisearch.searchvia." + wikiNameSpecified))) {
					sender.sendMessage("You don't have permission to search via \"" + wikiNameSpecified + "\".");
					return true;
				}
				
				else {
					wikiName = wikiNameSpecified;
				}
			}
			
			else {
				searchTerms += arg + " ";
			}
		}
		
		if (searchTerms.isEmpty()) {
			sender.sendMessage("Must specify one or more search terms.");
		}
		
		else {
			this.scheduler.runTask(this.plugin, new SearchQuery(sender, recipient, this.urlShortener, 
					wikiName, this.config.getResultsFormat(), searchTerms.trim(), this.config.getUrlFormat(wikiName)));
		}
				
		return true;
	}
}
