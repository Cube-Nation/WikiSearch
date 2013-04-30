package com.remypas.wikisearch;

import java.util.HashMap;
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
		String bitlyUser = config.getBitlyUsername();
		String bitlyKey  = config.getBitlyApiKey();
		UrlShortener urlShortener = null;
		
		if (bitlyUser == null) {
			log.warning("Must specify bit.ly username in config.yml");
			configInvalid = true;
		}
		
		else if (bitlyKey == null) {
			log.warning("Must specify bit.ly API key in config.yml");
			configInvalid = true;
		}
		
		else {
			try {
				Map<String, String> accountCredentials = new HashMap<String, String>();
				accountCredentials.put("USER", bitlyUser);
				accountCredentials.put("API_KEY", bitlyKey);
				urlShortener = UrlShortenerFactory.createUrlShortener("bitly", accountCredentials);
			} catch (Exception e) {
				log.warning("Invalid bit.ly username/API key pair");
				configInvalid = true;
			}
		}
		
		if (configInvalid) {
			log.warning("Disabling " + this.getName() + " v" + this.getDescription().getVersion());
			this.setEnabled(false);
		}
		
		this.getCommand("wikisearch").setExecutor(new SearchCommandExecutor(urlShortener, config, this));
	}
}
