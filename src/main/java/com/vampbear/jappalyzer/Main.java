package com.vampbear.jappalyzer;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final Options options = new Options();
        CommandLineParser argsParser = new DefaultParser();
        try {
            CommandLine cmd = argsParser.parse(options, args);
            List<String> argList = cmd.getArgList();
            for (String arg : argList) {
                processLink(arg);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void processLink(String url) {
        try {
            Jappalyzer jappalyzer = Jappalyzer.create();
            List<Technology> instanceTechnologies = jappalyzer.getTechnologies();
            System.out.println("Instance techs: " + instanceTechnologies.size());
            List<TechnologyMatch> foundTechs = jappalyzer.fromUrl(url);
            foundTechs.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
