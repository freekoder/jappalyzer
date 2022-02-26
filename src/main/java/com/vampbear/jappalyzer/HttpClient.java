package com.vampbear.jappalyzer;

import org.json.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpClient {

    public HttpClient() {}

    public PageResponse getPageByUrl(String url) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41");
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            Map<String, List<String>> headers = conn.getHeaderFields();
            String content = readInputStream(conn);
            PageResponse pageResponse = new PageResponse(status, headers, content);
            List<String> setCookiesHeaders = headers.get("Set-Cookie");
            if (setCookiesHeaders != null) {
                for (String cookieValue : setCookiesHeaders) {
                    List<HttpCookie> cookies = HttpCookie.parse(cookieValue);
                    for (HttpCookie cookie : cookies) {
                        pageResponse.addCookie(cookie.getName(), cookie.getValue());
                    }
                }
            }

            List<String> cookiesHeaders = headers.get("Cookie");
            if (cookiesHeaders != null) {
                for (String cookieValue : cookiesHeaders) {
                    List<HttpCookie> cookies = HttpCookie.parse(cookieValue);
                    for (HttpCookie cookie : cookies) {
                        pageResponse.addCookie(cookie.getName(), cookie.getValue());
                    }
                }
            }

            return pageResponse;
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
