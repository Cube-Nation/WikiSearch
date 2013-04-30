package com.remypas.wikisearch.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class SimpleUrlShortener implements UrlShortener {

	private final String apiUrl;
	
	public SimpleUrlShortener(final String apiUrl, 
			final Map<String, String> apiCredentials) {
		String apiUrlFormatted = apiUrl;
		
		if (apiCredentials != null) {
			for (String credentialName : apiCredentials.keySet()) {
				String credential = apiCredentials.get(credentialName);
				if (credential == null) {
					continue;
				}
				apiUrlFormatted = apiUrlFormatted.replace("%%" + credentialName + "%%", credential);
			}
		}
		
		this.apiUrl = apiUrlFormatted;
		
		String testAuthentication = this.shortenUrl("http://dev.bukkit.org/server-mods/wikisearch");
		if (testAuthentication == null) {
			throw new IllegalArgumentException("invalid API credentials");
		}
	}

	public String shortenUrl(final String longUrl) {
		return this.readUrl(this.apiUrl.replace("%%LONG_URL%%", longUrl));
	}
	
	private String readUrl(final String url) {
		try {
			String inputLine, output = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((inputLine = reader.readLine()) != null) {
				output += inputLine;
			}
			reader.close();
			return output;
		} 
		
		catch (IOException ex) {
			return null;
		}
	}
}
