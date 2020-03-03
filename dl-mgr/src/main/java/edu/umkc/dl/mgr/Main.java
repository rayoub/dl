package edu.umkc.dl.mgr;

import java.util.Map;
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

import edu.umkc.dl.gram.GramProbs;
import edu.umkc.dl.gram.ImportTargets;
import edu.umkc.dl.gram.PredictTargets;
import edu.umkc.dl.lib.Constants;
import edu.umkc.dl.lib.Descriptor;
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
        //ImportGrams.importGrams();
        ImportTargets.importTargets();
    }
    
    private static void option_s(CommandLine line) {

        SetFitSequences.set(FitSequenceType.PP);
        SetFitSequences.set(FitSequenceType.CI);
    }
    
    private static void option_d(CommandLine line) {

        Map<String, GramProbs> map = PredictTargets.getGramProbs();
       
        int total = 0;
        int diff = 0; 
        for(String key : map.keySet()) {

            GramProbs probs = map.get(key);

            String descrOrder = getSs(probs.getDescr(1)).toString() + getSs(probs.getDescr(2)) + getSs(probs.getDescr(3));
            String ssOrder = probs.getSs(1) + probs.getSs(2) + probs.getSs(3);
            
            total++;
            if (!descrOrder.equals(ssOrder)) {
                diff++;
           
                System.out.println(key + " 1 " + probs.getDescr(1) + " " + probs.getDescrProb(1));
                System.out.println(key + " 2 " + probs.getDescr(2) + " " + probs.getDescrProb(2));
                System.out.println(key + " 3 " + probs.getDescr(3) + " " + probs.getDescrProb(3));
                System.out.println(key + " 1 " + probs.getSs(1) + " " + probs.getSsProb(1));
                System.out.println(key + " 2 " + probs.getSs(2) + " " + probs.getSsProb(2));
                System.out.println(key + " 3 " + probs.getSs(3) + " " + probs.getSsProb(3));
                System.out.println(descrOrder);
                System.out.println(ssOrder);

                System.out.println("");
                System.out.println("------------------------------------------------------");
                System.out.println("");
            }
        }

        System.out.println(diff + "/" + total);
    }

    private static String getSs(int descr) {

        return Descriptor.toSs(descr);
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


