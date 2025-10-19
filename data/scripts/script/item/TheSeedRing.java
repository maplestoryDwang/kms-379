package script.item;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.item.*;
import objects.users.achievement.AchievementFactory;
import objects.utils.Randomizer;
import objects.utils.Triple;
import scripting.newscripting.ScriptEngineNPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TheSeedRing extends ScriptEngineNPC {

    public void the_seed_ring() {
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 || getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1
                || getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            self.sayOk("인벤토리 슬롯이 부족합니다. 장비, 소비, 기타 인벤토리 슬롯을 1칸 이상씩 비워주세요.");
            return;
        }
        if (getPlayer().getItemQuantity(itemID, false) <= 0) {
            getPlayer().ban("시드반지 복사버그", true, true, true);
            return;
        }
        int[][] rewards = reward(itemID);
        List<Triple<Integer, Integer, Integer>> itemsByPercent = new ArrayList<>();
        for (int i = 0; i < rewards.length; i++) {
            itemsByPercent.add(new Triple<>(rewards[i][0], rewards[i][1], rewards[i][2]));
        }
        Collections.shuffle(itemsByPercent);

        int selectItem = 0;
        int rewardqty = 1;
        while (true) {
            int Random = Randomizer.nextInt(100);
            Collections.shuffle(itemsByPercent);
            if (itemsByPercent.get(0).getRight() >= Random) {
                selectItem = itemsByPercent.get(0).getLeft();
                rewardqty = itemsByPercent.get(0).getMid();
                break;
            }
        }

        if (selectItem > 0) {
            gainItem(itemID, (short)-1, false, -1, -1, "", getClient(), false);
            if (GameConstants.isTheSeedRing(selectItem)) {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                Item rewardItem = ii.getEquipById(selectItem);
                Equip rewardEquip = null;
                if (rewardItem != null) {
                    rewardEquip = (Equip) rewardItem;
                }
                int seedLevel = rewardSeedLevel(itemID, selectItem);
                rewardEquip.setTheSeedRingLevel((byte) seedLevel);
                AchievementFactory.checkLotteryResultItem(getPlayer(), itemID, rewardEquip.getItemId(), seedLevel);
                short TI = MapleInventoryManipulator.addbyItem(getClient(), rewardEquip, false);
                TheSeedGetItemPacket(selectItem, rewardqty, getPlayer().getItemQuantity(itemID, false) > 0);
            } else {
                MapleInventoryManipulator.addById(getClient(), selectItem, (short) rewardqty, "알리샤의 반지에서 나온 아이템");
                TheSeedGetItemPacket(selectItem, rewardqty, getPlayer().getItemQuantity(itemID, false) > 0);
            }
        }
    }

    public int[][] reward(int item) {
        switch (item) {
            case 2028271: { //9급
                int[][] rewards = {
                        {1113117, 1, 9}, //스위프트
                        {1113118, 1, 9}, //오버패스
                        {1113120, 1, 9}, //리플렉티브
                        {5062500, 5, 5}, //에디큐브
                        {5062010, 5, 10}, //블랙큐브
                        {4001832, 500, 63}, //주문의 흔적
                };
                return rewards;
            }
            case 2028270: { //8급
                int[][] rewards = {
                        {1113117, 1, 9}, //스위프트
                        {1113118, 1, 9}, //오버패스
                        {1113120, 1, 9}, //리플렉티브
                        {5062500, 5, 10}, //에디큐브
                        {5062010, 5, 10}, //블랙큐브
                        {4001832, 500, 58}, //주문의 흔적
                };
                return rewards;
            }
            case 2028269: { //7급
                int[][] rewards = {
                        {1113117, 1, 9}, //스위프트
                        {1113118, 1, 9}, //오버패스
                        {1113120, 1, 9}, //리플렉티브
                        {5062500, 5, 13}, //에디큐브
                        {5062010, 5, 13}, //블랙큐브
                        {4001832, 500, 52}, //주문의 흔적
                };
                return rewards;
            }
            case 2028268:
            case 2028267:
            case 2028266: { //6, 5, 4급
                int[][] rewards = {
                        {1113117, 1, 8}, //스위프트
                        {1113118, 1, 8}, //오버패스
                        {1113120, 1, 8}, //리플렉티브

                        {1113103, 1, 2}, //듀라빌리티
                        {1113100, 1, 2}, //리밋링
                        {1113101, 1, 2}, //헬스컷링
                        {1113102, 1, 2}, //마나컷링

                        {5062500, 10, 10}, //에디큐브
                        {5062010, 20, 10}, //블랙큐브
                        {4001832, 500, 48}, //주문의 흔적
                };
                return rewards;
            }
            case 2028265:
            case 2028264: { //3, 2급
                int[][] rewards = {
                        {1113117, 1, 8}, //스위프트
                        {1113118, 1, 8}, //오버패스
                        {1113120, 1, 8}, //리플렉티브
                        {1113103, 1, 2}, //듀라빌리티
                        {1113100, 1, 2}, //리밋링
                        {1113101, 1, 2}, //헬스컷링
                        {1113102, 1, 2}, //마나컷링

                        {1113098, 1, 2}, //리스트레인트 링
                        {1113099, 1, 2}, //얼티메이덤 링
                        {1113122, 1, 2}, //리스크테이커 링
                        {1113104, 1, 2}, //크리데미지 링
                        {1113119, 1, 2}, //실드스와프 링

                        {5062500, 10, 10}, //에디큐브
                        {5062010, 20, 10}, //블랙큐브
                        {5062503, 20, 10}, //화이트 에디셔널 큐브
                        {2048723, 20, 10}, //영환불

                        {4001832, 500, 48}, //주문의 흔적
                };
                return rewards;
            }
            case 2028263: { //1급
                int[][] rewards = {
                        {1113117, 1, 3}, //스위프트
                        {1113118, 1, 3}, //오버패스
                        {1113120, 1, 3}, //리플렉티브
                        {1113103, 1, 3}, //듀라빌리티
                        {1113100, 1, 3}, //리밋링
                        {1113101, 1, 3}, //헬스컷링
                        {1113102, 1, 3}, //마나컷링
                        {1113098, 1, 3}, //리스트레인트 링
                        {1113099, 1, 3}, //얼티메이덤 링
                        {1113122, 1, 3}, //리스크테이커 링
                        {1113104, 1, 3}, //크리데미지 링
                        {1113119, 1, 3}, //실드스와프 링
                        {1113113, 1, 3}, //웨폰퍼프
                        {1113114, 1, 3}, //웨폰퍼프
                        {1113115, 1, 3}, //웨폰퍼프
                        {1113116, 1, 3}, //웨폰퍼프
                        {1113108, 1, 3}, //링오브썸
                        {1113126, 1, 3}, //리커버스탠스
                        {1113125, 1, 3}, //크라이시스HM
                        {1113109, 1, 3}, //레벨퍼프
                        {1113110, 1, 3}, //레벨퍼프
                        {1113111, 1, 3}, //레벨퍼프
                        {1113112, 1, 3}, //레벨퍼프
                        {1113121, 1, 3}, //버든리프트
                        {1113105, 1, 3}, //크리디펜스
                        {1113106, 1, 3}, //크리쉬프트
                        {1113107, 1, 3}, //스탠스쉬프트
                        {1113123, 1, 3}, //크라이시스H링
                        {1113124, 1, 3}, //크라이시스M링
                        {1113127, 1, 3}, //리커버디펜스

                        {5062500, 20, 2}, //에디큐브
                        {5062010, 20, 2}, //블랙큐브
                        {5062503, 10, 2}, //화이트 에디셔널 큐브
                        {2048753, 10, 2}, //영환불
                        {4001832, 500, 2}, //주문의 흔적
                };
                return rewards;
            }
            case 2028273: { //알리샤의 반지상자
                int[][] rewards = {
                        {1113117, 1, 3}, //스위프트
                        {1113118, 1, 3}, //오버패스
                        {1113120, 1, 3}, //리플렉티브
                        {1113103, 1, 3}, //듀라빌리티
                        {1113100, 1, 3}, //리밋링
                        {1113101, 1, 3}, //헬스컷링
                        {1113102, 1, 3}, //마나컷링
                        {1113098, 1, 3}, //리스트레인트 링
                        {1113099, 1, 3}, //얼티메이덤 링
                        {1113122, 1, 3}, //리스크테이커 링
                        {1113104, 1, 3}, //크리데미지 링
                        {1113119, 1, 3}, //실드스와프 링
                        {1113113, 1, 3}, //웨폰퍼프
                        {1113114, 1, 3}, //웨폰퍼프
                        {1113115, 1, 3}, //웨폰퍼프
                        {1113116, 1, 3}, //웨폰퍼프
                        {1113108, 1, 3}, //링오브썸
                        {1113126, 1, 3}, //리커버스탠스
                        {1113125, 1, 3}, //크라이시스HM
                        {1113109, 1, 3}, //레벨퍼프
                        {1113110, 1, 3}, //레벨퍼프
                        {1113111, 1, 3}, //레벨퍼프
                        {1113112, 1, 3}, //레벨퍼프
                        {1113121, 1, 3}, //버든리프트
                        {1113105, 1, 3}, //크리디펜스
                        {1113106, 1, 3}, //크리쉬프트
                        {1113107, 1, 3}, //스탠스쉬프트
                        {1113123, 1, 3}, //크라이시스H링
                        {1113124, 1, 3}, //크라이시스M링
                        {1113127, 1, 3}, //리커버디펜스
                };
                return rewards;
            }
            default: {
                int[][] rewards = {
                        {1113117, 1, 7}, //스위프트
                        {1113118, 1, 7}, //오버패스
                        {1113120, 1, 7}, //리플렉티브
                        {5062500, 1, 5}, //에디큐브
                        {5062010, 1, 10}, //블랙큐브
                        {4001832, 500, 69}, //주문의 흔적
                };
                return rewards;
            }
        }
    }

    public int rewardSeedLevel(int seedbox, int item) {
        switch (seedbox) {
            case 2028272: //반지10
            case 2028271: //반지9
                return 1;
            case 2028270: //반지8
                if (Randomizer.nextBoolean()) {
                    return 2;
                } else {
                    return 1;
                }
            case 2028269: //반지7
                return Randomizer.rand(1, 3);
            case 2028268: //반지6
                if (item == 1113117 || item == 1113118 || item == 1113120) {
                    return Randomizer.rand(1, 3);
                } else {
                    return 1;
                }
            case 2028267: //반지5
            case 2028266: //반지4
                if (item == 1113117 || item == 1113118 || item == 1113120) {
                    return Randomizer.rand(1, 4);
                } else {
                    return Randomizer.rand(1, 2);
                }
            case 2028265: //반지3
                if (item == 1113117 || item == 1113118 || item == 1113120 || item == 1113103 || item == 1113100 || item == 1113101 || item == 1113102) {
                    return Randomizer.rand(1, 4);
                } else {
                    return Randomizer.rand(1, 2);
                }
            case 2028264: //반지2
                if (item == 1113117 || item == 1113118 || item == 1113120 || item == 1113103 || item == 1113100 || item == 1113101 || item == 1113102) {
                    return Randomizer.rand(1, 4);
                } else {
                    return Randomizer.rand(1, 3);
                }
            case 2028263: //반지1
                return Randomizer.rand(1, 4);
            case 2028273:
                return Randomizer.rand(3, 4);
            default:
                return 1;
        }
    }

    private void TheSeedGetItemPacket(int itemID, int quantity, boolean canReUnboxing) {
        PacketEncoder p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
        p.write(53);
        p.write(0);
        p.write(!canReUnboxing); // 1이면 다시 개봉 안 뜨고 확인창 (남은 아이템 갯수가 0개면 확인창만)
        p.write(0);
        p.writeInt(itemID);
        p.writeInt(quantity);
        p.write(0);
        p.writeZeroBytes(1000);
        getPlayer().send(p.getPacket());
    }
}
