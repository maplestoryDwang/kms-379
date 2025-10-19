var status;
var select = -1;
var item1 = [1009600, 1159600, 1109600, 1089600, 1079600];
var item2 = [1009601, 1159601, 1109601, 1089601, 1079601];
var item3 = [1009602, 1159602, 1109602, 1089602, 1079602];
var item4 = [1009603, 1159603, 1109603, 1089603, 1079603];
var item5 = [1009604, 1159604, 1109604, 1089604, 1079604];


function start() {
    status = -1;
    action(1, 1, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
    text = "#fs11#받고 싶은 [파멸의 아케인셰이드] 아이템을 선택해주세요#l\r\n\r\n#b#e[ 올스탯 + 15% 데미지 + 15% 보공 + 15% 업횟 19 ]\r\n\r\n#r※ 상자 오픈시 거래가 불가능합니다\r\n※ 잘못 수령시 상자로 복구또한 불가능합니다\r\n\r\n#n#b";
    text += "　< 개별 >　　　　< 세트 >\r\n";
    text += "#L1#전사#l　　　　#L11#전사세트#l\r\n";
    text += "#L2#마법사#l　　　#L12#마법사세트#l\r\n";
    text += "#L3#궁수#l　　　　#L13#궁수세트#l\r\n";
    text += "#L4#도적#l　　　　#L14#도적세트#l\r\n";
    text += "#L5#해적#l　　　　#L15#해적세트#l\r\n\r\n";
    cm.sendSimple(text);

    } else if (status == 1) {
        sel = selection;
        text = "#fs11#받고 싶은 [파멸의 아케인셰이드] 아이템을 선택해주세요#l\r\n\r\n#b#e[ 올스탯 + 15% 데미지 + 15% 보공 + 15% 업횟 19 ]\r\n\r\n#r※ 상자 오픈시 거래가 불가능합니다\r\n※ 잘못 수령시 상자로 복구또한 불가능합니다\r\n\r\n#n#b";
        
        if (sel == 1 || sel == 2 || sel == 3 || sel == 4 || sel == 5) {
            // 개별
            switch (sel) {
                case 1:
                    for (var i = 0; i < item1.length; i++) {
                        text+="#L" + item1[i] + "##i" + item1[i] + "# #z" + item1[i] + "##l\r\n";
                    }
                break;
                
                case 2:
                    for (var i = 0; i < item2.length; i++) {
                        text+="#L" + item2[i] + "##i" + item2[i] + "# #z" + item2[i] + "##l\r\n";
                    }
                break;
                
                case 3:
                    for (var i = 0; i < item3.length; i++) {
                        text+="#L" + item3[i] + "##i" + item3[i] + "# #z" + item3[i] + "##l\r\n";
                    }
                break;
                
                case 4:
                    for (var i = 0; i < item4.length; i++) {
                        text+="#L" + item4[i] + "##i" + item4[i] + "# #z" + item4[i] + "##l\r\n";
                    }
                break;
                
                case 5:
                    for (var i = 0; i < item5.length; i++) {
                        text+="#L" + item5[i] + "##i" + item5[i] + "# #z" + item5[i] + "##l\r\n";
                    }
                break;
            }
        } else if (sel == 11 || sel == 12 || sel == 13 || sel == 14 || sel == 15) {
            // 세트
            
            if (!cm.haveItem(2439958, 5)) {
                cm.sendOk("#fs11#상자 5개를 소지하고있지 않습니다.");
                cm.dispose();
                return;
            }
            
            text = "#fs11##b#e[ 올스탯 + 15% 데미지 + 15% 보공 + 15% 업횟 19 ]\r\n\r\n#r※ 상자 오픈시 거래가 불가능합니다\r\n※ 잘못 수령시 상자로 복구또한 불가능합니다\r\n\r\n#n#b※상자 5개를 소모하여 세트로 지급됩니다#k\r\n";
            switch (sel) {
                case 11:
                    cm.sendYesNo(text + "#fs11#정말 전사 세트로 지급 받으시겠어요?");
                break;
                
                case 12:
                    cm.sendYesNo(text + "#fs11#정말 마법사 세트로 지급 받으시겠어요?");
                break;
                
                case 13:
                    cm.sendYesNo(text + "#fs11#정말 궁수 세트로 지급 받으시겠어요?");
                break;
                
                case 14:
                    cm.sendYesNo(text + "#fs11#정말 도적 세트로 지급 받으시겠어요?");
                break;
                
                case 15:
                    cm.sendYesNo(text + "#fs11#정말 해적 세트로 지급 받으시겠어요?");
                break;
            }
            
        } else {
            text = "#fs11#오류발생";
        }
        
        cm.sendSimple(text);
    } else if (status == 2) {
        if (sel == 1 || sel == 2 || sel == 3 || sel == 4 || sel == 5) {
            // 개별
            selitem = selection;
            
            if (cm.haveItem(selitem, 1)) {
                cm.sendYesNo("#fs11##b#z" + selitem + "#\r\n#k#r이미 해당 아이템을 소지하고있어요#k\r\n그래도 지급 받으시겠어요?");
            } else {
                cm.sendYesNo("#fs11#받을 [파멸의 아케인셰이드] 아이템이 #b#z" + selitem + "##k 맞나요?");
            }
            
        } else if (sel == 11 || sel == 12 || sel == 13 || sel == 14 || sel == 15) {
            // 세트
            
            if (!cm.haveItem(2439958, 5)) {
                cm.sendOk("#fs11#상자 5개를 소지하고있지 않습니다.");
                cm.dispose();
                return;
            }
            if (cm.getInvSlots(1) < 5) {
                cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리 장비칸에 5칸이 안남았구나?!", 2);
                cm.dispose();
                return;
            }
            
            switch (sel) {
                case 11:
                    for (var i = 0; i < item1.length; i++) {
                        cm.gainItem(2439958, -1);
                        cm.gainZeniaItemA(item1[i], "", 0, 0, 15, 15, 15, 0);
                    }
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                    return;
                break;
                
                case 12:
                    for (var i = 0; i < item2.length; i++) {
                        cm.gainItem(2439958, -1);
                        cm.gainZeniaItemA(item2[i], "", 0, 0, 15, 15, 15, 0);
                    }
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                    return;
                break;
                
                case 13:
                for (var i = 0; i < item3.length; i++) {
                        cm.gainItem(2439958, -1);
                        cm.gainZeniaItemA(item3[i], "", 0, 0, 15, 15, 15, 0);
                    }
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                    return;
                break;
                
                case 14:
                for (var i = 0; i < item4.length; i++) {
                        cm.gainItem(2439958, -1);
                        cm.gainZeniaItemA(item4[i], "", 0, 0, 15, 15, 15, 0);
                    }
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                    return;
                break;
                
                case 15:
                for (var i = 0; i < item5.length; i++) {
                        cm.gainItem(2439958, -1);
                        cm.gainZeniaItemA(item5[i], "", 0, 0, 15, 15, 15, 0);
                    }
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                    return;
                break;
            }
            
        } else {
            text = "#fs11#오류발생";
        }
    } else if (status == 3) {
        // 개별
        if (!cm.haveItem(2439958, 1)) {
            cm.sendOk("#fs11#상자를 소지하고있지 않습니다.");
            cm.dispose();
            return;
        }

        if (!cm.canHold(selitem)) {
            cm.sendOk("#fs11#장비칸에 빈 공간이 없습니다.");
            cm.dispose();
            return;
        }

        cm.sendOk("#fs11#지급이 완료되었습니다.");
        cm.gainItem(2439958, -1);
        cm.gainZeniaItemA(selitem, "", 0, 0, 15, 15, 15, 0);//올, 공, 올%, 뎀%, 보공%, 방무%
        cm.dispose();
    }
}