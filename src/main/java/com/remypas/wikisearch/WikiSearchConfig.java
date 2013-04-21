package com.remypas.wikisearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class WikiSearchConfig {
	
	private Map<String, String> wikis;
	private String bitlyKey, bitlyUser, resultsFormat;
	private final static String MEDIAWIKI_URL_APPEND = "?search=%%SEARCHTERMS%%&go=Go";
	
	public WikiSearchConfig(FileConfiguration configFile) {
		this.wikis = new HashMap<String, String>();
		
		for (String wikiName : configFile.getConfigurationSection("wikis").getKeys(false)) {
			String wikiNameLowcase = wikiName.toLowerCase();
			String url = configFile.getString("wikis." + wikiName);
			
			if (url == null) {
				continue;
			}
			
			else if (url.startsWith("http://") || url.startsWith("https://")) {
				if (url.contains("%%SEARCHTERMS%%")) {
					this.wikis.put(wikiNameLowcase, url);
				}
				
				else {
					this.wikis.put(wikiNameLowcase, url + WikiSearchConfig.MEDIAWIKI_URL_APPEND);
				}
			}
			
			else {
				url = configFile.getString("wikis." + url);
				
				if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
					continue;
				}
				
				else {
					if (url.contains("%%SEARCHTERMS%%")) {
						this.wikis.put(wikiNameLowcase, url);
					}
					
					else {
						this.wikis.put(wikiNameLowcase, url + WikiSearchConfig.MEDIAWIKI_URL_APPEND);
					}
				}
			}
		}
		
		this.bitlyKey  = configFile.getString("bitly-api-key");
		this.bitlyUser = configFile.getString("bitly-username");
		
		this.resultsFormat = ChatColor.translateAlternateColorCodes('&', configFile.getString("results-format"));
	}
	
	public String getUrlFormat(String wikiName) {
		return this.wikis.get(wikiName);
	}
	
	public String getBitlyApiKey() {
		return this.bitlyKey;
	}
	
	public String getBitlyUsername() {
		return this.bitlyUser;
	}
	
	public String listWikis() {
		String list = "Available wikis: ";
		Set<String> wikiNames = this.wikis.keySet();
		
		if (wikiNames.isEmpty()) {
			return "(No wikis are available right now.)";
		}
		
		for (String wikiName : this.wikis.keySet()) {
			list += wikiName + ", ";
		}
		
		return list.substring(0, list.length() - 2);
	}
	
	public String getResultsFormat() {
		return this.resultsFormat;
	}
}
