package objects.item;

public class InventoryException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public InventoryException() {
   }

   public InventoryException(String msg) {
      super(msg);
   }
}
