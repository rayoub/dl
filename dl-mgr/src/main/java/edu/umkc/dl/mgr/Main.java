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

import edu.umkc.dl.lib.Constants;
import edu.umkc.dl.lib.ImportAaSequences;
import edu.umkc.dl.lib.SetAaTypes;

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

        ImportAaSequences.importAaSequences();
        //ImportMaps.importMaps();
        //ImportStructures.importStructures();
    }
    
    private static void option_s(CommandLine line) {

        //SetSsSequences.set();
        SetAaTypes.set();
    }
    
    private static void option_d(CommandLine line) {
    
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


