package com.remypas.wikisearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class WikiSearchConfig {
    
    private Map<String, String> urlShortenerApiCredentials, wikis;
    private String resultsFormat, urlShortenerName, noDescriptionText;
    private final static String mediawikiUrlAppend = "?search=%%SEARCHTERMS%%&go=Go";
    
    public WikiSearchConfig(FileConfiguration configFile) {
        this.resultsFormat = ChatColor.translateAlternateColorCodes('&', configFile.getString("results-format"));
        this.urlShortenerName = configFile.getString("url-shortener");
        this.noDescriptionText = configFile.getString("noDescriptionText", "No description found");
        
        this.urlShortenerApiCredentials = new HashMap<String, String>();
        this.urlShortenerApiCredentials.put("API_KEY", configFile.getString(this.urlShortenerName + "-api-key"));
        this.urlShortenerApiCredentials.put("USER", configFile.getString(this.urlShortenerName + "-username"));
        
        this.wikis = new HashMap<String, String>();
        for (String wikiName : configFile.getConfigurationSection("wikis").getKeys(false)) {
            String url = configFile.getString("wikis." + wikiName);
            this.wikis.put(wikiName.toLowerCase(), url);
        }
    }
    
    public String getNoDescriptionText() {
        return this.noDescriptionText;
    }
    
    public String getResultsFormat() {
        return this.resultsFormat;
    }
    
    public String getUrlFormat(String wikiName) {
        String url = this.wikis.get(wikiName);
        
        if (url == null) {
            return null;
        }
        
        else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return this.getUrlFormat(url);
        }
        
        else {
            if (url.contains("%%SEARCHTERMS%%")) {
                return url;
            }
            
            else {
                return url + mediawikiUrlAppend;
            }
        }
    }
    
    public Map<String, String> getUrlShortenerApiCredentials() {
        return this.urlShortenerApiCredentials;
    }
    
    public String getUrlShortenerName() {
        return this.urlShortenerName;
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
}
