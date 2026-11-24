/**
* NAME: Derimar Gray
* Date: 11/24/2026
* ASSIGNMENT: Program 8 - Maps Revisited
* DESCRIPTION: Input the data. Through menu and user input, add, delete and display items in the map and display results
            to a report file along with a short message to the user (on the terminal) about the success of the aciton taken.
            Program functionalities are the same as program 2, but with a couple of updates:
                a) Changes from the use of Map to MyHashMap, where the key is the show duration and the use of chaining using MyLinkedList.
                b) Updates logic to accommodate the new testing scenarios identifies in program 2. Adding checks for valid keys and logic
                   to ensure does not duplicate a show for a particular duration.

* SOURCES: 
    - https://github.com/UMKC-CS303/CS303Lecture14
    - https://github.com/UMKC-CS303/FA26Pgm2-Derimarg.git

*/

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to Program 8: Maps Revisited");
        MyMap<Integer, MyArrayList<String>> TVList = new MyHashMap<>();

        Scanner input = new Scanner(System.in);

        // open output file
        try {
            PrintWriter out = new PrintWriter("report.txt");

            // load data into map
            Functions.loadData(TVList);

            String menuItem = Functions.getMenuItem(input);
            boolean needsSpacing = false; // Tracks cases where A or D item already happened

            while (!menuItem.equals("Q")) {

                // Track user changes
                if (menuItem.equalsIgnoreCase("A") || menuItem.equalsIgnoreCase("D")){
                    needsSpacing = true;
                }

                // test for valid menu options & call appropriate functions
                switch (menuItem) {
                    case "A" -> Functions.addShow(TVList, input, out);
                    case "D" -> Functions.deleteShow(TVList, input, out);
                    case "K", "P", "S" ->  {
                        if (needsSpacing) {
                            out.println("\n\n");
                            needsSpacing = false; // Reset flag
                        }

                        if (menuItem.equals("K")) Functions.printKeys(TVList, out);
                        else if (menuItem.equals("P")) Functions.printMap(TVList, out);
                        else Functions.printSpecificKey(TVList, input, out);
                    }
                }

                // Prompt for next action at the end of the loop iteration
                menuItem = Functions.getMenuItem(input);

            }

            // close files
            input.close();
            out.close();

        } catch (Exception e) {
            System.out.println("Error in input record");
        }
    }

}