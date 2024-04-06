// Student Name
// Period X
// Text Excel Project

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/* 
 * The Grid class will hold all the cells. It allows access to the cells via the
 * public methods. It will create a display String for the whole grid and process
 * many commands that update the cells. These command will include
 * sorting a range of cells and saving the grid contents to a file.
 *
 */
public class Grid extends GridBase {

  // These are called instance fields.
  // They are scoped to the instance of this Grid object.
  // Use them to keep track of the count of columns, rows and cell width.
  // They are initialized to the prescribed default values.
  // Notice that there is no "static" here.
  /*  The list_sum arraylist is used to keep track of values being updated making it so that particular sum gets     
      updated*/
  /*  The list_avg arraylist is used to keep track of values being updated making it so that particular avg gets     
    updated similar to sum but now just keep tracks of values in the range*/
  private int colCount = 100;
  private int rowCount = 100;
  private int cellWidth = 9;
  private ArrayList<String> list_sum = new ArrayList<>();
  private ArrayList<String> list_avg = new ArrayList<>();

  // The matrix method is used to intialize the "grid" that we will use
  private Cell[][] matrix = new Cell[rowCount][colCount];
  public Grid() {
    //The nested loop below i used to iterate throught the matrix
    for(int row = 0; row < rowCount; row++) {
      for (int col = 0; col < matrix[row].length; col++) {
        matrix[row][col] = new Cell(); /* this calling of cell constructor makes it so that each "cell" is refrencing a cell object */
      }
    }
  }

  // This method is used to sort particular cell values. It also handes self/circular refrences instances
  public String sorttime(String cell_1 , String cell_2 , String sort_type){
    String x = "you can't do that !";
    if(circular_reference_for_sort(cell_1 ,cell_2).equals(x)){ /* this method call checks to see if the two first values refrence each other */
      return x; // returns an appropiate response if a self/circular reference is found
    }
     ArrayList<Double> sorter = new ArrayList<Double>(); // this creates an arraylist for the sorting , basically all the values in the range
    // the following seven lines basically set up all the values needed 
     int row1 = rowfinder(cell_1);
     int col1 = colfinder(cell_1);
     int row2 = rowfinder(cell_2);
     int col2 = colfinder(cell_2);
     int col_min = Math.min(row1 , row2);
     int col_max = Math.max(row1 , row2);
     int row_min = Math.min(col1 , col2);
     int row_max = Math.max(col1 , col2);
     // this for loop iterates through the range of cells. Make sure none of them are self-referencing. And then adds them to the sorter arraylist
     for (int row = Math.min(row1, row2); row <= Math.max(row1, row2); row++) {
         for (int col = Math.min(col1, col2); col <= Math.max(col1, col2); col++) {
             if(!(matrix[col][row] instanceof NumberCell)){
              return "cant do it if its not a number";
             }
           // this looks for circular reference
             if(circular_reference_for_sort(cell_1 ,getCellLocation(col , row)).equals(x) || circular_reference_for_sort(cell_2 ,getCellLocation(col , row)).equals(x) ){
               return x;
             }
             sorter.add(matrix[col][row].getValue()); 
         }
     }
    // this if statement checks if it is asking for ascending or descending sort and sorts the sorter arraylist accordingly
    if(sort_type.equals("sorta")){
     Collections.sort(sorter);
    }else{
      Collections.sort(sorter, Collections.reverseOrder());
    }
    // this for loop goes through the entire range agains and gives them the values needed according to the sort request
    for (int row = row_min; row <= row_max; row++) {
     for (int col = col_min; col <= col_max; col++) {
         matrix[row][col].setExpression(String.valueOf(sorter.get(0)));
         sorter.remove(0);
     }
    }
    return "sorted !";
  }
  // this method gets a location at row and at col. It then converts it into the appropirate cell location. Ex : row:0 , col:0. This would be "a1"
  public String getCellLocation(int row, int col) {
      char colLetter = (char) ('a' + col); // Lowercase 'a' instead of 'A'
      row++;
      String x = Character.toString(colLetter) + row;
      return x;
  }
   // this method address the problem when the user needs to clear a specific cell , it takes the specific location. Uses rowfinder and colfinder and then sets that location to a new cell constructor. Making it not have any set expressions
  public void specifc_clear(String location){
    matrix[colfinder(location)][rowfinder(location)]  = new Cell();
  }
// this method takes the location and returns the row needed
  public int rowfinder(String command) {
    char variable = command.charAt(0);
    int row = variable - 97;
    return row;
  }
// similar to the method above it takes the location and returns the specific column that it's at
  public int colfinder(String command) {
    String variable = command.substring(1);
    int col = Integer.parseInt(variable);
    col--;
    return col;
  }
// this method is just used to clear the entire matrix
  public void clear() {
    // Iterate through each cell in the matrix
    for (int row = 0; row < matrix.length; row++) {
      for (int col = 0; col < matrix[row].length; col++) {
        // Set the expression of the cell to an empty string
         matrix[row][col] = new Cell();
    }
  }
    list_sum.clear(); // we do this because if we are resetting everything we also need to reset and formulas . Ex : sum a1 - a2.
    list_avg.clear(); // we do this for avg as well because we don't want any more formulas
  }

  // this method checks to see if a value fits in a cell. If it doesn't it uses cellWidth to determine how much of the value should be shown when the grid is printed
  public String cropper(String value) {
    if (value.length() > cellWidth) {
      String cropped_value = value.substring(0, cellWidth); // here's where the cropping happens
      return cropped_value;
    } else {
      return value; // just returns the value if it fits the cell size
    }
  }

  // this method address the specific command of dsiaplying a certain cell. It takes the location and returns what the cell would look like if it was printed
  public String display_option(int row, int col) {
    if (!(matrix[col][row].getExpression() == "")) {
      String location = getCellLocation(col, row);
      int indexInSumList = list_sum.indexOf(location); // these two lines accesing the arraylist , is accesing the location of the cell IF IT USED A FORMULA
      int indexInAvgList = list_avg.indexOf(location);

      if (indexInSumList != -1 && indexInSumList % 3 == 0) { // here it checks if it does contain a sum formula
          String new_value = sum(list_sum.get(indexInSumList + 1), list_sum.get(indexInSumList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value); // these two lines basically update the cell if any of the values have been changed
      }

      if (indexInAvgList != -1 && indexInAvgList % 3 == 0) { // here it checks if it does contain a avg formula
          String new_value = avg(list_avg.get(indexInAvgList + 1), list_avg.get(indexInAvgList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value); // similar to commments above , it also check it any updates need to made.
      }
      return matrix[col][row].getExpression();
    }
    return "0.0";
  }
 // In this method , it is another variation of dispalying the value of a certain cell
  public String actual_value_return(int row, int col) {
    if(matrix[col][row] instanceof DateCell){ // this checks to see if it a datecell then it uses.toString() because my tostring is what returns it in the right format
      return matrix[col][row].toString();
    }
    if (matrix[col][row] instanceof NumberCell) { // if it is a numbercell then it has a few more things to update before it returns a value

      // similar to the display option the below lines are used to calculate if sum  or avg were used
      String location = getCellLocation(row, col);
      int indexInSumList = list_sum.indexOf(location);
      int indexInAvgList = list_avg.indexOf(location);

      if (indexInSumList != -1 && indexInSumList % 3 == 0) {
          String new_value = sum(list_sum.get(indexInSumList + 1), list_sum.get(indexInSumList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value); // here's where the updating happens
      }

      if (indexInAvgList != -1 && indexInAvgList % 3 == 0) {
          String new_value = avg(list_avg.get(indexInAvgList + 1), list_avg.get(indexInAvgList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value); // here as well
      }
      return matrix[col][row].toString();
    } else { // this else statement is simply used if it is a textcell
      return String.format("\"%s\"", matrix[col][row].getExpression()); // here is just returns this with quotations to match the requirements
    }
  }
 // this method is simply used to see simple circular reference cases. Such as if a1 = b1 and you attempt to set b1 = a1
  public String circular_reference(String cell_location, String value){
    if(value.contains("(") && value.contains(")") ){ 
      String x = value.substring(2, value.length() - 2); // removes the parentheses
      if(x.contains(" ")){ // if it is a expression it splits it
        String[] tokens = GridBase.smartSplit(x);
        for(String y : tokens){
          if(!(GridBase.parseCellNameFrom(y) == null)){ // it then checks if any of the values in the array are a cell location
             String ref_value =  processCommand(y); // it then checks if their value contains the current location being mentioned
             if(ref_value.contains(cell_location)){
               return "you can't do that !"; // and if does then it simply says that you cannot do it.
             }
           }
        }
      }else{
        if(!(GridBase.parseCellNameFrom(x) == null)){ // this is the case for only one value entered , and if so it t then checks if their value contains the current location being mentioned
           String ref_value =  processCommand(x);
           if(ref_value.contains(cell_location)){
             return "you can't do that !";
           }
         }
      }  
    }
    return "its good"; // and if nothing else seems off it returns its good to signify that there is no circular reference 
  }

  /**
   * Checks for circular references between two cells.
   * 
   * The method takes two string parameters representing cell references (cell_1 and cell_2).
   * It processes the commands associated with each cell to obtain their values.
   * Then, it checks if there is a circular reference between the two cells by examining whether the value of one cell contains a reference to the other.
   * If a circular reference is found, it returns "you can't do that !"; otherwise, it returns "it's good".
   * 
   * @return A message indicating whether circular reference is found or not.
   */
  public String circular_reference_for_sort(String cell_1 , String cell_2){
      // Process the commands of the cells to get their values
      String value_of_cell1 = processCommand(cell_1);
      String value_of_cell2 = processCommand(cell_2);

      // Check if there's a circular reference between the cells
      if(value_of_cell1.contains(cell_2) || value_of_cell2.contains(cell_1) ){
          // If there's a circular reference, return a message indicating it's not allowed
          return "you can't do that !";
      } else {
          // If there's no circular reference, return a message indicating it's okay
          return "it's good";
      }
  }


  /**
   * Updates the value of a cell at the specified location.
   * 
   * The method first checks for circular references between the cell and the new value.
   * If a circular reference is detected, it returns an error message.
   * Otherwise, it parses the cell location to determine the row and column indices.
   * It then updates the cell value based on the provided value.
   * 
   * @param cell_location The location of the cell to be updated.
   * @param value The new value for the cell.
   * @return The expression representing the updated cell value, or an error message if the command is unknown or malformed.
   */
  public String updater(String cell_location, String value) {
      // Check for circular references
      if(circular_reference(cell_location,value).equals("you can't do that !")){
          // If circular reference is found, return error message
          return circular_reference(cell_location,value);
      } else {
          int row;
          int col;
          try {
              // Parse cell location to determine row and column indices
              row = rowfinder(cell_location);
              col = colfinder(cell_location);
          } catch (Exception e) {
              // If unable to parse cell location, return error message
              return "unknown or malformed command";
          }
          // Update cell value based on the provided value
          if (value.startsWith("\"")) {
              // If value starts with quotes, treat it as text
              matrix[col][row] = new TextCell(removeQuotes(value));
              return matrix[col][row].getExpression();
          } else if (isDateLikeString(value)) {
              // If the value resembles a date, treat it as text
              matrix[col][row] = new TextCell(value);
              return matrix[col][row].getExpression();
          } else if (value.contains("/") && !(value.contains(" "))) {
              // If value contains "/", treat it as a date
              matrix[col][row] = new DateCell(value);
              return matrix[col][row].getExpression();
          } else { 
              if(value.contains("sum")){
                  try{
                      // If value contains "sum", compute the sum
                      String cell_1 = value.substring(value.indexOf("(") + 6 , value.indexOf("-") - 1);
                      String cell_2 = value.substring(value.indexOf("-") + 2 , value.indexOf(")") - 1);
                      value = sum(cell_1 , cell_2);
                      matrix[col][row] = new NumberCell(value);
                      list_sum.add(cell_location);
                      list_sum.add(cell_1);
                      list_sum.add(cell_2);
                      return matrix[col][row].getExpression();
                  } catch(Exception e){
                      // If unable to compute sum, return error message
                      return "unknown or malformed command";
                  }
              }
              if(value.contains("avg")){
                  // If value contains "avg", compute the average
                  String cell_1 = value.substring(value.indexOf("(") + 6 , value.indexOf("-") - 1);
                  String cell_2 = value.substring(value.indexOf("-") + 2 , value.indexOf(")") - 1);
                  value = avg(cell_1 , cell_2);
                  matrix[col][row] = new NumberCell(value);
                  list_avg.add(cell_location);
                  list_avg.add(cell_1);
                  list_avg.add(cell_2);
                  return matrix[col][row].getExpression();
              }
              try {
                  // If none of the above conditions are met, treat value as a number
                  matrix[col][row] = new NumberCell(value);
                  return matrix[col][row].getExpression();
              } catch (Exception e) {
                  // If unable to parse value as a number, return error message
                  return "unknown or malformed command";
              }
          }
      }
  }

  /**
   * Checks if the given string resembles a date in the format "MMM d, yyyy".
   * 
   * The method attempts to parse the provided string using the specified date format.
   * If the parsing succeeds without throwing an exception, it returns true,
   * indicating that the string resembles a date in the specified format.
   * If an exception occurs during parsing, it returns false, indicating that
   * the string does not resemble a date in the specified format.
   * 
   * @param value The string to be checked.
   * @return True if the string resembles a date in the format "MMM d, yyyy"; otherwise, false.
   */
  private boolean isDateLikeString(String value) {
      try {
          // Attempt to parse the provided string using the specified date format
          SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
          dateFormat.parse(value);
          // If parsing succeeds, return true
          return true;
      } catch (Exception e) {
          // If an exception occurs during parsing, return false
          return false;
      }
  }
// this method is simply used to remove quotes for an expression , specifically for a textcell
  public String removeQuotes(String input) {
    if (input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"")) {
      // Check if the input starts and ends with double quotes
      return input.substring(1, input.length() - 1); // Remove the surrounding quotes
    } else {
      return input; // Return the original input if it's not surrounded by quotes
    }
  }
  // this method is sued for the sum command
  public String sum(String cell_1 , String cell_2) {
      double sum = 0;
      int row1 = rowfinder(cell_1);
      int col1 = colfinder(cell_1);
      int row2 = rowfinder(cell_2);
      int col2 = colfinder(cell_2);
// uses a nested for loop to iterate through the range , and add up all the values in that range
      for (int row = Math.min(row1, row2); row <= Math.max(row1, row2); row++) {
          for (int col = Math.min(col1, col2); col <= Math.max(col1, col2); col++) {
              sum += matrix[col][row].getValue();
          }
      } // it then returns the sum
      return Double.toString(sum);
  }
// this method is used to calcualte the average
 public String avg(String cell_1 , String cell_2) {
      int counter = 0; // similar to the method but also has a counter , as this method utilizes division
      double sum = 0;
      int row1 = rowfinder(cell_1);
      int col1 = colfinder(cell_1);
      int row2 = rowfinder(cell_2);
      int col2 = colfinder(cell_2);
// iterates through the range and adds up all the values from that range
      for (int row = Math.min(row1, row2); row <= Math.max(row1, row2); row++) {
          for (int col = Math.min(col1, col2); col <= Math.max(col1, col2); col++) {
              sum += matrix[col][row].getValue();
              counter++; // each time a new cell is visted it increments the counter as we need to keep track of how many cells we have visited to find the average
          }
      }
      sum /= counter; // it then divides sum by the counter to find the true average
      return Double.toString(sum); // it the returns the average
  }

// this method is solely used to create the header , one of the most basic and important methods in grid.
  public String headercreation() {
    String x = "    |"; // retuns one string as the whole header
    for (int i = 0; i < colCount; i++) {
      for (int j = 1; j <= cellWidth / 2; j++) {
        x += " "; // increments spaces for the header
      }

      x += String.valueOf((char) ('A' + i));
      for (int z = cellWidth / 2; z < cellWidth - 1; z++) {
        x += " "; // makes sure that columns are named right
      }
      x += "|"; // used for splitting between columns
    }
    x += "\n"; // it then goes to the next line for printing the actual grid
    return x; // the header is returned

  }
// the starting creator is used to print row numbers
  public String starting_creator(int rowcounter) {
    if (rowcounter > 9) {
      String starting_cell = " " + rowcounter + " |";
      return starting_cell; // actually prints that specific cell
    }
    String starting_cell = "  " + rowcounter + " |";
    return starting_cell;
  }
// this method is the actual creator of the individuals cells in the gird when its printed
  public String cell_creator(int row, int col) {
    String cell = "";
    if (matrix[row][col] == null || matrix[row][col].getExpression() == null
        || matrix[row][col].getExpression().isEmpty()) {
      for (int i = 1; i <= cellWidth; i++) {
        cell += " "; // if the cell at the corresponding point in the matirx is an empty cell then it just increments empty spaces to make the cell looks empty
      }
      cell += "|"; 
    } else { // if the cell is not empty then it finds its cell name and location
      String location = getCellLocation(row, col);
      int indexInSumList = list_sum.indexOf(location);
      int indexInAvgList = list_avg.indexOf(location);

      if (indexInSumList != -1 && indexInSumList % 3 == 0) { // it also checks it uses avg or sum int its formulas
          String new_value = sum(list_sum.get(indexInSumList + 1), list_sum.get(indexInSumList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value); // if it does then ti revaluates the  cell value
      }

      if (indexInAvgList != -1 && indexInAvgList % 3 == 0) { 
          String new_value = avg(list_avg.get(indexInAvgList + 1), list_avg.get(indexInAvgList + 2));
          matrix[colfinder(location)][rowfinder(location)].setExpression(new_value);
      }
      String raw_expression = matrix[row][col].getExpression();
      String expression = cropper(raw_expression); // it then sees if the expression is too long , and if it is then it cuts it down 
      int expressionLength = expression.length();
      for (int i = 0; i < Math.abs(expressionLength - cellWidth); i++) {
        cell += " ";
      }
      cell += expression + "|";
    }
    return cell;
  }
// this method prints the +-----+ line between each row
  public String midline() {
    String mid = "----+";
    for (int i = 1; i <= colCount; i++) {
      for (int j = 1; j <= cellWidth; j++) {
        mid += "-";
      }
      mid += "+";
    }

    return mid;
  }
// this prints the whole gird by bringing togethter all the components. It uses header creation for the ehader and midline for the middle. ti then  iteratores through the matrix and uses the cell creator to create each indivudal cell. 
  public String printgrid() {
      String x = headercreation() + midline(); // beginning
      x += "\n";
      for (int i = 0; i < rowCount; i++) {
        x += starting_creator(i + 1); // the cell being created 
        for (int j = 0; j < colCount; j++) {
          x += cell_creator(i, j);
        }
        x += "\n";
        x += midline(); // the split between each row
        x += "\n";
      }
      return x;
  }
 // this methods just checks if any of the original instance fields are ebing refrenced or changed 
  public String handleNumericCommand2(String command, String keyword) {
    String result = null;
    if (command.equals("rows") || command.equals("cols") || command.equals("width")) {
      String value = giver(keyword);
      return value;
    } else { // plus 2 not plus one because if plus one then you are going to include the
             // space and when you come to convert to int you might get an error
      String value_needed = command.substring((command.indexOf("=") + 2));
      result = setterspecific(value_needed, keyword);
      return result;
    }

  }
// this is just an acceses as it returns the value of the instance fields
  public String giver(String keyword) {
    String result = null;
    if (keyword.equals("rows")) {
      return Integer.toString(rowCount);
    }
    if (keyword.equals("cols")) {
      return Integer.toString(colCount);
    }
    if (keyword.equals("width")) {
      return Integer.toString(cellWidth);
    }
    return result;

  }
// this method if a mutator as it sets the value of specified instance fields
  public String setterspecific(String num, String keyword) {
    String result = null;
    if (keyword.equalsIgnoreCase("rows")) {
      rowCount = Integer.valueOf(num); // setting here 
      return Integer.toString(rowCount);
    }
    if (keyword.equalsIgnoreCase("cols")) {
      colCount = Integer.valueOf(num); // setting here
      return Integer.toString(colCount);
    }
    if (keyword.equalsIgnoreCase("width")) {
      cellWidth = Integer.valueOf(num); // setting heren
      return Integer.toString(cellWidth);
    }
    return result;

  }

  /*
   * This method processes a user command.
   * 
   * Checkpoint #1 commands are: print : render a text based version of the matrix
   * width = [value] : set the cell width width : get the cell width rows =
   * [value] : set the row count cols = [value] : set the column count rows : get
   * the row count cols : get the column count
   *
   * Checkpoint #2 commands are: [cell] = [expression] : set the cell's
   * expression, for checkpoint # expressions may be... - a value such as 5.
   * Example: a2 = 5 - a string such as "hello". Example: a3 = "hello" [cell] :
   * get the cell's expression, NOT the cell's value value [cell] : get the cell
   * value expr [cell] : get the cell's expression, NOT the cell's value display
   * [cell] : get the string for how the cell wants to display itself clear :
   * empty out the entire matrix save [file] : saves to a file all the commands
   * necessary to regenerate the grid's contents
   *
   * Checkpoint #3 commands are: [cell] = [expression] : where the expression is a
   * complicated formula. Example: a1 = ( 3.141 * b3 + b1 - c2 / 4 )
   *
   * Final commands are: [cell] = [expression] : where the expression may contain
   * a single function, sum or avg: Example: a1 = ( sum a1 - a3 ) Example: b1 = (
   * avg a1 - d1 ) clear [cell] : empty out a single cell. Example: clear a1 sorta
   * [range] : sort the range in ascending order. Example: sorta a1 - a5 sortd
   * [range] : sort the range in descending order. Example: sortd b1 - e1
   * 
   *
   * 
   * Parameters: command : The command to be processed. Returns : The results of
   * the command as a string to be printed by the infrastructure.
   */
  /**
   * Processes the given command and returns the result.
   * 
   * The method interprets various commands related to managing and manipulating the grid.
   * It handles commands such as printing the grid, handling numeric commands,
   * clearing cells, displaying cell values, setting cell values, evaluating expressions, and sorting cells.
   * 
   * @param command The command to be processed.
   * @return The result of processing the command, or an error message if the command is unknown or malformed.
   */
  public String processCommand(String command) {
      String result = null;

      // Check various types of commands and perform corresponding actions
      if (command.startsWith("print")) {
          // Print the grid
          result = printgrid();
      } else if (command.contains("rows")) {
          // Handle numeric command related to rows
          result = handleNumericCommand2(command, "rows");
      } else if (command.contains("cols")) {
          // Handle numeric command related to columns
          result = handleNumericCommand2(command, "cols");
      } else if (command.contains("width")) {
          // Handle numeric command related to width
          result = handleNumericCommand2(command, "width");
      } else if (command.startsWith("clear")) {
          // Clear cells
          if(command.length() > 5){
              String location = command.substring(command.indexOf("r") + 2);
              specifc_clear(location);
          } else {
              clear();
          }
          result = "cleared!";
      } else if (command.startsWith("display")) {
          // Display cell value
          String x = command.substring(command.indexOf("y") + 2);
          int row = rowfinder(x);
          int col = colfinder(x);
          result = display_option(row, col);
      } else if (command.startsWith("value")){
          // Get cell value
          String location = command.substring(6);
          int row = rowfinder(location);
          int col = colfinder(location);
          result =  display_option(row, col);
      } else if (Character.isDigit(command.charAt(1)) && command.length() <= 3){
          // Get actual value of a cell
          int row = rowfinder(command);
          int col = colfinder(command);
          result = actual_value_return(row , col);
      } else if (command.contains("=") && !(command.contains("rows") || command.contains("cols"))) {
          // Set cell value
          String value_input = command.substring(command.indexOf("=") + 2);
          result = updater(command.substring(0, command.indexOf("=") - 1), value_input);
      } else if (command.startsWith("expr")){
          // Evaluate expression
          String location = command.substring(5);
          int row = rowfinder(location);
          int col = colfinder(location);
          result = actual_value_return(row , col);
      } else if(command.startsWith("sort")){
          // Sort cells
          String sort_type = command.substring(0,5);
          String cell_1 = command.substring(6 , command.indexOf("-") - 1);
          String cell_2 = command.substring(command.indexOf("-") + 2);
          result = sorttime(cell_1, cell_2, sort_type);
      }
      else {
          // If the command is unknown or malformed, return an error message
          result = "unknown or malformed command: " + command;
      }

      // If the result is still null, it indicates an unknown or malformed command
      if (result == null)
          result = "unknown or malformed command: " + command;

      return result;
  }


  /**
   * saveToFile
   *
   * This method will process the command: "save {filename}"
   * <p>
   * Ask the matrix for all formulas for all non-empty cells. Empty cells should
   * not be saved.
   *
   * Save all properties such as grid size and cell width (if available)
   * 
   * @param filename is the name of the file to save
   * @return A message to user about the success/failure of saving to a file.
   */
  private String saveToFile(String filename) {
    File file = new File(filename);
    String result = "Saved to file: " + file.getAbsolutePath();

    try {
      // Get the writer ready
      PrintStream writer = new PrintStream(file);
      saveGrid(writer);
    } catch (FileNotFoundException e) {
      result = "Cannot write to the file: " + file.getAbsolutePath();
    }

    return result;
  }

  /**
   * saveGrid will save the gride to a file.
   *
   * Ask the matrix for all formulas for all non-empty cells. Empty cells should
   * not be saved.
   *
   * Save all properties such as grid size and cell width (if available)
   * 
   * @param writer is the PrintStream to print to
   */
  public void saveGrid(PrintStream writer) {
    // save the rows, cols and width
    writer.println("rows = " + rowCount);
    writer.println("cols = " + colCount);
    writer.println("width = " + cellWidth);

    // save the grid formulas, for every cell that is not empty
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < colCount; col++) {
        String formula = matrix[row][col].getExpression();
        if (formula != null && formula.length() > 0) {
          writer.println("" + (char) ('A' + col) + (row + 1) + " = " + formula);
        }
      }
    }
  }

}
