package com.remypas.wikisearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.remypas.wikisearch.url.UrlShortener;

public class SearchQuery extends BukkitRunnable {
    
    private CommandSender sender;
    private List<CommandSender> recipients;
    private Plugin plugin;
    private UrlShortener urlShortener;
    private String resultsFormat, searchTerms, urlFormat, wikiName, noDescString;

    public SearchQuery(CommandSender sender, List<CommandSender> recipients, 
            Plugin plugin, UrlShortener urlShortener, 
            String resultsFormat, String noDescString, String searchTerms, 
            String urlFormat, String wikiName) {
        this.sender = sender;
        this.recipients = recipients;
        this.plugin = plugin;
        this.urlShortener = urlShortener;
        this.resultsFormat = resultsFormat;
        this.noDescString = noDescString;
        this.searchTerms = searchTerms;
        this.urlFormat = urlFormat;
        this.wikiName = wikiName;
    }
    
    public void run() {
        String descriptionText = this.noDescString;
        String shortUrl;
        String searchUrl = this.urlFormat.replaceAll("%%SEARCHTERMS%%", this.searchTerms);
        
 
        // try to be more clever on media wikis
        try {
            URL _searchUrl = new URL(searchUrl);
            String searchUrlBase = _searchUrl.getProtocol() + "://" + _searchUrl.getHost();
            
            // try to find the best match
            
            String matchAPI = searchUrlBase.concat("/api.php?action=query&list=search&srsearch=" + URLEncoder.encode(this.searchTerms, "ISO-8859-1") + "&srprop=titlesnippet&srlimit=1&format=json");
            String matchAPIResponse = readURL(matchAPI);

            String match = ".*\\\"title\\\":\\\"([^\"]+)\\\",\\\"titlesnippet\\\".*";
            if (matchAPIResponse.matches(match)) {
                String matchedTitle = matchAPIResponse.replaceAll(match, "$1");
                if (matchedTitle.length() > 0) this.searchTerms = matchedTitle;
            }


            
            // try to find and parse the description directly
            
            String descriptionAPI = searchUrlBase.concat("/api.php?action=query&titles=" + URLEncoder.encode(this.searchTerms, "ISO-8859-1") + "&prop=revisions&rvprop=content&format=txt");
            String descriptionAPIResponse = readURL(descriptionAPI);
            
            
            int descriptionIndex = descriptionAPIResponse.indexOf("[*] => ");
            if (descriptionIndex != -1) {
                descriptionAPIResponse = descriptionAPIResponse.substring(descriptionIndex + 7);

                while (descriptionAPIResponse.startsWith("{{")) {
                    if (descriptionAPIResponse.startsWith("{{")) {
                        int closeTag = descriptionAPIResponse.indexOf("}}");
                        int openTag = descriptionAPIResponse.substring(0, closeTag).lastIndexOf("{{");
                        descriptionAPIResponse = descriptionAPIResponse.substring(0, openTag) + descriptionAPIResponse.substring(closeTag + 2, descriptionAPIResponse.length()-1);
                        
                    }
                    
                    descriptionAPIResponse = descriptionAPIResponse.trim();
                    if (descriptionAPIResponse.startsWith("\n")) descriptionAPIResponse = descriptionAPIResponse.substring(2);
                }
                
                descriptionAPIResponse = descriptionAPIResponse
                        .replaceAll("\\[\\[[a-zA-Z0-9]*:[^\\]]*\\]\\]", "")
                        .replaceAll("\\[\\[[^\\]]*\\|([^\\|]*)\\]\\]", "$1")
                        .replaceAll("\\[\\[", "")
                        .replaceAll("\\]\\]", "")
                        .replaceAll("'''", "")
                        .replaceAll("<br>", " ")
                        .replaceAll("\\{\\{IS\\|", "\n"+ChatColor.AQUA+"[")
                        .replaceAll("\\}\\}", "]")
                        .replaceAll("===([^=]+)===", " ➜ $1\n"+ChatColor.AQUA)
                        .replaceAll("==([^=]+)==", "\n"+ChatColor.AQUA+" ➜ $1")
                        .replaceAll(" [^\\| ]+\\|([^\\| ]+) ", " $1 ")
                        ;
                
                descriptionText = "\n" + ChatColor.AQUA + (descriptionAPIResponse.length() > 700 ? (descriptionAPIResponse.substring(0,700) + "...") : descriptionAPIResponse) + "\n";                
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        String message = this.resultsFormat
                .replaceAll("%%SEARCHTERMS%%", this.searchTerms)
                .replaceAll("%%SEARCHVIA%%", this.wikiName).replaceAll("%%RESULTTEXT%%", descriptionText);
        

        
        try {
            shortUrl = this.urlShortener.shortenUrl(searchUrl);
            message = message.replaceAll("%%RESULTSURL%%", shortUrl);
        } catch (Exception e) {
            message = "Couldn't shorten search URL.";
            this.recipients = new ArrayList<CommandSender>();
        }
        
        new SearchResult(this.sender, this.recipients, message).runTask(this.plugin);
    }

    private String readURL(String descriptionAPI) {
        try {
            String urlResponse = "";
            
            URLConnection connection = new URL(descriptionAPI).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            System.out.println("UrlReq: " + connection.getURL());

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            String inputLine;
            while ((inputLine = br.readLine()) != null) urlResponse += inputLine;
            br.close();
            
            System.out.println("Response: " + urlResponse);

            return urlResponse;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
