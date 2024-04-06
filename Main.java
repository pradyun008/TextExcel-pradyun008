// Student Name
// Period X
// Text Excel Project

import java.io.*;
import java.util.*;

/*
 * This class, Main, is the main implementation of the TextExcel program. It
 * implements the requirements established by ExcelBase. Most methods will be
 * private as the only public methods necessary are to processCommand(). This
 * class will handle certain commands that the Grid does not: help and load
 * file.
 */
public class Main extends ExcelBase {

    public static void main(String args[]) {

        // Create our Grid object and assign it to the GridBase
        // static field so that we can reference it later on.
        GridBase.grid = new Grid();
        Main engine = new Main();

        engine.runInputLoop();
    }

    /*
     * This method will parse a line that contains a command. It will delegate the
     * command to the Grid if the Grid should handle it. It will call the
     * appropriate method to handle the command. This method prints NOTHING!
     *
     * ALL Commands should be distributed to an appropriate method. Here are the
     * Checkpoint #1 commands:
     *   help  : provides help to the user on how to use Text Excel
     *   print : returns a string of the printed grid. The grid does this for itself!
     *   rows  : returns the number of rows currently in the grid. The grid knows this info.
     *   cols  : returns the number of columns currently in the grid 
     *   width : returns the width of an individual cell that is used when
     *           displaying the grid contents.
     *   rows = 5  : resizes the grid to have 5 rows. The grid contents will be cleared.
     *   cols = 3  : resizes the grid to have 3 columns. The grid contents will be cleared.
     *   width = 6 : resizes the width of a cell to be 6 characters wide
     *               when printing the grid. 
     *   load file1.txt  : opens the file specified and processes all commands in it.
     * 
     * Parameters:
     *    command : The command to be processed (described above)
     * Returns:
     *    The result of the command which will be printed by the infrastructure.
     */
    public String processCommand(String command) {
        String result = null;
      
        if(command.contains("help")){
          result = help();
        }
       
        
        


        // TODO: handle help command here...

        // TODO: handle all file related commands here...

        // Dispatch the command to the Grid object to see if it can handle it.
        if (result == null && GridBase.grid != null) {
            // The GridBase class has a static field, grid, of type GridBase.
            // Ask the grid object to process the command.
            result = GridBase.grid.processCommand(command);
        }

        // the command is still not handled
        if (result == null)
            result = "Unhandled";

        return result;
    }

    /*
     * Method loadFromFile.
     *
     * This will process the command: load {filename}
     *
     * Call processCommand() for every line in the file. During file processing,
     * there should be no output in the final implementation.
     * 
     * Parameter: 
     *    filename : The name/path to the file
     * Returns: 
     *    true/false: True if the file was found and the commands in the file
     *                were processed by processCommand.
     */
    private String loadFromFile(String filename) {

        // create a File object at this scope so we can have the file's path
        File file = new File(filename);
        String result = "Load command not yet implemented";
        try {
            Scanner reader = new Scanner(file);

            // Read the file and process all the commands in the file.
            while (reader.hasNextLine()) {
                String cmd = reader.nextLine();
                // We print the cmd and it's result so we can 
                // see if it is working or not.
                String cmdResult = processCommand(cmd);
                System.out.printf("result: \"%s\"  cmd: \"%s\"\n", result, cmdResult);
            }
            reader.close();
            // If successful, set the result to say that
            result = "File loaded successfully";
        } catch (FileNotFoundException e) {
            // Use the file object to get the absolute path
            result = "Could not find file: " + file.getAbsolutePath();
        } catch (Exception e) {
            result = "ERROR: Exception thrown during file processing. Student must FIX!";
        }

        return result;
    }

  public String help(){
    return("in this application you can use\nprint , rows , cols , width , or change either of those values and get a very helpful table for you to use!\n once you are satisfied with your row , column , and width values you can use the print command to print the table ") ;
  }

}
