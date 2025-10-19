package objects.fields.events;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class MapleOxQuizFactory {
   private final Map<Pair<Integer, Integer>, MapleOxQuizFactory.MapleOxQuizEntry> questionCache = new HashMap<>();
   private static final MapleOxQuizFactory instance = new MapleOxQuizFactory();

   public MapleOxQuizFactory() {
      this.initialize();
   }

   public static MapleOxQuizFactory getInstance() {
      return instance;
   }

   public Entry<Pair<Integer, Integer>, MapleOxQuizFactory.MapleOxQuizEntry> grabRandomQuestion() {
      int size = this.questionCache.size();

      while (true) {
         for (Entry<Pair<Integer, Integer>, MapleOxQuizFactory.MapleOxQuizEntry> oxquiz : this.questionCache.entrySet()) {
            if (Randomizer.nextInt(size) == 0) {
               return oxquiz;
            }
         }
      }
   }

   private void initialize() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_oxdata");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            this.questionCache.put(new Pair<>(rs.getInt("questionset"), rs.getInt("questionid")), this.get(rs));
         }

         rs.close();
         ps.close();
      } catch (Exception var7) {
         System.out.println("OX Init Err");
         var7.printStackTrace();
      }
   }

   private MapleOxQuizFactory.MapleOxQuizEntry get(ResultSet rs) throws SQLException {
      return new MapleOxQuizFactory.MapleOxQuizEntry(
         rs.getString("question"), rs.getString("display"), this.getAnswerByText(rs.getString("answer")), rs.getInt("questionset"), rs.getInt("questionid")
      );
   }

   private int getAnswerByText(String text) {
      if (text.equalsIgnoreCase("x")) {
         return 0;
      } else {
         return text.equalsIgnoreCase("o") ? 1 : -1;
      }
   }

   public static class MapleOxQuizEntry {
      private String question;
      private String answerText;
      private int answer;
      private int questionset;
      private int questionid;

      public MapleOxQuizEntry(String question, String answerText, int answer, int questionset, int questionid) {
         this.question = question;
         this.answerText = answerText;
         this.answer = answer;
         this.questionset = questionset;
         this.questionid = questionid;
      }

      public String getQuestion() {
         return this.question;
      }

      public String getAnswerText() {
         return this.answerText;
      }

      public int getAnswer() {
         return this.answer;
      }

      public int getQuestionSet() {
         return this.questionset;
      }

      public int getQuestionId() {
         return this.questionid;
      }
   }
}
