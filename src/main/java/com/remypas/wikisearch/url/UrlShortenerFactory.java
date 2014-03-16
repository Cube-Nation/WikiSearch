package com.remypas.wikisearch.url;

import java.util.Map;

public class UrlShortenerFactory {
    
    private static final String 
            bitlyApiUrl = "http://api.bit.ly/v3/shorten?" +
                    "login=%%USER%%&apiKey=%%API_KEY%%&longUrl=%%LONG_URL%%&format=txt",
            isgdApiUrl = "http://is.gd/create.php?format=simple&url=%%LONG_URL%%";

    public static UrlShortener createUrlShortener(String serviceName, 
            final Map<String, String> accountCredentials) {
        if (serviceName == null) {
            throw new IllegalArgumentException("service name cannot be null");
        }
        
        String serviceNameClean = serviceName.toLowerCase().replace(".", "");
        
        if (serviceNameClean.equals("isgd")) {
            return new SimpleUrlShortener(isgdApiUrl, accountCredentials);
        }
        
        else if (serviceNameClean.equals("bitly")) {
            return new SimpleUrlShortener(bitlyApiUrl, accountCredentials);
        }
        
        else {
            throw new IllegalArgumentException("no service called " + serviceNameClean);
        }
    }
}
