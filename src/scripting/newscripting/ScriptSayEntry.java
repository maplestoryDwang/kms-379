package scripting.newscripting;

import scripting.ScriptMessageType;

public class ScriptSayEntry {
   private final byte nSpeakerTypeID;
   private final int nSpeakerTemplateID;
   private int customSpeakerTemplateID;
   private final ScriptMessageType msgType;
   private final int flag;
   private byte dlgColor;
   private final String msg;
   private int tWait = 0;
   private byte[] bytes = null;

   public ScriptSayEntry(byte nSpeakerTypeID, int nSpeakerTemplateID, ScriptMessageType msgType, int flag, String msg) {
      this.nSpeakerTypeID = nSpeakerTypeID;
      this.nSpeakerTemplateID = nSpeakerTemplateID;
      this.msgType = msgType;
      this.flag = flag;
      this.dlgColor = 1;
      this.msg = msg;
   }

   public ScriptSayEntry(int nSpeakerTemplateID, ScriptMessageType msgType, int flag, String msg) {
      this.nSpeakerTypeID = 4;
      this.nSpeakerTemplateID = nSpeakerTemplateID;
      this.msgType = msgType;
      this.flag = flag;
      this.dlgColor = 1;
      this.msg = msg;
   }

   public ScriptSayEntry(byte nSpeakerTypeID, int nSpeakerTemplateID, ScriptMessageType msgType, int flag, byte dlgColor, String msg) {
      this.nSpeakerTypeID = nSpeakerTypeID;
      this.nSpeakerTemplateID = nSpeakerTemplateID;
      this.msgType = msgType;
      this.flag = flag;
      this.dlgColor = dlgColor;
      this.msg = msg;
   }

   public ScriptSayEntry(int nSpeakerTemplateID, ScriptMessageType msgType, int flag, byte dlgColor, String msg) {
      this.nSpeakerTypeID = 4;
      this.nSpeakerTemplateID = nSpeakerTemplateID;
      this.msgType = msgType;
      this.flag = flag;
      this.dlgColor = dlgColor;
      this.msg = msg;
   }

   public byte getnSpeakerTypeID() {
      return this.nSpeakerTypeID;
   }

   public int getnSpeakerTemplateID() {
      return this.nSpeakerTemplateID;
   }

   public ScriptMessageType getMsgType() {
      return this.msgType;
   }

   public String getMsg() {
      return this.msg;
   }

   public int getFlag() {
      return this.flag;
   }

   public byte getDlgColor() {
      return this.dlgColor;
   }

   public void setDlgColor(byte dlgColor) {
      this.dlgColor = dlgColor;
   }

   public int getCustomSpeakerTemplateID() {
      return this.customSpeakerTemplateID;
   }

   public void setCustomSpeakerTemplateID(int customSpeakerTemplateID) {
      this.customSpeakerTemplateID = customSpeakerTemplateID;
   }

   public int gettWait() {
      return this.tWait;
   }

   public void settWait(int tWait) {
      this.tWait = tWait;
   }

   public byte[] getBytes() {
      return this.bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = bytes;
   }
}
