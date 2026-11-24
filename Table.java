import java.io.PrintWriter;

public class Table {
  private int columnsPerRow;
  private int padding;
  private int uniformRowLength;
  private int[] globalColWidths;
  private boolean isKeyListing;
  private MyMap<Integer, MyArrayList<String>> map;

  // PRE: TVList is initialized, columns per row is a valid integer
  // POST: Initializes the table properties with default padding and calculates column widths
  public Table(MyMap<Integer, MyArrayList<String>> TVList, int columnsPerRow){
    this.map = TVList;
    this.columnsPerRow = (columnsPerRow <= 0) ? 4 : columnsPerRow;
    this.padding = 3;
    this.uniformRowLength = 9;
    this.globalColWidths = new int[columnsPerRow];
    calculateProperties();
  }

  // PRE: TVList is initialized, columns per row is a valid integer
  // POST: Initializes the table properties with custom row length and key listing mode, then calculates column widths
  public Table(MyMap<Integer, MyArrayList<String>> TVList, int columnsPerRow, boolean isKeyListing){
    this.map = TVList;
    this.columnsPerRow = (columnsPerRow <= 0) ? 4 : columnsPerRow;
    this.padding = 3;
    this.uniformRowLength = 0;
    this.globalColWidths = new int[columnsPerRow];
    this.isKeyListing = isKeyListing;
    calculateProperties();
  }

  // PRE: a valid uniform row length
  // POST: updates the uniform row width used for tables borders
  public void setUniformRowLength(int uniformRowLength) {
    this.uniformRowLength = uniformRowLength;
  }

  // PRE: none
  // POST: returns the calculated uniform row length of the table
  public int getUniformRowLength() {
    return uniformRowLength;
  }

  // PRE: none
  // POST: returns the array of calculated column widths
  public int[] getGlobalWidths() {
    return globalColWidths;
  }

  // PRE: none
  // POST: calculates global columns widths
  // for each each column and sets the uniform row length based on item lengths
  private void calculateProperties() {
    if (!isKeyListing) {

        for (Integer duration : map.keySet()) {
          MyArrayList<String> shows = map.get(duration);

            if (shows != null) {
                for (int i = 0; i < shows.size(); i++) {
                    int colIdx = i % columnsPerRow;
                    int titleLength = shows.get(i).length();
                    if (titleLength > globalColWidths[colIdx]) {
                        globalColWidths[colIdx] = titleLength;
                    }
                }
            }
        }

        for (int c = 0; c < globalColWidths.length; c++) {
            globalColWidths[c] += padding;
        }

        for (int w : globalColWidths) {
            uniformRowLength += w;
        }
    } else {
      int maxKeyLength = 4;
      for (Integer k : map.keySet()) {
        MyArrayList<String> items = map.get(k);
        if (items != null) {
          for (int i = 0; i < items.size(); i++) {
            int len = items.get(i).length();
            if (len > maxKeyLength) maxKeyLength = len;
          }
        }
      }

      int colWidth = maxKeyLength + padding;
      for (int c = 0; c < globalColWidths.length; c++) {
        globalColWidths[c] = colWidth;
      }

      for (int w : globalColWidths) {
        uniformRowLength += w;
      }
    }
  }
  
  // PRE: a formatted custom header string, number of timer for the divider, and the output report
  // POST: Prints a custom header box followed by the formatted grid body to the report file
  public void printGrid(String customHeader, int numOfTimes, boolean hasPrefix, PrintWriter out) {
      numOfTimes = (numOfTimes <= 0) ? 50 : numOfTimes;
        headerBox("S: Print Specific Key (Durations) Listing", customHeader, numOfTimes, out);
        printGridBody(hasPrefix, out);
  }
  
  // PRE: header command is a valid menu command and report is provided
  // POST: prints the standard section header folled by the fomatted grid body to the report file
  public void printGrid(String headerCommand, boolean hasPrefix, PrintWriter out) {
    header(headerCommand, uniformRowLength, out);
    printGridBody(hasPrefix, out);
  }

  // PRE: boolean for prefix, and report is provide
  // POST: formats and writes all map entries in aligned columns with row borders to the file
  public void printGridBody(boolean hasPrefix, PrintWriter out) {
       for (Integer key : map.keySet()) {
          MyArrayList<String> items = map.get(key);

          if (items != null && !items.isEmpty()) {
            if (hasPrefix) {
              out.printf("%-8d | ", key);
            }

            StringBuilder currentLine = new StringBuilder();
            int currentCount = 0;

            for (int i = 0; i < items.size(); i++) {
                int colIdx = i % columnsPerRow;

                // format item string
                String formattedTitle = String.format("%-" + globalColWidths[colIdx] + "s", items.get(i));
                
                currentLine.append(formattedTitle);
                currentCount++;
                
                // print line when reached the end ot items
                if (currentCount == columnsPerRow || i == items.size() - 1) {
                    out.print(currentLine.toString());
                    out.println();

                    // if has prefix, continuo adding indent it there are more items
                    if (hasPrefix && i < items.size() - 1) {
                        out.print("         | ");
                    }
                    currentLine.setLength(0);
                    currentCount = 0;
                }
            }

            if (hasPrefix) {
              out.println("-".repeat(uniformRowLength));
            }
          }
        }

        out.println("\n\n");
        out.flush();
    }

    // PRE: menu item string, report ready to be used.
    // POST: Prints formatted table header to the report.
    public void header(String command, int numOfTimes, PrintWriter out) {
        String title = "";
        String headerText = "";

        switch (command) {
            case "K" -> {
                title = "K: Print all keys (Durations)";
                headerText = "durations";
            }
            case "P" -> {
                title = "P: Print Map Listing";
                headerText = String.format("%-8s | %s", "duration", "shows");
            }

        }

        headerBox(title, headerText, numOfTimes, out);
    }

    // PRE: title and header text, box width are provided
    // POST: prints a formatted title and header banner
    private void headerBox(String title, String headerText, int width, PrintWriter out) {
      width = (width <= 0 ? 50 : width);
      out.println(title);
      out.println("+".repeat(width));
      if (headerText != null && !headerText.isEmpty()) {
        out.println(headerText.toUpperCase());
      }
      out.println("-".repeat(width));
    }
}
