package commands;

public class IllegalCommandSyntaxException extends Exception {
   public IllegalCommandSyntaxException() {
   }

   public IllegalCommandSyntaxException(String message) {
      super(message);
   }

   public IllegalCommandSyntaxException(int expectedArguments) {
      super("Expected at least " + expectedArguments + " arguments");
   }
}
