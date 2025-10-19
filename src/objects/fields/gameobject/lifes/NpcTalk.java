package objects.fields.gameobject.lifes;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class NpcTalk {
   public byte speakerTypeID;
   public byte msgType;
   public byte color;
   public short param;
   public boolean bUseOtherTemplate;
   public int speakerTemplateID;
   public int otherSpeakerTemplateID;
   public String text;
   public boolean bPrev;
   public boolean bNext;
   public int waitTime;
   public List<String> imageList = new ArrayList<>();
   public short lengthMin;
   public short lengthMax;
   public String defaultText;
   public int defaultNumber;
   public int minNumber;
   public int maxNumber;
   public boolean bAngelicBuster;
   public boolean bZeroBeta;
   public byte type;
   public int bValue;
   public int baseColor;
   public int addColor;
   public int baseProb;
   public int itemID;
   public int[] list;

   public NpcTalk(byte speakerTypeID, int speakerTemplateID, boolean bUseOtherTemplate, byte msgType, short param, byte color) {
      this.speakerTypeID = speakerTypeID;
      this.speakerTemplateID = speakerTemplateID;
      this.bUseOtherTemplate = bUseOtherTemplate;
      this.msgType = msgType;
      this.param = param;
      this.color = color;
   }

   public void onSay(PacketEncoder o) {
      o.writeInt(0);
      o.writeInt(0);
      if ((this.param & 4) != 0) {
         o.writeInt(this.speakerTemplateID);
      }

      o.writeMapleAsciiString(this.text);
      o.write(this.bPrev);
      o.write(this.bNext);
      o.writeInt(this.waitTime);
   }

   public void onSayImage(PacketEncoder o) {
      o.write(this.imageList.size());

      for (String s : this.imageList) {
         o.writeMapleAsciiString(s);
      }
   }

   public void onAskYesNo(PacketEncoder o) {
      if ((this.param & 4) != 0) {
         o.writeInt(this.speakerTemplateID);
      }

      o.writeMapleAsciiString(this.text);
   }

   public void onAskText(PacketEncoder o) {
      if ((this.param & 4) != 0) {
         o.writeInt(this.speakerTemplateID);
      }

      o.writeMapleAsciiString(this.text);
      o.writeMapleAsciiString(this.defaultText);
      o.writeShort(this.lengthMin);
      o.writeShort(this.lengthMax);
   }

   public void onAskNumber(PacketEncoder o) {
      o.writeMapleAsciiString(this.text);
      o.writeInt(this.defaultNumber);
      o.writeInt(this.minNumber);
      o.writeInt(this.maxNumber);
   }

   public void onAskMenu(PacketEncoder o) {
      if ((this.param & 4) != 0) {
         o.writeInt(this.speakerTemplateID);
      }

      o.writeMapleAsciiString(this.text);
   }

   public void onAskAvatar(PacketEncoder o) {
      o.write(this.bAngelicBuster);
      o.write(this.bZeroBeta);
      o.writeMapleAsciiString(this.text);
      o.write(this.type);
      o.writeInt(this.bValue);
      o.write(this.baseColor);
      o.write(this.addColor);
      o.write(this.baseProb);
      o.writeInt(this.itemID);
      o.write(this.list.length);

      for (int i : this.list) {
         o.writeInt(i);
      }
   }

   public void onAskCustomMixHairAndProb(PacketEncoder o) {
      o.writeInt(this.bValue);
      o.write(this.bAngelicBuster);
      o.writeInt(this.bZeroBeta ? 2 : 0);
      o.writeInt(this.baseProb);
      o.writeMapleAsciiString(this.text);
   }
}
