package com.cfscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//each object of that class is for one person
class SubGrabber {

    private final String CODEFORCES_URL = "http://codeforces.com";
    private final String SUBMISSION_URL = "/submissions/%s/page/%d";

    private final String handle;
    private List<Submission> submissions;

    public SubGrabber(String handle){
        this.handle = handle;
    }

    /**
     * this function is used to construct the submissions list or to update it
     *
     * @param numberOfSubmissions number of submissions to get or -1 to get all submissions
     * @return the list of submissions
     */
    public List<Submission> get(int numberOfSubmissions){

        if(submissions == null)
            submissions = new ArrayList<>();

        try {

            int pagesCount = getPagesCount(handle);
            for(int page = 1; page <= pagesCount; ++page) {

                String url = CODEFORCES_URL + String.format(SUBMISSION_URL, handle, page);
                Document doc = Jsoup.connect(url).get();
                Elements submissionTable = doc
                        .select("table[class=status-frame-datatable]")
                        .select("tbody")
                        .select("tr");

                boolean done = add(submissionTable, numberOfSubmissions);
                if(done) break;
            }

        } catch (IOException e) {
            System.err.println("Error while getting submissions for " + handle);
            System.err.println("Check your internet connection and try again");
            e.printStackTrace();
        }

        return submissions;
    }

    private int getPagesCount(String name) throws IOException {

        int maxCnt = 0;
        String url = CODEFORCES_URL + String.format(SUBMISSION_URL, name, 1);

        Document doc = Jsoup.connect(url).get();
        Elements pages = doc.select("div[class=pagination]").select("ul").select("li");

        for(Element e : pages)
            if(e.text().matches("^[0-9]+$"))
                maxCnt = Math.max(maxCnt, Integer.parseInt(e.text()));

        return maxCnt;
    }

    private boolean add(Elements submissionTable, int numberOfSubmissions) {

        for (int row = 1; row < submissionTable.size(); ++row) {
            Submission sub = construct(submissionTable.get(row).select("td"));

            if(!submissions.contains(sub))
                submissions.add(sub);

            //done getting submission
            if(submissions.size() == numberOfSubmissions) return true;
        }

        return false;
    }

    private Submission construct(Elements row) {

        return new Submission(
                row.get(0).text(),
                row.get(1).text(),
                row.get(2).text(),
                row.get(3).text(),
                CODEFORCES_URL + row.get(3).selectFirst("a").attr("href"),
                row.get(4).text(),
                row.get(5).text(),
                row.get(6).text(),
                row.get(7).text());
    }

    public List<Submission> get(){
        return this.submissions;
    }
}