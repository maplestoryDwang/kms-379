package objects.fields.gameobject;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import objects.utils.Pair;

public class ReactorStats {
   private Point tl;
   private Point br;
   private Map<Byte, ReactorStats.StateData> stateInfo = new HashMap<>();

   public void setTL(Point tl) {
      this.tl = tl;
   }

   public void setBR(Point br) {
      this.br = br;
   }

   public Point getTL() {
      return this.tl;
   }

   public Point getBR() {
      return this.br;
   }

   public void addState(byte state, int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut, byte canTouch) {
      this.stateInfo.put(state, new ReactorStats.StateData(type, reactItem, nextState, timeOut, canTouch));
   }

   public byte getNextState(byte state) {
      ReactorStats.StateData nextState = this.stateInfo.get(state);
      return nextState != null ? nextState.getNextState() : -1;
   }

   public int getType(byte state) {
      ReactorStats.StateData nextState = this.stateInfo.get(state);
      return nextState != null ? nextState.getType() : -1;
   }

   public Pair<Integer, Integer> getReactItem(byte state) {
      ReactorStats.StateData nextState = this.stateInfo.get(state);
      return nextState != null ? nextState.getReactItem() : null;
   }

   public int getTimeOut(byte state) {
      ReactorStats.StateData nextState = this.stateInfo.get(state);
      return nextState != null ? nextState.getTimeOut() : -1;
   }

   public byte canTouch(byte state) {
      ReactorStats.StateData nextState = this.stateInfo.get(state);
      return nextState != null ? nextState.canTouch() : 0;
   }

   private static class StateData {
      private int type;
      private int timeOut;
      private Pair<Integer, Integer> reactItem;
      private byte nextState;
      private byte canTouch;

      private StateData(int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut, byte canTouch) {
         this.type = type;
         this.reactItem = reactItem;
         this.nextState = nextState;
         this.timeOut = timeOut;
         this.canTouch = canTouch;
      }

      private int getType() {
         return this.type;
      }

      private byte getNextState() {
         return this.nextState;
      }

      private Pair<Integer, Integer> getReactItem() {
         return this.reactItem;
      }

      private int getTimeOut() {
         return this.timeOut;
      }

      private byte canTouch() {
         return this.canTouch;
      }
   }
}
