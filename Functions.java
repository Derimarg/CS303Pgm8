import java.io.*;
import java.util.*;

public class Functions {

    // PRE: TVList is initialized, and tv.csv exists in the directory
    // POST: Map is populated with duration as the key and an ArrayList of titles as the key value.
    public static void loadData(MyMap<Integer, MyArrayList<String>> TVList) {
        String fileName = "tv.csv";

        // read the file & load the map
        try {
            Scanner inFile = new Scanner(new File(fileName));

            while (inFile.hasNextLine()) {
                String inputRecord = inFile.nextLine().trim();

                if (inputRecord.isEmpty())
                    continue;

                try {
                    // Split data into max 2 parts, so titles with a comma aren't split
                    String[] row = inputRecord.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (row.length < 2)
                        continue;

                    int key = Integer.parseInt(row[0].trim());
                    String showTitle = row[1].trim().replaceAll("^\"|\"$", "");

                    // add to map
                    MyArrayList<String> shows = TVList.get(key);
                    if (shows == null) {
                      shows = new MyArrayList<>();
                      shows.add(showTitle);
                      TVList.put(key, shows);
                    } else {
                      // Check for duplicates
                      if (!shows.contains(showTitle)) {
                        shows.add(showTitle);
                      }
                    }

                } catch (Exception e) {
                  // Ignore error
                }
            }
            inFile.close();
        } catch (Exception e) {
            System.out.println("Error in input record: " + fileName);
        }
    }

    // PRE: The scanner is open and ready to recive input.
    // POST: Returns a validated, single character string as the menu command to be selected
    public static String getMenuItem(Scanner input) {
        String choice = " ";
        System.out.println("\nACTIONS FOR TVSHOW MAP");
        System.out.println("A: Add a Show ");
        System.out.println("D: Delete a Show ");
        System.out.println("K: Print All Keys (Durations) to Report");
        System.out.println("P: Print Map Listing to Report ");
        System.out.println("S: Print Specific Key (Duration) Listing to Report ");
        System.out.println("Q: Quit ");
        System.out.print("Please enter your choice: ");
        choice = input.nextLine().toUpperCase().trim();

        while (!(choice.equals("A") || choice.equals("D") ||
                choice.equals("K") || choice.equals("P") ||
                choice.equals("S") || choice.equals("Q"))) {
            System.out.print("You entered an invalid value. Please enter a valid choice: ");
            choice = input.nextLine().toUpperCase().trim();
        }
        System.out.println();

        return choice;
    }

    // PRE: TVList, input, and report are ready to be use.
    // POST: Promps user for duration and name, updates TVList map, and writes 
    // confirmation message to report file and console.
    public static void addShow(MyMap<Integer, MyArrayList<String>> TVList, Scanner input, PrintWriter out) {
        int duration = -1;
        boolean valid = false;

        System.out.print("Enter duration (seasons/years): ");
        while (!valid) {
            String rawInp = input.nextLine().trim();

            try {
                duration = Integer.parseInt(rawInp);
                if (duration <= 0) {
                    System.out.print("Please enter a positive integer for duration: ");
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter an integer duration: ");
            }
        }

        System.out.print("Enter the name of the show: ");
        String showName = input.nextLine().trim();

        MyArrayList<String> shows = TVList.get(duration);
        String msg = String.format("A: This new show was added to the map: %s with the duration of %d years.", showName, duration);
        if (shows == null) {
          shows = new MyArrayList<>();
          shows.add(showName);
          TVList.put(duration, shows);
          
          System.out.println(msg);
          out.println(msg);
          out.flush();
        } else {
          // Check if show already exists for that duration
          if (shows.contains(showName)) {
            System.out.printf("The show %s already exists for the duration: %d", showName, duration);
          } else {
            shows.add(showName);
            
            System.out.println(msg);
            out.println(msg);
            out.flush();
          }
        }

    }

    // PRE: TVList, input, and report are ready to be use.
    // POST: Promps user for show name, removes entry if found and deletes the key, if the value becomes empty.
    // Print status message to report file and console.
    public static void deleteShow(MyMap<Integer, MyArrayList<String>> TVList, Scanner input, PrintWriter out) {
        System.out.print("Enter the name of the show to delete: ");
        String showName = input.nextLine().trim();

        int key = findKeyByShowTitle(TVList, showName);

        // remove show if key was found
        if (key != -1) {
            MyArrayList<String> shows = TVList.get(key);

            // delete show
            for (int i = shows.size() - 1; i >= 0; i--) {
                if (shows.get(i).equalsIgnoreCase(showName)) {
                    shows.remove(i);
                }
            }

            // delete key if value becomes an empty list
            if (shows.isEmpty())
                TVList.remove(key);

            String msg = String.format("R: Deleted item %s from the map", showName);
            System.out.println(msg);
            out.println(msg);
            out.flush();
        } else {
            String msg = String.format("R: Unable to delete %s. Item is not in the map", showName);
            System.out.println(msg);
            out.println(msg);
            out.flush();
        }
    }

    // PRE: TVList is initialized, out writer to report file.
    // POST: Prints all key durations to report file and prints confirmation.
     public static void printKeys(MyMap<Integer, MyArrayList<String>> TVList, PrintWriter out) {
        int keysPerRow = 10;
        
        MyArrayList<String> keyStrings = new MyArrayList<>();
        for (Integer k : TVList.keySet()) {
            keyStrings.add(String.valueOf(k));
        }

        MyMap<Integer, MyArrayList<String>> tempMap = new MyHashMap<>();
        tempMap.put(0, keyStrings);

        Table table = new Table(tempMap, keysPerRow, true);
        table.printGrid("K", false, out);
        System.out.println("Key listing was printed to report file");
    }

    // PRE: TVList is initialized, out writer to report file.
    // POST: Prints all keys and values to report file and prints confirmation.
    public static void printMap(MyMap<Integer, MyArrayList<String>> TVList, PrintWriter out) {
        int columnsPerRow = 8; // number of columns per key
        Table table = new Table(TVList, columnsPerRow);
        table.printGrid("P", true, out);
        System.out.println("Map listing was printed to report file");
    }

    // PRE: TVList, input and out report are initialized
    // POST: Prompts user for a duration key and writes values to report file.
    public static void printSpecificKey(MyMap<Integer, MyArrayList<String>> TVList, Scanner input, PrintWriter out) {
        System.out.print("Enter duration to list: ");

        if (input.hasNextInt()) {
            int searchKey = input.nextInt();
            input.nextLine();

            MyArrayList<String> titles = TVList.get(searchKey);

            if (titles != null && !titles.isEmpty()) {
                int threshold = 15;

                // Simple vertical list of 15 row max
                if (titles.size() <= threshold){
                    // write the shows title with that duration to the report
                    out.println("S: Print Specific Key Listing");
                    out.println("+".repeat(50));
                    out.printf("Duration: %d (%d shows)%n", searchKey, titles.size());
                    out.println("-".repeat(50));
                    
                    for (int i = 0; i < titles.size(); i++) {
                        out.printf("%s%n", titles.get(i));
                    }
                    
                    out.println("\n\n");
                    out.flush();

                } else { // Multicolumn table view

                    int columnsPerRow = 6;

                    MyMap<Integer, MyArrayList<String>> singleMap = new MyHashMap<>();
                    singleMap.put(searchKey, titles);

                    Table table = new Table(singleMap, columnsPerRow);
                    String customHeaderStr = String.format("Duration: %d (%d shows)", searchKey, titles.size());
                    table.printGrid(customHeaderStr, table.getUniformRowLength(), false, out);    
                }

                System.out.printf("Shows with the duration of %d were written to report file.%n", searchKey);

            } else {
                System.out.printf("S: This duration %d. is not in the map%n", searchKey);
            }
        } else {
            input.nextLine(); // Clean invalid input token
            System.out.println("Invalid input. Duration must be an integer.");
        }
    }

    // PRE: TVList is initialized and showName is a string.
    // POST: Returns the duration key if the show exits in the map, otherwise will return -1 indicatting the show was not found.
    public static int findKeyByShowTitle(MyMap<Integer, MyArrayList<String>> TVList, String showName) {
        for (MyMap.Entry<Integer, MyArrayList<String>> entry : TVList.entrySet()) {
            for (String title : entry.getValue()) {
                if (title.equalsIgnoreCase(showName)) {
                    return entry.getKey(); // Return duration key
                }
            }
        }
        return -1; // Show not found
    }
}
