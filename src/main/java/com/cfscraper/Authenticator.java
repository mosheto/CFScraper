package com.cfscraper;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Authenticator {

    private boolean isAuthenticated;
    private List<String> cookies;
    private HttpsURLConnection conn;

    private final String LOGIN_URL = "https://codeforces.com/enter";

    private final String USER_AGENT = "Mozilla/5.0";

    /**
     * function to test the class
     *
     * @param args not used
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        Authenticator auth = new Authenticator();

        if(auth.authenticate("handle", "passwd")) {

            String result = auth.GetPageContent("https://codeforces.com/gym/101798/submission/39701627");
            System.out.println(result);
            Elements code = Jsoup.parse(result).select("pre[id=program-source-text]");
            System.out.println(code.text());
        }
    }

    public boolean authenticate(String handle, String password) {

        try {

            // make sure cookies is turn on
            CookieHandler.setDefault(new CookieManager());

            // 1. Send a "GET" request, so that you can extract the form's data.
            String loginPage = GetPageContent(LOGIN_URL);
            String formParams = getFormParams(loginPage, handle, password, "on");

            // 2. Construct above post's content and then send a POST request for
            // authentication
            String authResponse = sendPost(formParams);

            return isAuthenticated = !authResponse.contains("Invalid handle/email or password");

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private String sendPost(String postParams) throws IOException {

        URL obj = new URL(LOGIN_URL);
        conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "codeforces.com");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,fr;q=0.8,ar;q=0.7");
        for (String cookie : this.cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "https://codeforces.com/enter?back=%2F");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + LOGIN_URL);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();
        return response.toString();
    }

    public String GetPageContent(String url) throws IOException {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,fr;q=0.8,ar;q=0.7");

        if (cookies != null)
            for (String cookie : this.cookies)
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        // Get the response cookies
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();

    }

    private String getFormParams(String html, String username, String password, String remember)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        Element loginform = doc.getElementById("enterForm");
        Elements inputElements = loginform.getElementsByTag("input");

        List<String> paramList = new ArrayList<>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            switch (key) {
                case "handleOrEmail":
                    value = username;
                    break;
                case "password":
                    value = password;
                    break;
                case "remember":
                    value = remember;
                    break;
            }
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&").append(param);
            }
        }
        return result.toString();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    private List<String> getCookies() {
        return cookies;
    }

    private void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

}