package objects.utils;

import java.io.PrintStream;
import java.text.MessageFormat;

public class DebugStream extends PrintStream {
   private static final DebugStream INSTANCE = new DebugStream();

   public static void activate() {
      System.setOut(INSTANCE);
   }

   private DebugStream() {
      super(System.out);
   }

   @Override
   public void println(Object x) {
      this.showLocation();
      super.println(x);
   }

   @Override
   public void println(String x) {
      this.showLocation();
      super.println(x);
   }

   private void showLocation() {
      StackTraceElement element = Thread.currentThread().getStackTrace()[3];
      super.print(MessageFormat.format("({0}:{1, number,#}) : ", element.getFileName(), element.getLineNumber()));
   }
}
