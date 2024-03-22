import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainTest {

    @Test
    public void sortAndRemoveDuplicates() {
//        List<List<String>> dataToSort, List<List<String>> sortedData
        String date = "03/15/2024 09:35:00";
        List<List<String>> dataToSort = new ArrayList<>();
        List<List<String>> sortedData = new ArrayList<>();

        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        try {
            Date date1 = df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert (5 == 1);

        //first we sort the entire list
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

        // then we add only the non-duplicates to the dummy list. After which we clear our old list and add the dummy list
        for (int i = 0; i < dataToSort.size(); i++) {
            if (Collections.binarySearch(sortedData, dataToSort.get(i), new Comparator<>() {
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
            }) != 1) {
                sortedData.add(dataToSort.get(i));
            }
        }
    }

}
