package com.remypas.wikisearch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;

public class SearchQuery extends BukkitRunnable {
	
	private CommandSender sender;
	private List<CommandSender> recipients;
	private Plugin plugin;
	private Provider urlShortener;
	private String messageFormat, searchTerms, urlFormat, wikiName;

	public SearchQuery(CommandSender sender, List<CommandSender> recipients, 
			Plugin plugin, Provider urlShortener, 
			String messageFormat, String searchTerms, 
			String urlFormat, String wikiName) {
		this.sender = sender;
		this.recipients = recipients;
		this.plugin = plugin;
		this.urlShortener = urlShortener;
		this.messageFormat = messageFormat;
		this.searchTerms = searchTerms;
		this.urlFormat = urlFormat;
		this.wikiName = wikiName;
	}
	
	public void run() {
		String message = this.messageFormat
				.replaceAll("%%SEARCHTERMS%%", this.searchTerms)
				.replaceAll("%%SEARCHVIA%%", this.wikiName);
		
		String searchUrl = this.urlFormat.replaceAll("%%SEARCHTERMS%%", this.searchTerms);
		String shortUrl;
		
		try {
			shortUrl = this.urlShortener.call(Bitly.shorten(searchUrl)).getShortUrl();
			message = message.replaceAll("%%RESULTSURL%%", shortUrl);
		} catch (BitlyException e) {
			message = "Couldn't shorten search URL.";
			this.recipients = new ArrayList<CommandSender>();
		}
		
		new SearchResult(this.sender, this.recipients, message).runTask(this.plugin);
	}
}
