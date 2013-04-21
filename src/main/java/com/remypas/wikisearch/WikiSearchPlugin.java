package com.remypas.wikisearch;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;

public class WikiSearchPlugin extends JavaPlugin {

	public void onEnable() {
		Logger log = this.getLogger();
		
		this.saveDefaultConfig();
		WikiSearchConfig config = new WikiSearchConfig(this.getConfig());
		
		boolean configInvalid = false;
		String bitlyUser = config.getBitlyUsername();
		String bitlyKey  = config.getBitlyApiKey();
		Provider urlShortener = null;
		
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
				urlShortener = Bitly.as(bitlyUser, bitlyKey);
			} catch (BitlyException e) {
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
