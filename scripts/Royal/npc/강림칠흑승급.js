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
      'itemid': 1012643, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1012632, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1012632, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1022289, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1022278, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1022278, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1032318, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1032316, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1032316, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1122431, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1122430, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1122430, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1132311, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1132308, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1132308, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1672078, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1672082, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1672082, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1113307, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1113306, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1113306, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1182286, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1182285, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 50, //재료, 비용, 확률
      'fail': [[1182285, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1162078, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1162080, 1], [1162081, 1], [1162082, 1], [1162083, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 100, //재료, 비용, 확률
      'fail': [[1162078, 1]] //실패시 지급되는 아이템
   },
   {
      'itemid': 1190564, 'qty': 1, 'allstat': 0, 'atk': 0, //템id, 수량, 추가스탯, 추가공격력, 스탯은 장비템에만 적용
      'recipes': [[1190555, 1], [1190556, 1], [1190557, 1], [1190558, 1], [1190559, 1], [4310237, 5000], [4009005, 2000], [4033172, 500], [4001878, 2000], [4031227, 1000], [4001715, 600]], 'price': 0, 'chance': 100, //재료, 비용, 확률
      'fail': [[1190564, 1]] //실패시 지급되는 아이템
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
      itemsId = 1;
      msg += 검정 + "#h0#님, 만들고싶은 아이템을 선택해 주세요.#fc0xFF990033#\r\n\r\n"
      for (i = 0; i < items.length; i++) {
         msg += "#L" + i + "##i" + items[i]['itemid'] + "# #z" + items[i]['itemid'] + "# " + items[i]['qty'] + "개" + enter;
      }
      cm.sendSimple(msg);
   } else if (status == 1) {
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
         msg += "#i5200002#" + item['price'] + " 메소 (150억)" + enter;

      msg += enter + "#fs12##e제작 성공 확률 : " + item['chance'] + "%#n" + enter + enter;
      msg += "#k제작 실패시 다음과 같은 아이템이 지급된다.#fs11##d" + enter + enter;
      if (item['fail'].length > 0) {
         for (i = 0; i < item['fail'].length; i++)
            msg += "#i" + item['fail'][i][0] + "##z" + item['fail'][i][0] + "# " + item['fail'][i][1] + "개" + enter;
      }
      msg += "#fs12#" + enter;
      msg += canMake ? "#b선택하신 아이템을 만들기 위한 재료들이 모두 모였구만!." + enter + "제작을 하려면 '예'를 눌러주시게나" : "#r선택한 아이템을 만들기 위한 재료들이 모자라지않나?";
      if (canMake) { cm.sendYesNo(msg); }
      else {
         cm.sendOk(msg);
         cm.dispose();
      }

   } else if (status == 2) {
      canMake = checkItems(item);

      if (!canMake) {
         cm.sendOk("재료가 충분한지 다시 한 번 확인해.");
         cm.dispose();
         return;
      }
      payItems(item);
      if (Packages.objects.utils.Randomizer.rand(1, 100) <= item['chance']) {
         gainItem(item);
         cm.sendOk("정상적으로 교환이 되었네.");
         cm.worldGMMessage(21, "[혼돈칠흑] " + cm.getPlayer().getName()+"님께서 ["+cm.getItemName(item['itemid'])+"] 제작에 성공하였습니다!");
      } else {
         cm.sendOk("승급에 실패하였습니다.");
         cm.worldGMMessage(21, "[혼돈칠흑] " + cm.getPlayer().getName()+"님께서 ["+cm.getItemName(item['itemid'])+"] 제작에 실패하였습니다.");
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
         Packages.objects.item.MapleInventoryManipulator.removeById(cm.getClient(), Packages.objects.item.MapleInventoryType.EQUIP, recipe[j][0], 1, false, false);
      else cm.gainItem(recipe[j][0], -recipe[j][1]);
           cm.gainMeso(-1000000);
   }
}

function gainItem(i) {
   ise = Math.floor(i['itemid'] / 1000000) == 1;
   if (ise) {
      vitem = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(i['itemid']);
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
      Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(), vitem, false);
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


