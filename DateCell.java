// Pradyun Pinapala
// Period 5

/*
 * This is a stub class that will be completed for the Final Submission.
 *
 * For the Final submission:
 *   Students should leverage the SimpleDateFormat and/or DateFormat.
 *   Do NOT implement from scratch. Instead, search the internet for help
 *   on how to use SimpleDateFormat/DateFormat to accomplish the desired
 *   behavior.
 * 
 *   A properly implemented class can have as few as ~10 lines of code.
 */
 // https://www.youtube.com/watch?v=G8_B214NFlM
import java.text.SimpleDateFormat;
import java.util.Date;



public class DateCell extends Cell {

  // Private variable to store the display version of the date
  private String display_version;

  // Constructor to initialize DateCell with a given value
  public DateCell(String value){
      // Call the superclass constructor to set the expression
      this.setExpression(value);
      display_version = value; // Store the display version
  }

  // Method to get the formatted expression for display
  public String getExpression() {
      // Define date format based on the length of the expression
      SimpleDateFormat date_format;
      String date = super.getExpression(); // Get the expression value
      if(date.length() < 10){
          date_format = new SimpleDateFormat("MM/dd/yy");
      } else {
          date_format = new SimpleDateFormat("MM/dd/yyyy");
      }

      SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy"); // Define output format

      if(date.equals("")){ // If the date is empty, return empty string
          return date;
      }

      try {
          Date parsedDate = date_format.parse(date); // Parse the date
          return outputFormat.format(parsedDate); // Format the parsed date and return
      } catch (Exception e){
          throw new IllegalArgumentException("Invalid date"); // Throw exception for invalid date format
      }
  }

  // Method to return the display version of the date as a string
  public String toString(){
      return display_version; // Return the display version
  }   

    
}
