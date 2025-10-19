importPackage(Packages.constants);

var enter = "\r\n";

아이콘 = "#fUI/GuildMark.img/BackGround/00001026/1#"
아이콘2 = "#fUI/GuildMark.img/BackGround/00001028/12#"
아이콘3 = "#fUI/GuildMark.img/BackGround/00001028/9#"

별1 = "#fUI/GuildMark.img/Mark/Pattern/00004014/11#"

Num0 = "#fUI/GuildMark.img/Mark/Letter/00005026/15#"
Num1 = "#fUI/GuildMark.img/Mark/Letter/00005027/15#"
Num2 = "#fUI/GuildMark.img/Mark/Letter/00005028/4#"

T2 = "#fUI/GuildMark.img/Mark/Letter/00005019/4#"
O2 = "#fUI/GuildMark.img/Mark/Letter/00005014/4#"
P2 = "#fUI/GuildMark.img/Mark/Letter/00005015/4#"

T10 = "#fUI/GuildMark.img/Mark/Letter/00005019/15#"
O10 = "#fUI/GuildMark.img/Mark/Letter/00005014/15#"
P10 = "#fUI/GuildMark.img/Mark/Letter/00005015/15#"

랭킹 = "#fUI/Basic.img/theblackcoin/24#";
검정 = "#fc0xFF191919#";
빨강 = "#fc0xFFF15F5F#";
보라 = "#fc0xFF5F00FF#";

강화 = "#fUI/Basic.img/theblackcoin/8#";

var seld = -1;

var items = [
   {
      'itemid': 1005980, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1005980, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1042433, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1042433, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1062285, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1062285, 1]] //실패시 지급되는 아이템
   },

   {
      'itemid': 1005981, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1005981, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1042434, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1042434, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1062286, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1062286, 1]] //실패시 지급되는 아이템
   },

   {
      'itemid': 1005982, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1005982, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1042435, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1042435, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1062287, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1062287, 1]] //실패시 지급되는 아이템
   },

   {
      'itemid': 1005983, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1005983, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1042436, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1042436, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1062288, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1062288, 1]] //실패시 지급되는 아이템
   },

   {
      'itemid': 1005984, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1005984, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1042437, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1042437, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1062289, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[2634472, 10],[4310237, 20000],[4310229, 5000],[4310266, 5000],[4310308, 2000],[4031227, 1000]], 'price': 50000000000, 'chance': 100, //재료, 비용, 확률
      'fail': [[1062289, 1]] //실패시 지급되는 아이템
   },
]

var item;
var isEquip = false;
var canMake = false;

function start() {
   status = -1;
   action(1, 0, 0);
}
function action(mode, type, sel) {
   if (mode == 1) {
      status++;
   } else {
      cm.dispose();
      return;
   }
   var msg = "#fs11#"
   if (status == 0) {
      msg += "제작을 위해 당신의 직업을 알려주세요.\r\n\r\n"
      msg += "#L0#저는 #e전사#n 아이템을 만들고 싶습니다.\r\n"
      msg += "#L1#저는 #e마법사#n 아이템을 만들고 싶습니다.\r\n"
      msg += "#L2#저는 #e궁수#n 아이템을 만들고 싶습니다.\r\n"
      msg += "#L3#저는 #e도적#n 아이템을 만들고 싶습니다.\r\n"
      msg += "#L4#저는 #e해적#n 아이템을 만들고 싶습니다.\r\n"
      cm.sendOk(msg);
   } else if (status == 1) {
      msg += 검정 + "#h0#님, 만들고싶은 아이템을 선택해 주세요.\r\r\n\n\r\n"
      switch (sel) {
         case 0:
            for (i = 0; i < 3; i++) {
               msg += "#fs11##L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
            }
            break;
         case 1:
            for (i = 3; i < 6; i++) {
               msg += "#fs11##L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
            }
            break;
         case 2:
            for (i = 6; i < 9; i++) {
               msg += "#fs11##L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
            }
            break;
         case 3:
            for (i = 9; i < 12; i++) {
               msg += "#fs11##L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
            }
            break;
         case 4:
            for (i = 12; i < 15; i++) {
               msg += "#fs11##L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
            }
            break;
      }
      cm.sendSimple(msg);
   } else if (status == 2) {
      seld = sel;
      item = items[sel];
      isEquip = Math.floor(item['itemid'] / 1000000) == 1;

      canMake = checkItems(item);

      var msg = "선택한 아이템은 다음과 같다.#fs11##b" + enter;
      msg += "아이템 : #i" + item['itemid'] + "##z" + item['itemid'] + "# " + item['qty'] + "개" + enter;

      if (isEquip) {
         if (item['allstat'] > 0)
            msg += "올스탯 : +" + item['allstat'] + enter;
         if (item['atk'] > 0)
            msg += "공격력, 마력 : +" + item['atk'] + enter;
      }

      msg += enter;
      msg += "#fs12##k선택한 아이템을 제작하기 위한 재료다.#fs11##d" + enter + enter;

      if (item['recipes'].length > 0) {
         for (i = 0; i < item['recipes'].length; i++)
            msg += "#i" + item['recipes'][i][0] + "##z" + item['recipes'][i][0] + "# " + item['recipes'][i][1] + "개" + enter;
      }

      if (item['price'] > 0)
         msg += "#i5200002#" + item['price'] + " 메소 (500억)" + enter;

      msg += enter + "#fs12##e제작 성공 확률 : " + item['chance'] + "%#n" + enter + enter;
      msg += "#k제작 성공시 다음과 같은 아이템이 지급된다.#fs11##d" + enter + enter;
      if (item['fail'].length > 0) {
         for (i = 0; i < item['fail'].length; i++)
            msg += "#i" + item['fail'][i][0] + "##z" + item['fail'][i][0] + "# " + item['fail'][i][1] + "개" + enter;
      }
      msg += "#fs12#" + enter;
      msg += canMake ? "#b선택하신 아이템을 만들기 위한 재료들이 모두 모였구만!." + enter + "제작을 하려면 '예'를 눌러주시게나" : "#r선택한 아이템을 만들기 위한 재료들이 모자라지않나?";

      if (canMake) cm.sendYesNo(msg);
      else {
         cm.sendOk(msg);
         cm.dispose();
      }

   } else if (status == 3) {
      canMake = checkItems(item);

      if (!canMake) {
         cm.sendOk("재료가 충분한지 다시 한 번 확인해.");
         cm.dispose();
         return;
      }
      payItems(item);
      if (Packages.server.Randomizer.rand(1, 100) <= item['chance']) {
         gainItem(item);
         cm.sendOk("정상적으로 교환이 되었네.");
         Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(11, "", cm.getPlayer().getName() + "님께서 " + cm.getItemName(item['itemid']) + " 아이템으로 승급하셨습니다."));
      } else {
         //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(11, cm.getPlayer().getName()+"님께서 ["+cm.getItemName(item['itemid'])+"] 제작 실패하셨습니다."));
         cm.sendOk("실패 하였습니다.");
         gainFail(item);
      }
      cm.dispose();
   }
}

function checkItems(i) {
   recipe = i['recipes'];
   ret = true;

   for (j = 0; j < recipe.length; j++) {
      if (!cm.haveItem(recipe[j][0], recipe[j][1])) {
         //cm.getPlayer().dropMessage(6, "fas");
         ret = false;
         break;
      }
   }
   if (ret) ret = cm.getPlayer().getMeso() >= i['price'];

   return ret;
}

function payItems(i) {
   recipe = i['recipes'];
   for (j = 0; j < recipe.length; j++) {
      if (Math.floor(recipe[j][0] / 1000000) == 1)
         Packages.server.MapleInventoryManipulator.removeById(cm.getClient(), Packages.client.inventory.MapleInventoryType.EQUIP, recipe[j][0], 1, false, false);
      else {
         if(recipe[j][1] > 30000) {
            cm.gainItem(recipe[j][0], -recipe[j][1]/2);
            cm.gainItem(recipe[j][0], -recipe[j][1]/2);
         } else {
            cm.gainItem(recipe[j][0], -recipe[j][1]);
         }
      } 
      cm.gainMeso(-i['price'] / recipe.length);
   }
}

function gainItem(i) {
   ise = Math.floor(i['itemid'] / 1000000) == 1;
   if (ise) {
      vitem = Packages.server.MapleItemInformationProvider.getInstance().getEquipById(i['itemid']);
      if (i['allstat'] > 0) {
         vitem.setStr(i['allstat']);
         vitem.setDex(i['allstat']);
         vitem.setInt(i['allstat']);
         vitem.setLuk(i['allstat']);
      }
      if (i['atk'] > 0) {
         vitem.setWatk(i['atk']);
         vitem.setMatk(i['atk']);
      }
      Packages.server.MapleInventoryManipulator.addFromDrop(cm.getClient(), vitem, false);
   } else {
      cm.gainItem(i['itemid'], i['qty']);
   }
}
function gainFail(i) {
   fail = i['fail'];

   for (j = 0; j < fail.length; j++) {
      cm.gainItem(fail[j][0], fail[j][1]);
   }
}


