package com.remypas.wikisearch;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;

public class SearchQuery implements Runnable {
	
	private CommandSender sender, recipient;
	private Provider urlShortener;
	private String knowledgebaseName, msgFormat, searchTerms, shortUrlFormat;

	public SearchQuery(CommandSender sender, CommandSender recipient, Provider urlShortener, 
			String knowledgebaseName, String msgFormat, String searchTerms, String shortUrlFormat) {
		this.sender = sender;
		this.recipient = recipient;
		this.urlShortener = urlShortener;
		this.knowledgebaseName = knowledgebaseName;
		this.msgFormat = msgFormat;
		this.searchTerms = searchTerms;
		this.shortUrlFormat = shortUrlFormat;
	}
	
	public void run() {
		String searchUrl = this.shortUrlFormat.replaceAll("%%SEARCHTERMS%%", searchTerms);
		String shortUrl;
		
		try {
			shortUrl = this.urlShortener.call(Bitly.shorten(searchUrl)).getShortUrl();
		} catch (BitlyException e) {
			sender.sendMessage("Couldn't shorten search URL");
			return;
		}
		
		String message = this.msgFormat;
		message = message.replaceAll("%%KNOWLEDGEBASE%%", knowledgebaseName);
		message = message.replaceAll("%%RESULTSURL%%", shortUrl);
		message = message.replaceAll("%%SEARCHTERMS%%", searchTerms);
		
		if (recipient == null) { // send to global chat
			if (sender.hasPermission("wikisearch.sendsearchresultsto.globalchat")) {
				Bukkit.broadcastMessage(message);
			}
			
			else {
				sender.sendMessage("You're not allowed to broadcast search results globally.");
			}
		}
		
		else if (recipient.equals(sender)) { // send to self
			recipient.sendMessage(message);
		}
			
		else { // send to another player
			if (sender.hasPermission("wikisearch.sendsearchresultsto.otherplayer")) {
				sender.sendMessage("Sent search results to " + recipient.getName() + ".");
				recipient.sendMessage(message);
			} 
			
			else {
				sender.sendMessage("You're not allowed to send search results to a specific player.");
			}
		}
	}
}
