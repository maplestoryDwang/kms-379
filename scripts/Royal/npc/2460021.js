importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);

importPackage(Packages.objects.item);
importPackage(Packages.objects.users);
importPackage(Packages.objects.utils);
importPackage(Packages.constants);
importPackage(Packages.network.models);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.fields);
importPackage(Packages.objects.fields.gameobject);
importPackage(Packages.objects.fields.gameobject.lifes);
importPackage(Packages.database);

var nf = NumberFormat.getInstance();
var point = "";
var point2 = "";
var realName = "";
var event = false;
var sel = -1;
var sel2 = -1;
var sel3 = -1;

var cashnumber = "";
var cancelname = "";
var cancelcash = 0;
var cashcount = 0;
var VIP = false;
var havecashnumber = false;

function start() {
    point = "";
    point2 = "";
    realName = "";
    event = "";
    status = -1;
    sel = -1;
    sel2 = -1;
    sel3 = -1;
    action(1, 0, 0);
}

function CheckVIP() {
    con = DBConnection.getConnection();
    ps = con.prepareStatement("SELECT * FROM donation_request WHERE status = 1 AND account_name = '" + cm.getClient().getAccountName() + "'");
    rs = ps.executeQuery();
    while (rs.next()) {
        cashcount ++
    }
    rs.close();
    ps.close();
    con.close();

    // 총 충전금액 30만원 이상 OR 2회이상 정상충전시 VIP 처리
    if (cm.getClient().getKeyValue("DPointAll") >= 500000 || cashcount >= 3 ) {
        VIP = true;
    } else {
        VIP = false;
    }
}

function CheckCashnumber() {
    con = DBConnection.getConnection();
    ps = con.prepareStatement("SELECT cashnumber FROM accounts WHERE name = '" + cm.getClient().getAccountName() + "'");
    rs = ps.executeQuery();
    while (rs.next()) {
        cashnumber = rs.getString("cashnumber");
    }
    
    if (cashnumber != null && cashnumber != "") {
        havecashnumber = true;
    }
    rs.close();
    ps.close();
    con.close();
}

function CheckRepeat(name, cash) {
    Check = false;
    con = DBConnection.getConnection();
    ps = con.prepareStatement("SELECT real_name, point FROM donation_request WHERE status = 0 AND real_name = '" + name + "' AND point = " + cash);
    rs = ps.executeQuery();
    while (rs.next()) {
        Check = true;
    }
    rs.close();
    ps.close();
    con.close();
    return Check;
}

function action(mode, type, selection) {
    if (mode == 0 && type == 3 && selection == -1 && status >= 5) {
        display();
        return;
    }
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        
        
        if (status == 0) {
            // 첫충전일시 누적금액 0원 설정
            if (cm.getClient().getKeyValue("DPointAll") == null)
                cm.getClient().setKeyValue("DPointAll", "0");

            // 임시 충전 불가처리
            cantcharging = false;

            // VIP 체크
            CheckVIP()
            
            //가상계좌 발급 체크
            CheckCashnumber()

            /*
            if (!VIP) { // VIP가 아닐경우
                cm.sendOk("#fs11##r#e[캐시충전 안내]#n\r\n\r\n#fc0xFF000000#일시적으로 [ #fc0xFFFF3366##eVIP#n#fc0xFF000000# ] 유저가 아니실 경우 충전이 불가능합니다.\r\n추후 다시 시도해주시기 바랍니다.\r\n\r\n캐시 충전이 가능한 날짜는 안내가 힘드니\r\n고객센터를 통해 #e'언제가능한가요?'#n, #e'지금안되나요?'#n 등\r\n#r문의 받지않으니 #e참고#n 부탁드립니다.\r\n\r\n#fc0xFF000000##e최대한 빠른시일내에 충전이 가능하도록 수정하겠습니다.#n\r\n이용에 불편을 드려 죄송합니다.");
                cm.dispose();
                return;
            }
            */

            var msg = "#fs11#강림월드에 캐시 충전을 담당하고 있는 #b셀#k이라고 합니다.\r\n\r\n";
            msg += "#b#L0#[계좌이체]로 캐시 충전을 신청하겠습니다.#l\r\n";
            msg += "#b#L3#[모바일 컬처랜드] 문화상품권으로 캐시 충전 신청하기.#l\r\n";
            //msg += "#L1#제가 신청한 충전 목록을 보고 싶습니다.#l\r\n";
            msg += "#L2#누적 금액 및 충전 내역을 보고 싶습니다.#l";
            
            cm.sendSimple(msg);
        } else if (status == 1) {
            sel = selection;
            if (selection == 0) {
                if (cantcharging) {
                    cm.sendOk("#fs11##r#e[캐시충전 안내]#n\r\n\r\n#fc0xFF000000#일시적으로 충전이 불가능합니다.\r\n추후 다시 시도해주시기 바랍니다.\r\n\r\n캐시 충전이 가능한 날짜는 안내가 힘드니\r\n고객센터를 통해 #e'언제가능한가요?'#n, #e'지금안되나요?'#n 등\r\n#r문의 받지않으니 #e참고#n 부탁드립니다.\r\n\r\n#fc0xFF000000##e최대한 빠른시일내에 충전이 가능하도록 수정하겠습니다.#n\r\n이용에 불편을 드려 죄송합니다.");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getAccountTotalLevel() < 8000) {
                    cm.sendOk("#fs11##fc0xFF000000#계정 내 통합 레벨 (유니온 원정대) 8000 이상만 신청 할 수 있습니다.");
                    cm.dispose();
                    return;
                }
                if (!cm.canDonationRequest()) {
                    cm.sendOk("#fs11##fc0xFF000000#신청 내역 중 미처리건이 있습니다. #e해당 충전건이 완료된 후#n 혹은 #e해당 충전건을 취소#n하고 충전 시도해주시기 바랍니다. #b'제가 신청한 충전 목록을 보고 싶습니다.' 를 통해 취소할 수 있습니다.");
                    cm.dispose();
                    return;
                }
                cm.sendYesNo("#fs11##fc0xFF000000#계좌이체로 캐시 충전을 시작하시겠습니까?\r\n#e신청 후 미입금건이 고의적으로 반복되거나, 장난 신청등은 제재처리 될 수 있습니다.");
				
				
            } else if (selection == 3) {
                if (cantcharging) {
                    cm.sendOk("#fs11##r#e[캐시충전 안내]#n\r\n\r\n#fc0xFF000000#일시적으로 충전이 불가능합니다.\r\n추후 다시 시도해주시기 바랍니다.\r\n\r\n캐시 충전이 가능한 날짜는 안내가 힘드니\r\n고객센터를 통해 #e'언제가능한가요?'#n, #e'지금안되나요?'#n 등\r\n#r문의 받지않으니 #e참고#n 부탁드립니다.\r\n\r\n#fc0xFF000000##e최대한 빠른시일내에 충전이 가능하도록 수정하겠습니다.#n\r\n이용에 불편을 드려 죄송합니다.");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getAccountTotalLevel() < 8000) {
                    cm.sendOk("#fs11##fc0xFF000000#계정 내 통합 레벨 (유니온 원정대) 8000 이상만 신청 할 수 있습니다.");
                    cm.dispose();
                    return;
                }
                if (!cm.canDonationRequest()) {
                    cm.sendOk("#fs11##fc0xFF000000#신청 내역 중 미처리건이 있습니다. #e해당 충전건이 완료된 후#n 혹은 #e해당 충전건을 취소#n하고 충전 시도해주시기 바랍니다. #b'제가 신청한 충전 목록을 보고 싶습니다.' 를 통해 취소할 수 있습니다.");
                    cm.dispose();
                    return;
                }
                cm.sendYesNo("#fs11##fc0xFF000000#[모바일] 컬처랜드 문화상품권으로 캐시 충전을 시작하시겠습니까?\r\n#e신청 후 미입금건이 고의적으로 반복되거나, 장난 신청등은 제재처리 될 수 있습니다.\r\n#r컬처랜드 16자리 핀번호로 이루어진 모바일 문화 상품권으로만 충전할 수 있습니다.");
				
				
            } else if (selection == 1) {
                if (!cm.displayDonationRequest()) {
                    cm.dispose();
                    return;
                }
            } else if (selection == 2) {
                cm.displayDonationLog();
                cm.dispose();
            }

        } else if (status == 2) {
            if (sel == 0) {
                // 시, 분 불러오기
                today = new Date();
                hours = today.getHours();
                minutes = today.getMinutes();

                // 점검안내
                if (hours >= 23 && minutes >= 30 || hours <= 00 && minutes <= 30) {
                    cm.sendOk("#fs11##r#e[점검안내]#k#n\r\n매일 23시30분 ~ 00시30분\r\n계좌 및 은행 점검시간입니다");
                    cm.dispose();
                    return;
                }

                var msg = "#fs11##fc0xFF000000#충전 할 금액을 정확하게 적어주시기 바랍니다.\r\n#r#e※ 10,000원 이상 10,000원 단위 #b#eEX) 50000";
                cm.sendGetNumber(msg, 0, 10000, 2000000);
            } else if (sel == 3) {
                today = new Date();
                hours = today.getHours();
                minutes = today.getMinutes();

                var msg = "#fs11##fc0xFF000000#충전 할 모바일 컬처랜드 문화상품권 총 금액을 정확하게 적어주시기 바랍니다.\r\n#r#e※ 10,000원 이상 10,000원 단위 #b#eEX) 50000";
                cm.sendGetNumber(msg, 0, 10000, 2000000);

            } else if (sel == 1) {
                // 충전취소
                sel2 = selection / 12438;
                checkcancelcash(sel2);
                cm.askDeleteDonationRequest(sel2);
            }
        } else if (status == 3) {
            if (sel == 0) {
                point = selection;
                var msg = "#fs11##fc0xFF000000#입금자명을 정확하게 적어주시기 바랍니다.\r\n#r#e※ 입금통장의 이름을 그대로 기입하시길 바랍니다.";
                cm.sendGetText(msg);
            } else if (sel == 3) {
                point = selection;
                var msg = "#fs11##fc0xFF000000#보내는분의 이메일을 정확하게 적어주시기 바랍니다.\r\n#r#e※ 본인 이메일을 그대로 기입하시길 바랍니다.";
                cm.sendGetText(msg);
            } else if (sel == 1) {
                // 충전취소
                if (!cm.deleteDonationRequest(sel2)) {
                    cm.sendNext("#fs11##fc0xFF000000#이미 충전 완료 처리되어 취소가 불가능합니다.");
                    cm.dispose();
                    return;
                }
                cm.addCustomLog(101, "[캐시충전] 취소 금액 : " + nf.format(cancelcash) + ", 이름 : " + cancelname);
            }
        } else if (status == 4) {
            realName = cm.getText();

	
			if (sel == 0) {
    if (!havecashnumber) {
        cm.sendOk("인터넷 브라우저를 통해 아래 주소로 접속하여, 인증 절차를 거쳐 가상계좌를 발급받은 뒤 다시 충전신청을 시도해주세요\r\n\r\n#bhttps://mapleroyal.cc/cash");
        cm.dispose();
        return;
    }
            if (!realName.matches("^[가-힝]*$")) {
                cm.sendOk("#fs11#입금자 명은 한글만 입력할 수 있습니다.\r\n\r\n입력하신 입금자 명 : "+ realName +"");
                cm.dispose();
                return;
            }

            if (realName.length() < 2 || realName.length() > 3) {
                cm.sendOk("#fs11#입금자명은 한글 2~3글자로 입력해 주세요.\r\n\r\n입력하신 입금자 명 : "+ realName +"");
                cm.dispose();
                return;
            }

            if (CheckRepeat(realName, point)) {
                cm.sendOk("#fs11#현재 입력하신 동일한 입금자명, 금액으로 신청한 타인의 신청내역이 있습니다.\r\n금액을 변경하여 신청해 주시거나 타인의 충전 처리가 완료될 때까지 잠시 기다려주신 후 다시 신청해 주세요.\r\n\r\n#r해당 메시지가 오랜 기간 지속될 시 고객센터로 문의 바랍니다.");
                cm.dispose();
                return;
            }

            if (point % 10000 != 0 || point > 2000000) {
                cm.sendOk("#fs11##r※ 캐시충전은 10,000원 이상 10,000원 단위로 1회 충전 당 최대 2,000,000원까지 가능합니다");
                cm.dispose();
                return;
            }

            var msg = "#fs11##fc0xFF000000##e<입력하신 정보>#n\r\n";
            msg += "#b충전 금액 : " + point;
            msg += "\r\n입금자 명 : " + realName;
            msg += "\r\n\r\n#fs14##e<안내 사항>#fs11#";
            msg += "\r\n#fc0xFF000000#1. 안내된 계좌번호로 #r즉시입금#k을 하셔야합니다.";
            msg += "\r\n#fc0xFF000000#2. 신청된 입금자명, 금액과 실제입금내역이 일치할시 자동으로 충전됩니다. ( 반드시 본인통장이름을 그대로 사용하세요 )";
            msg += "\r\n#fc0xFF000000#3. 계좌번호 불일치시 고객센터로 문의 바랍니다.";
            msg += "\r\n\r\n#k#r위 내용을 어길시 영구정지 처리가 될 수 있습니다.";
            msg += "\r\n#k#b위 내용을 동의하시고 강림 캐시 충전 신청을 진행하시겠다면\r\n#r'동의합니다'#b 를 입력해주세요";
            cm.sendGetText(msg);
			
			
			} else if (sel == 3) {
            if (CheckRepeat(realName, point)) {
                cm.sendOk("#fs11#현재 입력하신 동일한 입금자명, 금액으로 신청한 타인의 신청내역이 있습니다.\r\n금액을 변경하여 신청해 주시거나 타인의 충전 처리가 완료될 때까지 잠시 기다려주신 후 다시 신청해 주세요.\r\n\r\n#r해당 메시지가 오랜 기간 지속될 시 고객센터로 문의 바랍니다.");
                cm.dispose();
                return;
            }

            if (point % 10000 != 0 || point > 2000000) {
                cm.sendOk("#fs11##r※ 문화상품권으로 캐시 충전은 10,000원 이상 10,000원 단위로 1회 충전 당 최대 2,000,000원까지 가능하며 신청하신 금액에 90%만 지급됩니다.");
                cm.dispose();
                return;
            }

            var msg = "#fs11##fc0xFF000000##e<입력하신 정보>#n\r\n";
            msg += "#b충전 금액 : " + point;
            msg += "\r\n이메일 : " + realName;
            msg += "\r\n\r\n#fs14##e<안내 사항>#fs11#";
            msg += "\r\n#fc0xFF000000#1. 안내된 이메일로 #r즉시 상품권 번호#k을 보내주셔야 합니다.";
            msg += "\r\n#fc0xFF000000#2. 신청된 이메일, 금액과 실제 이메일로 보내주신 문화상품권 금액이 일치할시 자동으로 충전됩니다.";
            msg += "\r\n#fc0xFF000000#3. 문화상품권은 보내주신 금액에 #r90%만#k 충전되니 유의하시기 바랍니다.";
            msg += "\r\n\r\n#k#r위 내용을 어길시 영구정지 처리가 될 수 있습니다.";
            msg += "\r\n#k#b위 내용을 동의하시고 강림 캐시 충전 신청을 진행하시겠다면\r\n#r'동의합니다'#b 를 입력해주세요";
            cm.sendGetText(msg);
			}
        } else if (status == 5) {
            if (cm.getText() != "동의합니다") {
                cm.dispose();
                cm.sendOk("#fs11#동의 하지않는다면 도와줄 방법이 없어요\r\n#b동의#k 한다면 다시 '#r#e동의합니다#k#n' 를 입력해주세요");
                return;
            }
			if (sel == 0) {
                display();
			} else if (sel == 3) {
				point2 = point;
				point = point * 0.9;
                display2();
			}
        }
    }
}

function display2() {
    var result = cm.putDonationRequest(point, realName, event);
    if (result == -1) {
        cm.dispose();
        return;
    }
    
    var msg = "#fs11##fc0xFF000000##e<문화상품권 이메일 안내>#n\r\n";

    var cashurl = "https://mapleroyal.cc/cash";
    
    msg += "\r\n#b이메일 : hiticket@etlgr.com#k\r\n\r\n";
    
    msg += "#fc0xFF000000#보내실 상품권 총 금액 : #b" + nf.format(point2) + "#k\r\n";
    msg += "#fc0xFF000000#실제 충전 금액 : #b" + nf.format(point) + "#k\r\n";
    msg += "#fc0xFF000000#본인 이메일 : #e" + realName + "#n\r\n\r\n";
    msg += "#b※ 이메일로 문화상품권 핀번호를 보내주시면됩니다.\r\n";
    msg += "#r※ 잘 못된 핀번호를 보낼시 충전 이용이 제한될 수 있으니 이메일을 보내시기 전 한번 더 확인 후 보내주시기 바랍니다.";
    
    cm.addCustomLog(100, "[캐시충전] 신청 금액 : " + nf.format(point2) + ", 이름 : " + realName);
    
        cm.getPlayer().dropMessage(-22, "캐시 충전 신청이 완료되었습니다. 문화상품권 " + nf.format(point2) + "원을 아래 이메일로 보내주시기 바랍니다.");
        cm.getPlayer().dropMessage(-22, "[이메일] hiticket@etlgr.com");

    cm.sendOk(msg);
    cm.dispose();
}

function display() {
    var result = cm.putDonationRequest(point, realName, event);
    /*
    con = DBConnection.getConnection();
    ps = con.prepareStatement("SELECT * FROM donation_number WHERE accountid = '" + cm.getPlayer().getAccountID() + "'");
    rs = ps.executeQuery();
    while (rs.next()) {
        cashnumber = rs.getString("number");
    }
    rs.close();
    ps.close();
    con.close();
    */
    
    if (result == -1) {
        cm.dispose();
        return;
    }
    
    var msg = "#fs11##fc0xFF000000##e<캐시 충전 계좌 안내>#n\r\n";

    var cashurl = "https://mapleroyal.cc/cash";
    
        chatnumber = cashnumber;
        cashnumber = "\r\n#b입금 계좌 : 광주은행 " + cashnumber + "#k\r\n";  
    
    if (VIP) { // VIP 일경우
        msg += "\r\n#fUI/FarmUI.img/objectStatus/star/whole# #fc0xFFFF3366#[ VIP ] #fc0xFF6600CC#회원 전용 계좌번호가 안내됩니다.\r\n" + cashnumber + "#k#n#k\r\n";
    } else { // VIP가 아닐경우
        msg += "\r\n#fUI/FarmUI.img/objectStatus/star/whole# #fc0xFFFF3366#[ VIP ] #fc0xFF6600CC#회원 전용 계좌번호가 안내됩니다.\r\n" + cashnumber + "#k#n#k\r\n";
        //msg += "#r입금 전 계좌를 항상 확인해주세요.#fc0xFF000000# 수시로 변경됩니다.\r\n\r\n입금 계좌 : #b고객센터#k로 #e문의바랍니다.#n#k\r\n";
    }
    
    msg += "#fc0xFF000000#입금 금액 : #b" + nf.format(point) + "#k\r\n";
    msg += "#fc0xFF000000#입금자 명 : #e" + realName + "#n\r\n\r\n";
    msg += "#r※ 토스, 간편이체, 오픈뱅킹 등으로는 입금이 불가능하며\r\n";
    msg += "#r※ 실명 인증하신 계좌의 은행 앱으로만 이체가 가능합니다.\r\n";
    msg += "#b※ 예시) 우리은행 - > 우리WON뱅킹, 신한은행 - > 신한 쏠";
    
    cm.addCustomLog(100, "[캐시충전] 신청 금액 : " + nf.format(point) + ", 이름 : " + realName);
    
    if (havecashnumber) {
        cm.getPlayer().dropMessage(-22, "캐시 충전 신청이 완료되었습니다. " + nf.format(point) + "원을 입금해주시기 바랍니다.");
        cm.getPlayer().dropMessage(-22, "[입금계좌] " + chatnumber + "");
        //cm.getPlayer().dropMessage(-22, "계좌번호는 20분마다 변경됩니다 계좌번호 불일치시 고객센터로 문의바랍니다.");        cm.getPlayer().dropMessage(-22, "입금계좌 : " + chatnumber);
    } else {
        cm.getPlayer().dropMessage(-22, "캐시 충전 신청이 완료되었습니다. " + nf.format(point) + "원을 입금해주시기 바랍니다.");
        cm.getPlayer().dropMessage(-22, "[입금계좌] " + chatnumber + "");
    }

    cm.sendOk(msg);
    cm.dispose();
}

function checkcancelcash(requestid) {
    con = DBConnection.getConnection();
    ps = con.prepareStatement("SELECT point, real_name FROM donation_request Where id = " + requestid + "");
    rs = ps.executeQuery();
    while (rs.next()) {
        cancelcash = rs.getInt("point");
        cancelname = rs.getString("real_name");
    }
    rs.close();
    ps.close();
    con.close();
}