package database.callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBGenerateKeyCallback {
   void execute(PreparedStatement var1) throws SQLException;

   void afterAction(PreparedStatement var1, ResultSet var2) throws SQLException;
}
