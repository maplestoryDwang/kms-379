package objects.fields.child.minigame.yutgame;

import network.encode.PacketEncoder;

public class YutGameResult_InstallItem extends YutGameResultEntry {
   private int position = 0;
   private boolean create = true;
   private YutGameResult_Action.InstallItemType itemType = null;

   public YutGameResult_InstallItem(int position, YutGameResult_Action.InstallItemType type, boolean create) {
      this.setPosition(position);
      this.setItemType(type);
      this.setCreate(create);
   }

   @Override
   public YutGameResultType getType() {
      return YutGameResultType.YutGameInstallItem;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(YutGameResultType.YutGameInstallItem.getType());
      packet.writeInt(this.position);
      packet.writeInt(this.isCreate() ? this.getItemType().getType() : 0);
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public YutGameResult_Action.InstallItemType getItemType() {
      return this.itemType;
   }

   public void setItemType(YutGameResult_Action.InstallItemType itemType) {
      this.itemType = itemType;
   }

   public boolean isCreate() {
      return this.create;
   }

   public void setCreate(boolean create) {
      this.create = create;
   }
}
