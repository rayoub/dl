package edu.umkc.dl.mgr;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.umkc.dl.gram.ImportGrams;
import edu.umkc.dl.gram.ImportPairs;
import edu.umkc.dl.gram.ImportTargets;
import edu.umkc.dl.gram.PredictTargets;
import edu.umkc.dl.lib.Constants;
import edu.umkc.dl.lib.FitSequenceType;
import edu.umkc.dl.lib.SetFitSequences;

public class Main {

    public static void main(String[] args) {

        Options options = new Options();
    
        OptionGroup group = new OptionGroup();

        group.addOption(Option.builder("i")
                .longOpt("import")
                .build());
        group.addOption(Option.builder("s")
                .longOpt("set")
                .build());
        group.addOption(Option.builder("d")
                .longOpt("debug")
                .build());
        group.addOption(Option.builder("?")
                .longOpt("help")
                .build());
        
        group.setRequired(true);
        options.addOptionGroup(group);
        
        CommandLine line;

        try {
            CommandLineParser parser = new DefaultParser();
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            option_help(options);
            return;
        }
        
        try {
            if (line.hasOption("i")) {
                option_i(line);
            } else if (line.hasOption("s")) {
                option_s(line);
            } else if (line.hasOption("d")) {
                option_d(line);
            } else if (line.hasOption("?")) {
                option_help(options);
            }
        }
        catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private static void option_i(CommandLine line) {

        //ImportAaSequences.importAaSequences();
        //ImportMaps.importMaps();
        //ImportStructures.importStructures();
        ImportGrams.importGrams();
        ImportTargets.importTargets();
        ImportPairs.importPairs();
    }
    
    private static void option_s(CommandLine line) {

        SetFitSequences.set(FitSequenceType.PP);
        SetFitSequences.set(FitSequenceType.CI);
    }
    
    private static void option_d(CommandLine line) {


        PredictTargets.predict();
        /*
        Map<Integer,PairProbs> map = Db.getPairProbs();
        for (Integer key : map.keySet()) {

            System.out.println(key + ":");
            PairProbs probs = map.get(key);
            for (int i = 0; i < 10; i++) {
                double prob = probs.getProbByDescr1(i);
                System.out.println(i + " = " + prob);
            }
        }
        */
        /*
        Map<String,DescrProbs> map = Db.getDescrProbs();
        for (String key : map.keySet()) {

            System.out.println(key + ":");
            DescrProbs probs = map.get(key);
            for (int i = 0; i < 10; i++) {
                double prob = probs.getProbByDescr(i);
                System.out.println(i + " = " + prob);
            }
        }
        */
        /*
        Map<Integer,GramProbs> map = Db.getGramProbs();
        for (int key : map.keySet()) {

            System.out.println(key + ":");
            GramProbs probs = map.get(key);
            double prob = probs.getProbByGram("ACA");
            System.out.println("ACA = " + prob);
        }
        */
    }

    private static void option_help(Options options) {

        HelpFormatter formatter = getHelpFormatter("Usage: ");
        formatter.printHelp(Constants.APP_NAME, options);
    }

    private static HelpFormatter getHelpFormatter(String headerPrefix){

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new OptionComparator());
        formatter.setSyntaxPrefix(headerPrefix);
        formatter.setWidth(140);
        formatter.setLeftPadding(5);
        return formatter;
    }
}


