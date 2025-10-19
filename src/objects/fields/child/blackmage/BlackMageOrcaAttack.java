package objects.fields.child.blackmage;

import java.awt.Point;
import java.util.List;
import network.encode.PacketEncoder;

public class BlackMageOrcaAttack {
   private int orcaKey;
   private int attackType;
   private Point targetPosition;
   private int targetObjectID;
   private List<BlackMageOrcaAttackEntry> attackEntrys;

   public BlackMageOrcaAttack(int orcaKey, int attackType, Point targetPosition, int targetObjectID, List<BlackMageOrcaAttackEntry> attackEntrys) {
      this.orcaKey = orcaKey;
      this.attackType = attackType;
      this.targetPosition = targetPosition;
      this.targetObjectID = targetObjectID;
      this.attackEntrys = attackEntrys;
   }

   public int getOrcaKey() {
      return this.orcaKey;
   }

   public void setOrcaKey(int orcaKey) {
      this.orcaKey = orcaKey;
   }

   public int getAttackType() {
      return this.attackType;
   }

   public void setAttackType(int attackType) {
      this.attackType = attackType;
   }

   public Point getTargetPosition() {
      return this.targetPosition;
   }

   public void setTargetPosition(Point targetPosition) {
      this.targetPosition = targetPosition;
   }

   public int getTargetObjectID() {
      return this.targetObjectID;
   }

   public void setTargetObjectID(int targetObjectID) {
      this.targetObjectID = targetObjectID;
   }

   public List<BlackMageOrcaAttackEntry> getAttackEntrys() {
      return this.attackEntrys;
   }

   public void setAttackEntrys(List<BlackMageOrcaAttackEntry> attackEntrys) {
      this.attackEntrys = attackEntrys;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.orcaKey);
      packet.writeInt(this.attackType);
      packet.writeInt(this.targetPosition.x);
      packet.writeInt(this.targetPosition.y);
      packet.writeInt(this.targetObjectID);
      packet.writeInt(this.attackEntrys.size());

      for (BlackMageOrcaAttackEntry entry : this.attackEntrys) {
         entry.encode(packet);
      }
   }
}
