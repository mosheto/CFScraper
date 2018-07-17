package com.cfscraper;

import java.io.IOException;

import java.util.List;
import java.util.Scanner;

/*
 * this class is for Command Line Interface
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        //System.out.println(args.length);

        /*Scanner in=new Scanner(System.in);
        System.out.print("Enter Valid User Handle : ");
        String name=in.nextLine();*/

        SubGrabber grabber = new SubGrabber(args[0]);
        List<Submission> submissions = grabber.get(25);

        //System.out.println(submissions.size());

        Submission[] subs = submissions.stream()
                .filter((s)-> s.verdict.contains("Ac")) //get AC submissions only
                .toArray(Submission[]::new);


        for(Submission sub : subs)
            Codes.Get(sub.problemUrl,sub.problemName,sub.srcNumber);

        /*
        AsciiTable table = new AsciiTable();
        table.addRule();
        for(Submission sub : submissions) {
            table.addRow(sub.srcNumber, sub.when, sub.who, sub.problemName, sub.lang, sub.verdict).setTextAlignment(TextAlignment.CENTER);
            table.addRule();
        }
        */

    }
}


