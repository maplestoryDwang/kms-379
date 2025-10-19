package scripting.newscripting;

public interface ScriptThreadManagerMBean {
   boolean isTerminated();

   boolean isShutdown();

   long getCompletedTaskCount();

   long getActiveCount();

   long getTaskCount();

   int getQueuedTasks();
}
