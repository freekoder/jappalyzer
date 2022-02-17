package com.vampbear.jappalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public HttpClient() {}

    public String getUrlContent(String url) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41");
            conn.setInstanceFollowRedirects(true);
            return readInputStream(conn);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String readInputStream(HttpURLConnection conn) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } finally {
            if (in != null) try { in.close(); } catch (IOException ignored) {}
        }
    }

    private HttpURLConnection getHttpURLConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

}
