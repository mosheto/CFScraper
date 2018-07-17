package com.cfscraper;

import java.util.Objects;

class Submission {

    public final String srcNumber, when, who, problemName, problemUrl, lang, verdict, time, memory;

    public Submission(String srcNumber,   String when,       String who,
                      String problemName, String problemUrl, String lang,
                      String verdict,     String time,       String memory) {

        this.srcNumber = srcNumber;
        this.when = when;
        this.who = who;
        this.problemName = problemName;
        this.problemUrl = problemUrl;
        this.lang = lang;
        this.verdict = verdict;
        this.time = time;
        this.memory = memory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Submission that = (Submission) o;
        return Objects.equals(srcNumber, that.srcNumber) &&
                Objects.equals(when, that.when);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcNumber, when);
    }
}
