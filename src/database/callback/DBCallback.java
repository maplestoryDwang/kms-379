package database.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DBCallback {
   void execute(PreparedStatement var1) throws SQLException;
}
