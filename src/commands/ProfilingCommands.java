package commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import objects.users.MapleClient;
import objects.utils.CPUSampler;

public class ProfilingCommands implements Command {
   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("감시시작", "", "프로파일러로 CPU 감시를 시작합니다.", 6), new CommandDefinition("감시종료", "<파일이름>", "프로파일러로 CPU 감시를 종료하고 해당 파일이름으로 결과를 저장합니다.", 6)
      };
   }

   @Override
   public void execute(MapleClient c, String[] splitted) {
      if (splitted[0].equals("감시시작")) {
         CPUSampler sampler = CPUSampler.getInstance();
         sampler.addIncluded("api");
         sampler.addIncluded("commands");
         sampler.addIncluded("constants");
         sampler.addIncluded("logging");
         sampler.addIncluded("network");
         sampler.addIncluded("objects");
         sampler.addIncluded("scripting");
         sampler.addIncluded("security");
         sampler.start();
      } else if (splitted[0].equals("감시종료")) {
         CPUSampler sampler = CPUSampler.getInstance();

         try {
            String filename = "odinprofile.txt";
            if (splitted.length > 1) {
               filename = splitted[1];
            }

            File file = new File(filename);
            if (file.exists()) {
               c.getPlayer().dropMessage(6, "입력한 파일이 이미 존재합니다. 다른 이름을 사용해 주세요.");
               return;
            }

            sampler.stop();
            FileWriter fw = new FileWriter(file);
            sampler.save(fw, 1, 10);
            fw.close();
         } catch (IOException var7) {
            System.err.println("Error saving profile" + var7);
         }

         sampler.reset();
      }
   }
}
