package com.remypas.wikisearch;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.remypas.wikisearch.url.UrlShortener;
import com.remypas.wikisearch.url.UrlShortenerFactory;

public class WikiSearchPlugin extends JavaPlugin {

	public void onEnable() {
		Logger log = this.getLogger();
		
		this.saveDefaultConfig();
		WikiSearchConfig config = new WikiSearchConfig(this.getConfig());
		boolean configInvalid = false;
		
		String serviceName = config.getUrlShortenerName();
		Map<String, String> apiCredentials = config.getUrlShortenerApiCredentials();
		UrlShortener urlShortener = null;
		
		try {
			urlShortener = UrlShortenerFactory.createUrlShortener(serviceName, apiCredentials);
		} catch (Exception e) {
			log.warning("Invalid " + serviceName + " API credentials");
			configInvalid = true;
		}
		
		if (configInvalid) {
			log.warning("Disabling " + this.getName() + " v" + this.getDescription().getVersion());
			this.setEnabled(false);
		}
		
		this.getCommand("wikisearch").setExecutor(new SearchCommandExecutor(urlShortener, config, this));
	}
}
