package com.remypas.wikisearch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rosaloves.bitlyj.Bitly.Provider;

public class SearchCommandExecutor implements CommandExecutor {
	
	private Provider urlShortener;
	private WikiSearchConfig config;
	private WikiSearchPlugin plugin;
	
	public SearchCommandExecutor(Provider urlShortener, WikiSearchConfig config, WikiSearchPlugin plugin) {
		this.urlShortener = urlShortener;
		this.config = config;
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
		List<CommandSender> recipients = new ArrayList<CommandSender>();
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
				if (!sender.hasPermission("wikisearch.sendresultsto.globalchat")) {
					sender.sendMessage("You're not allowed to globally broadcast search results.");
					return true;
				}
				
				recipients = null;
			}
			
			else if (arg.startsWith("-to:")) {
				if (!sender.hasPermission("wikisearch.sendresultsto.otherplayer")) {
					sender.sendMessage("You're not allowed to send search results to other players.");
					return true;
				}
				
				String playerName = arg.replaceFirst("-to:", "");
				Player player = Bukkit.getPlayer(playerName);
				
				if (player == null) {
					sender.sendMessage("No online player matching \"" + playerName + "\" could be found.");
					return true;
				}
				
				else if (recipients == null) {
					sender.sendMessage("The -bro (broadcast) flag may not be used alongside -to:<player> flags.");
					return true;
				}
				
				else {
					recipients.add(player);
				}
			}
			
			else if (arg.startsWith("-via:")) {
				String wikiNameSpecified = arg.replaceFirst("-via:", "");
				
				if (this.config.getUrlFormat(wikiNameSpecified) == null) {
					sender.sendMessage("There's no available wiki called \"" + wikiNameSpecified + "\".");
					sender.sendMessage(config.listWikis());
					return true;
				} 
				
				else if (!(sender.hasPermission("wikisearch.searchvia.*") 
						|| sender.hasPermission("wikisearch.searchvia." + wikiNameSpecified))) {
					sender.sendMessage("You're not allowed to search via \"" + wikiNameSpecified + "\".");
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
			sender.sendMessage("Running your search query...");
			
			SearchQuery query = new SearchQuery(sender, recipients,
					this.plugin, this.urlShortener,
					this.config.getResultsFormat(), searchTerms.trim(),
					this.config.getUrlFormat(wikiName), wikiName);
			
			query.runTaskAsynchronously(this.plugin);
		}
				
		return true;
	}
}
