package com.cfscraper;

import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Codes {
    private static final String CODEFORCES_URL="http://codeforces.com/%s/%s/submission/%s"; //type {contest,gym} , contest/gym number , srcNumber

    public static void Get(String problemUrl,String name,String srcNumber) throws IOException, InterruptedException {

        String type,num;
        type=problemUrl.substring(problemUrl.indexOf(".com/")+5,25);

        if(!type.equals("gym")) {//Just print contests codes "gyms codes need user permission to access them"
            type = "contest";
            num = problemUrl.substring(problemUrl.indexOf(type) + 8, problemUrl.indexOf("/problem/"));
            String url=String.format(CODEFORCES_URL,type,num,srcNumber);
            Document doc = Jsoup.connect(url).get();
            Elements code = doc.select("pre[id=program-source-text]");
            Thread.sleep(1000);
            BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(name+".cpp"));
            out.write(code.text().getBytes());
            out.close();
        }
    }
}

