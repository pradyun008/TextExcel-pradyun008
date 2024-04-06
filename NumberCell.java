import java.util.ArrayList;
import java.util.*;

// Student Name
// Period X

/*
 * In Checkpoint #1: this class is ignored.
 * In Checkpoint #2: the NumberCell will hold a number only.
 *    Example:  a1 = 5
 * In Checkpoint #3: the NumberCell may hold an expression.
 *    Example:  a1 = ( 2 + a2 / a3 * a4 )
 * In Final Submission: the NumberCell may hold functions.
 *    Example:  a1 = ( sum b1 - b5 )
 */
public class NumberCell extends Cell {
  
    public String toString() {
        // TODO: You will need to CHANGE this in later checkpoints.
        // Leverage this method when fulfilling the command, "display [cell]"
        if(super.getExpression().matches(".*[a-zA-Z].*")){
          return (super.getExpression());
        }
        int num = (int)getValue();
        return (num + "");
    }

    

  public NumberCell(String expression) {
     // Call the default constructor of the superclass
    this.setExpression(expression);// Set the expression using the provided argument
  }


    /*
     * This will return the number for this cell.
     */
  /**
   * Retrieves the numerical value of the expression.
   * 
   * The method parses the expression and evaluates it to obtain its numerical value.
   * It handles arithmetic operations, parentheses, square root, logarithm, and exponentiation.
   * 
   * @return The numerical value of the expression.
   */
  public double getValue() {
      // If the expression starts with "(", it indicates a complex expression
      if(super.getExpression().startsWith("(")){
          // Extract the expression within parentheses
          String x = super.getExpression();
          String y = x.substring(2 , x.lastIndexOf(")") - 1);
          // Tokenize the expression
          String[] tokens = GridBase.smartSplit(y);
          // Change array elements if necessary
          array_changer(tokens);
          // Convert array elements within parentheses
          ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(tokens));
          arrayList = parentheses(arrayList);
          // Check operator precedence
          tokens = precedence_checker(arrayList);
          // Handle special cases: square root, logarithm, exponentiation
          if(y.contains("sqrt") || y.contains("log")){
              return sqrt_or_log(tokens[0] , tokens[1]);
          }
          if(y.contains("^")){
              String first_value = y.substring(0 , y.indexOf("^") - 1);
              String second_value = y.substring(y.indexOf("^") + 2 );
              return exponent(first_value , second_value);
          }

          // Evaluate the expression
          String first =(tokens[0]);
          for (int numOfOP = 0; numOfOP < tokens.length / 2; numOfOP++) {
              first = converter(first , tokens[numOfOP * 2 + 1],tokens[numOfOP * 2 + 2]);
          }
          return Double.parseDouble(first);
      } else {
          // If the expression does not start with "(", it is a simple numerical value
          if(super.getExpression().equals("")){
              // If the expression is empty, return 0
              return 0.0;
          }
          // Parse and return the numerical value of the expression
          return Double.parseDouble(super.getExpression());
      }
  }

      
      
        // TODO: You will need to CHANGE this in later checkpoints.
        // Leverage this method when fulfilling the command, "value [cell]"
       
    

  public String getExpression() {
      try {
          double value = getValue() ; // Parse the expression to a double
          return String.valueOf(value); // Convert the double back to a string
      } catch (NumberFormatException e) {
          // If parsing fails, return the original expression
          return super.getExpression();
      }
  }

/**
 * Performs arithmetic operation between two operands based on the given operator.
 * 
 * @param left The left operand.
 * @param operator The operator specifying the operation to be performed.
 * @param right The right operand.
 * @return The result of the arithmetic operation as a string.
 */
public String converter(String left , String operator , String right){
    // Perform arithmetic operation based on the operator
    if(operator.equals("+")){
        // Addition
        return(((Double.parseDouble(left))  + (Double.parseDouble(right))) + "") ;
    } else if((operator.equals("*"))){
        // Multiplication
        return(((Double.parseDouble(left)) * (Double.parseDouble(right))) + "") ;
    } else if((operator.equals("/"))){
        // Division
        return(((Double.parseDouble(left)) / (Double.parseDouble(right))) + "") ;
    } else {
        // Subtraction
        return((Double.parseDouble(left)- (Double.parseDouble(right))) + "") ;
    }
}


 /**
  * Computes the square root or natural logarithm of the given value.
  * 
  * If the type is "sqrt", the method computes the square root of the value.
  * If the type is "log", the method computes the natural logarithm of the value.
  * 
  * @param type The type of operation ("sqrt" for square root, "log" for natural logarithm).
  * @param value The value for which the operation is performed.
  * @return The result of the operation.
  */
 public double sqrt_or_log(String type , String value){
     double x = 0.0;

     // Parse and retrieve the numerical value of the cell if the value is a cell reference
     if(!(GridBase.parseCellNameFrom(value) == null)){
         String y = GridBase.grid.processCommand("value " + value);
         x = Double.parseDouble(y);
     }
     else{
         x = Double.parseDouble(value);
     }

     // Compute the square root or natural logarithm based on the specified type
     if(type.equals("sqrt")){
         // Square root
         return Math.sqrt(x);
     } else {
         // Natural logarithm
         return Math.log(x);
     }
 }


 /**
  * Computes the exponentiation of the given values.
  * 
  * The method raises the value of cell_1 to the power of cell_2.
  * If cell_1 or cell_2 are cell references, their values are retrieved before computation.
  * 
  * @param cell_1 The base value or cell reference.
  * @param cell_2 The exponent value or cell reference.
  * @return The result of the exponentiation.
  */
 public double exponent(String cell_1 , String cell_2){
     double x = 0.0;
     double z = 0.0;

     // Retrieve the numerical value of cell_1 if it's a cell reference
     if(!(GridBase.parseCellNameFrom(cell_1) == null)){
         String y = GridBase.grid.processCommand("value " + cell_1);
         x = Double.parseDouble(y);
     } else {
         x = Double.parseDouble(cell_1);
     }

     // Retrieve the numerical value of cell_2 if it's a cell reference
     if(!(GridBase.parseCellNameFrom(cell_2) == null)){
         String y = GridBase.grid.processCommand("value " + cell_2);
         z = Double.parseDouble(y);
     } else {
         z = Double.parseDouble(cell_2);
     }

     // Compute and return the result of exponentiation
     return Math.pow(x, z);
 }


  /**
   * Modifies the array elements by replacing cell references with their corresponding values.
   * 
   * The method iterates through the array and checks each element. If an element is a cell reference,
   * its value is retrieved from the grid and replaces the cell reference in the array.
   * 
   * @param tokens The array of tokens to be modified.
   */
  public void array_changer(String[] tokens){
      // Iterate through the array
      for(int i = 0 ; i < tokens.length ; i++){
          // Check if the element is a cell reference
          if(!(GridBase.parseCellNameFrom(tokens[i]) == null)){
              // Retrieve the value of the cell reference from the grid
              String token = tokens[i];
              tokens[i] =  GridBase.grid.processCommand("value " + token);
              // If the value is empty, set it to "0"
              if(tokens[i].equals("")){
                  tokens[i] = "0";
              }
          }
      }
  }


  /**
   * Checks for and handles precedence of multiplication and division operations in the list.
   * 
   * The method iterates through the list and checks for multiplication (*) and division (/) operations.
   * If found, it performs the operation and removes the operator and operands from the list.
   * It then recursively calls itself to handle additional multiplication and division operations.
   * 
   * @param list The list of tokens representing an expression.
   * @return An array of tokens with handled precedence.
   */
  public String[] precedence_checker(ArrayList<String> list){
      // Iterate through the list
      for(int i = 0 ; i < list.size() ; i++){
          String item = list.get(i);
          // Check if the current item is multiplication (*) or division (/)
          if(item.equals("*") || item.equals("/")){
              // Perform the operation and replace the operands and operator in the list
              list.set(i - 1, converter(list.get(i-1), list.get(i), list.get(i + 1)));
              list.remove(i);
              list.remove(i);
              // Recursively call precedence_checker to handle additional multiplication and division operations
              precedence_checker(list);
          }
      }
      // Convert the list back to an array and return it
      String[] tokens = new String[list.size()];
      tokens = list.toArray(tokens);
      return tokens;
  }


  /**
   * Handles parentheses in the list by evaluating expressions enclosed in parentheses.
   * 
   * The method iterates through the list and processes expressions within parentheses.
   * It replaces the expression within parentheses with its evaluated value.
   * 
   * @param list The list of tokens representing an expression.
   * @return The modified list after evaluating expressions within parentheses.
   */
  public ArrayList<String> parentheses(ArrayList<String> list){
      // Continue processing until all parentheses are resolved
      while(list.contains("(")){
          int lastindex = 0;
          int firstindex = 0;

          // Find the index of the first open parenthesis
          outerloop:
          for(int i = list.size() - 1 ; i >= 0 ; i--){
              String item = list.get(i);
              if(item.equals("(")){
                  firstindex = i;
                  break outerloop;
              }
          }

          // Find the index of the corresponding closing parenthesis
          outerloop:
          for(int i = 0 ; i < list.size() ; i++){
              String item = list.get(i);
              if(item.equals(")")){
                  lastindex = i;
                  break outerloop;
              }
          }

          // Evaluate the expression within parentheses
          String value = converter(list.get(firstindex + 1) , list.get(firstindex + 2) , list.get(firstindex + 3));

          // Replace the expression within parentheses with its evaluated value
          list.set(lastindex,value);
          // Remove the parentheses and the expression from the list
          list.remove(firstindex);
          list.remove(firstindex);
          list.remove(firstindex);
          list.remove(firstindex);
      }
      // Return the modified list
      return list;
  }


  }
  


   


