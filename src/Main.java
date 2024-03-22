
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<List<String>> dataToSort = new ArrayList<>();
        List<List<String>> sortedData = new ArrayList<>();
        System.out.println("Input File: ");
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
        System.out.println("--------");

        //FOR TESTING PURPOSES
        args = new String[3];
        args[0] = "C:\\Users\\treim\\IdeaProjects\\DayTrading\\Historical_Data_Formatting\\StockExcelExcelMergerTest";
        args[1] = "C:\\Users\\treim\\IdeaProjects\\DayTrading\\Historical_Data_Formatting\\StockExcelExcelMergerTest\\organized_EMINI_3_23_2009_3_15_2024.csv";
        args[2] = "C:\\Users\\treim\\IdeaProjects\\DayTrading\\Historical_Data_Formatting\\StockExcelExcelMergerTest\\ES.csv";
        //~~~~

        // the first arg will always be the path that we want to send the result to
        if (args.length > 0) {
            for (int i = 1; i < args.length; i++) {
                csvToArray(args[i], dataToSort);
            }
            sortAndRemoveDuplicates(dataToSort, sortedData);

            // Create the output file path and file name
            // the first argument of args must be the file we want the final output to go in!!!
            String ticker = sortedData.get(0).get(0);
            String startDate = sortedData.get(0).get(1).split(" ")[0].replace("/", "_");
            String endDate = sortedData.get(sortedData.size() - 1).get(1).split(" ")[0].replace("/", "_");
            StringJoiner stringJoiner = new StringJoiner("_");
            stringJoiner.add(ticker).add(startDate).add(endDate);
            arrayToCSV(args[0] + "\\" + stringJoiner + ".csv", sortedData);
        } else {
            System.out.print("No input files were entered to parse");
        }
    }

    private static void arrayToCSV(String path, List<List<String>> parseData) {
        // first create file object for file placed at location
        // specified by filepath
        String[] header = {"Symbol", "DateTime", "Open", "High", "Low", "Close", "PrevClose", "Volume"};
        // create FileWriter object with file as parameter
        // create CSVWriter object filewriter object as parameter
        try (FileWriter outputFile = new FileWriter(path)
             ; CSVWriter writer = new CSVWriter(outputFile)) {

            writer.writeNext(header); // input header
            for (List<String> parseDatum : parseData) {
                writer.writeNext(parseDatum.toArray(String[]::new));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void sortAndRemoveDuplicates(List<List<String>> dataToSort, List<List<String>> sortedData) {
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm"); //we will never need seconds, ss

        //sort the entire list
        dataToSort.sort(new Comparator<>() {
            Date date1, date2;
            @Override
            public int compare(List<String> entry1, List<String> entry2) {
                try {
                    date1 = df.parse(entry1.get(1));
                    date2 = df.parse(entry2.get(1));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return date1.compareTo(date2);
            }
        });

        //get rid of duplicates
        for (int i = 0; i < dataToSort.size()-1; i++) {
            Date date1, date2;
            if (sortedData.isEmpty()) {
                sortedData.add(dataToSort.get(i));
            } else {
                try {
                    date1 = df.parse(dataToSort.get(i).get(1));
                    date2 = df.parse(dataToSort.get(i+1).get(1));
                    if (date1.compareTo(date2) != 0) {
                        sortedData.add(dataToSort.get(i));
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        sortedData.add(dataToSort.get(dataToSort.size() - 1)); // we need the last entry no matter what
    }

    private static void csvToArray(String path, List<List<String>> filedata) {
        try (Scanner scanner = new Scanner(new File(path))) {

            scanner.nextLine();//tosses the header
            while (scanner.hasNextLine()) {
                filedata.add(Arrays.stream(scanner.
                                nextLine()
                                .replace("\"", "")
                                .split(","))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
            }
        } catch (Exception e) {
            System.out.printf("Path Throwing Error: %s", path);
            e.printStackTrace();
        }
    }
}