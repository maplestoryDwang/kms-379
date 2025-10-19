package objects.fields.fieldset;

import constants.QuestExConstants;
import database.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import network.game.GameServer;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.users.MapleCharacter;

public abstract class FieldSet {
   protected Properties Var = new Properties();
   public String name;
   public int channel;
   public int minLv;
   public int maxLv;
   public int minMember;
   public int maxMember;
   public int qexKey;
   public String keyValue;
   public String canTimeKey;
   public String bossName;
   public String difficulty;
   public int dailyLimit;
   public int genesisQuestMemberLimit;
   public int genesisQuestId;
   public List<FieldSetInstanceMap> instanceMaps = new ArrayList<>();
   public HashMap<FieldSetInstance, List<Integer>> fInstance = new HashMap<>();

   public FieldSet(String name, int channel) {
      this.name = name;
      this.channel = channel;
   }

   public String getVar(String key) {
      return this.Var.getProperty(key);
   }

   public void setVar(String key, String value) {
      this.Var.put(key, value);
   }

   public abstract void updateFieldSetPS();

   public int getUserCount(FieldSetInstance fim) {
      return this.fInstance.get(fim) != null ? this.fInstance.get(fim).size() : 0;
   }

   public abstract int enter(int var1, int var2);

   public int tryEnterBoss(int CharacterID, boolean isPracticeMode) {
      return this.tryEnterBoss(CharacterID, isPracticeMode, true);
   }

   public int tryEnterBoss(int CharacterID, boolean isPracticeMode, boolean useTier) {
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
      if (nCharacter == null) {
         return -1;
      } else {
         Party party = nCharacter.getParty();
         if (party == null) {
            return 1;
         } else if (party.getLeader().getId() != CharacterID) {
            return 2;
         } else {
            int meberSize = 0;

            for (MapleCharacter pMember : nCharacter.getMap().getCharacters()) {
               Party party1 = pMember.getParty();
               if (party1 != null && party1.getId() == party.getId()) {
                  meberSize++;
               }
            }

            if (meberSize >= this.minMember && meberSize <= this.maxMember) {
               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (pc.getLevel() < this.minLv || pc.getLevel() > this.maxLv) {
                     return 4;
                  }

                  if (!isPracticeMode) {
                     MapleCharacter partyCharacter = gs.getPlayerStorage().getCharacterById(pc.getId());
                     int enterLimit = 1;
                     if (useTier) {
                        enterLimit += pc.getBossTier();
                     }

                     int currentClearCount = partyCharacter.getOneInfoQuestInteger(this.qexKey, this.keyValue);
                     if (enterLimit <= currentClearCount) {
                        return 4;
                     }
                  }
               }

               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (!pc.isOnline()) {
                     return 5;
                  }
               }

               if (!nCharacter.getParty().isPartySameMap()) {
                  return 5;
               } else {
                  for (FieldSetInstanceMap iMap : this.instanceMaps) {
                     int startmap = 0;

                     for (FieldSetInstance f : this.fInstance.keySet()) {
                        if (f.fsim.equals(iMap) && this.getUserCount(f) != 0) {
                           startmap = -1;
                           break;
                        }
                     }

                     if (startmap == 0) {
                        for (int mapID : iMap.instances) {
                           if (GameServer.getInstance(this.channel).getMapFactory().getMap(mapID).getCharactersSize() > 0) {
                              return 6;
                           }
                        }

                        return iMap.instances.get(0);
                     }
                  }

                  return 6;
               }
            } else {
               return 3;
            }
         }
      }
   }

   public int tryEnterPartyQuest(int CharacterID, boolean isPracticeMode, boolean isBoss) {
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
      if (nCharacter == null) {
         return -1;
      } else {
         Party party = nCharacter.getParty();
         if (party == null) {
            return 1;
         } else if (party.getLeader().getId() != CharacterID) {
            return 2;
         } else {
            int meberSize = 0;

            for (MapleCharacter pMember : nCharacter.getMap().getCharacters()) {
               Party party1 = pMember.getParty();
               if (party1 != null && party1.getId() == party.getId()) {
                  meberSize++;
               }
            }

            if (meberSize >= this.minMember && meberSize <= this.maxMember) {
               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (pc.getLevel() < this.minLv || pc.getLevel() > this.maxLv) {
                     return 4;
                  }

                  if (!isPracticeMode && isBoss) {
                     MapleCharacter partyCharacter = gs.getPlayerStorage().getCharacterById(pc.getId());
                     int enterLimit = pc.getBossTier() + 1;
                     int currentClearCount = partyCharacter.getOneInfoQuestInteger(this.qexKey, this.keyValue);
                     if (DBConfig.isGanglim && !Objects.equals(this.difficulty, "헬")) {
                        if (enterLimit <= currentClearCount) {
                           return 4;
                        }
                     } else if (!DBConfig.isGanglim) {
                        boolean isHell = this.difficulty != null && this.difficulty.equals("헬");
                        if (isHell) {
                           String hellKeyValue = this.keyValue;
                           if (this.keyValue != null && !this.keyValue.contains("hell_")) {
                              hellKeyValue = "hell_" + this.keyValue;
                           }

                           currentClearCount = partyCharacter.getOneInfoQuestInteger(this.qexKey, hellKeyValue);
                           if (enterLimit <= currentClearCount) {
                              return 4;
                           }
                        } else if (this.keyValue != null
                           && (
                              this.keyValue.equals("swoo_clear")
                                 || this.keyValue.equals("demian_clear")
                                 || this.keyValue.equals("lucid_clear")
                                 || this.keyValue.equals("will_clear")
                                 || this.keyValue.equals("guardian_angel_slime_clear")
                                 || this.keyValue.equals("dusk_clear")
                                 || this.keyValue.equals("jinhillah_clear")
                                 || this.keyValue.equals("dunkel_clear")
                                 || this.keyValue.equals("clear") && this.qexKey == QuestExConstants.SerniumSeren.getQuestID()
                           )) {
                           boolean single = partyCharacter.getPartyMemberSize() == 1;
                           currentClearCount = partyCharacter.getOneInfoQuestInteger(this.qexKey, this.keyValue + (single ? "_single" : "_multi"));
                           String checkBoss = "";
                           if (this.keyValue.equals("swoo_clear")) {
                              checkBoss = "Swoo";
                           } else if (this.keyValue.equals("demian_clear")) {
                              checkBoss = "Demian";
                           } else if (this.keyValue.equals("lucid_clear")) {
                              checkBoss = "Lucid";
                           } else if (this.keyValue.equals("will_clear")) {
                              checkBoss = "Will";
                           } else if (this.keyValue.equals("guardian_angel_slime_clear")) {
                              checkBoss = "GuardianSlime";
                           } else if (this.keyValue.equals("dusk_clear")) {
                              checkBoss = "Dusk";
                           } else if (this.keyValue.equals("jinhillah_clear")) {
                              checkBoss = "JinHillah";
                           } else if (this.keyValue.equals("dunkel_clear")) {
                              checkBoss = "Dunkel";
                           } else if (this.keyValue.equals("clear") && this.qexKey == QuestExConstants.SerniumSeren.getQuestID()) {
                              checkBoss = "Seren";
                           }

                           int totalEnterLimit = enterLimit
                              + partyCharacter.getOneInfoQuestInteger(
                                 QuestExConstants.WeeklyQuestResetCount.getQuestID(), checkBoss + (single ? "Single" : "Multi")
                              );
                           if (!DBConfig.isHosting) {
                              partyCharacter.dropMessage(1, "현재 입장횟수 : " + currentClearCount + " / 제한횟수 : " + totalEnterLimit);
                           }

                           if (totalEnterLimit <= currentClearCount) {
                              return 4;
                           }
                        } else if (!isHell && enterLimit <= currentClearCount) {
                           return 4;
                        }
                     }
                  }
               }

               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (!pc.isOnline()) {
                     return 5;
                  }
               }

               if (!nCharacter.getParty().isPartySameMap()) {
                  return 5;
               } else {
                  for (FieldSetInstanceMap iMap : this.instanceMaps) {
                     int startmap = 0;

                     for (FieldSetInstance f : this.fInstance.keySet()) {
                        if (f.fsim.equals(iMap) && this.getUserCount(f) != 0) {
                           startmap = -1;
                           break;
                        }
                     }

                     if (startmap == 0) {
                        for (int mapID : iMap.instances) {
                           if (GameServer.getInstance(this.channel).getMapFactory().getMap(mapID).getCharactersSize() > 0) {
                              return 6;
                           }
                        }

                        return iMap.instances.get(0);
                     }
                  }

                  return 6;
               }
            } else {
               return 3;
            }
         }
      }
   }

   public int tryEnterSingleQuest(int CharacterID) {
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
      if (nCharacter != null) {
         Party party = nCharacter.getParty();
         if (party != null) {
            return 1;
         } else if (nCharacter.getLevel() >= this.minLv && nCharacter.getLevel() <= this.maxLv) {
            for (FieldSetInstanceMap iMap : this.instanceMaps) {
               int startmap = 0;

               for (FieldSetInstance f : this.fInstance.keySet()) {
                  if (f.fsim.equals(iMap) && this.getUserCount(f) != 0) {
                     startmap = -1;
                     break;
                  }
               }

               if (startmap == 0) {
                  return iMap.instances.get(0);
               }
            }

            return 3;
         } else {
            return 2;
         }
      } else {
         return -1;
      }
   }

   public int tryEnterFlagRace(int CharacterID) {
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
      if (nCharacter != null) {
         Party party = nCharacter.getParty();
         if (party != null) {
            return 1;
         } else {
            return nCharacter.getLevel() >= this.minLv && nCharacter.getLevel() <= this.maxLv ? this.instanceMaps.get(0).instances.get(0) : 2;
         }
      } else {
         return -1;
      }
   }

   public void incExpAll(int CharacterID, int exp) {
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter Character = gs.getPlayerStorage().getCharacterById(CharacterID);
   }

   public int getReactorState(int fieldid, String name) {
      return 0;
   }

   public abstract String getQuestTime(int var1);

   public abstract void resetQuestTime();

   public void transferFieldAll(int mapId, List<Integer> chars) {
      GameServer gs = GameServer.getInstance(this.channel);

      for (Integer cha : chars) {
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(cha);
         if (nCharacter != null) {
            nCharacter.changeMap(mapId);
         }
      }
   }

   public void broadcastMsg(int type, String name) {
   }

   public abstract int startManually();

   public Field field(int map) {
      return GameServer.getInstance(this.channel).getMapFactory().getMap(map);
   }
}
