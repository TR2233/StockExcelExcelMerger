import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("M/d/yyyy H:m:s");
    private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("M/d/yyyy H:m");

    public static void main(String[] args) {

        List<List<String>> dataToSort = new ArrayList<>();
        List<List<String>> sortedData = new ArrayList<>();
        System.out.println("Input File: ");
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
        System.out.println("--------");

        // the first arg will always be the path that we want to send the result to
        if (args.length > 0) {
            for (int i = 1; i < args.length; i++) {
                csvToArray(args[i], dataToSort);
            }
            sortAndRemoveDuplicates(dataToSort, sortedData);

            // Create the output file path and file name
            // the first argument of args must be the file we want the final output to go in!!!
            String ticker = sortedData.get(0).get(0);
            String startDate = getStartDate(sortedData);
            String endDate = getEndDate(sortedData);
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

        //sort the entire list
        dataToSort.sort((entry1, entry2) -> {
            LocalDateTime date1 = entry1.get(1).contains("T") ? LocalDateTime.parse(entry1.get(1))
                    : getUnusualParsedLocalDateTime(entry1.get(1));
            LocalDateTime date2 = entry2.get(1).contains("T") ? LocalDateTime.parse(entry2.get(1))
                    : getUnusualParsedLocalDateTime(entry2.get(1));
            return date1.compareTo(date2);
        });


        //get rid of duplicates
        for (int i = 0; i < dataToSort.size() - 1; i++) {
            LocalDateTime date1, date2;
            if (sortedData.isEmpty()) {
                sortedData.add(dataToSort.get(i));
            } else {
                date1 = dataToSort.get(i).get(1).contains("T") ? LocalDateTime.parse(dataToSort.get(i).get(1))
                        : getUnusualParsedLocalDateTime(dataToSort.get(i).get(1));
                date2 = dataToSort.get(i + 1).get(1).contains("T") ? LocalDateTime.parse(dataToSort.get(i + 1).get(1))
                        : getUnusualParsedLocalDateTime(dataToSort.get(i + 1).get(1));
                if (!date1.equals(date2)) {
                    sortedData.add(dataToSort.get(i));
                }
            }
        }
        sortedData.add(dataToSort.get(dataToSort.size() - 1)); // we need the last entry no matter what
    }

    private static LocalDateTime getUnusualParsedLocalDateTime(String date) {
        try {
            return LocalDateTime.parse(date, dateTimeFormatter1);
        } catch (Exception e) {
            return LocalDateTime.parse(date, dateTimeFormatter2);
        }
    }

    private static String getEndDate(List<List<String>> sortedData) {
        String stringDate = sortedData.get(sortedData.size() - 1).get(1);
        LocalDateTime localDateTime = stringDate.contains("T") ? LocalDateTime.parse(stringDate) : getUnusualParsedLocalDateTime(stringDate);
        return localDateTime.toLocalDate().toString().replace("-", "_");
    }

    private static String getStartDate(List<List<String>> sortedData) {
        String stringDate = sortedData.get(0).get(1);
        LocalDateTime localDateTime = stringDate.contains("T") ? LocalDateTime.parse(stringDate) : getUnusualParsedLocalDateTime(stringDate);
        return localDateTime.toLocalDate().toString().replace("-", "_");
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