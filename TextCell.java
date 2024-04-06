// Student Name
// Period X


// This class highly leverages inheritance and requires very little code.
public class TextCell extends Cell {

    /*
    *  This will return the string for how a TextCell wants to display
    *  itself. All it needs to do is remove the bounding quotes from
    *  the expression.
    */
    public String toString() {
        // TODO: Get the expression and remove the quotes
        return getExpression();
    }

  public TextCell(String expression) {
    // Call the default constructor of the superclass
    this.setExpression(expression);// Set the expression using the provided argument
  }
}
