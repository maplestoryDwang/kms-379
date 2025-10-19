importPackage(Packages.server);
importPackage(Packages.database);
importPackage(Packages.objects.utils);
importPackage(Packages.objects.item);
importPackage(Packages.scripting);
importPackage(Packages.network.game);
importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);

var status = -1;
var tempsel = 0;
var seld = 0;

function start() {
    status = -1;
    action (1, 1, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var send = "#fs11#원하시는 기능을 선택해주세요.\r\n";
        send += "#L0#BGM 현재맵(현재채널) 대기열 확인#l\r\n";
        send += "#L1#BGM 현재맵(현재채널) 대기열 등록(20억 메소)#l\r\n";
		
		/*
        if (cm.getPlayer().isGM()) {
        send += "\r\n\r\n";
        send += "#r#e   <GM Menu>#k#n\r\n";
        send += "#L2#BGM 현재맵(전체채널) 대기열 등록#l\r\n";
        send += "#L3#BGM 현재맵(전체채널) 대기열 초기화#l";
        }
		*/
        cm.sendSimple(send);
    }
    if (status == 1) {
        seld = selection;
        if (seld == 0) {
            var mapmusic = cm.getMapMusicList();
            if (mapmusic.size() == 0) {
                cm.sendOk("#fs11#현재 노래 대기열이 없습니다.");
                cm.dispose();
                return;
            }
            var musiclist = "#fs11#현재 대기열에 등록된 BGM 목록입니다.\r\n#r※ 약 5분 재생후 다음 곡으로 넘어갑니다.#k\r\n\r\n";
            for (var i = 0; i < mapmusic.size(); i++) {
                musiclist += mapmusic.get(i) + "\r\n";
            }
            cm.sendOk(musiclist);
            cm.dispose();
            return;
        }
        if (seld == 1) {
            var mapmusic = cm.getMapMusicList();
            if (mapmusic.size() > 8) {
                cm.sendOk("#fs11#대기열은 최대 8개 까지 등록 가능합니다.");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4001715, 20)) {
                cm.sendOk("#fs11##i4001715# #z4001715# 20개가 부족합니다.");
                cm.dispose();
                return;
            }
            var musiclist = cm.getAllSound();
            var musicask = "#fs11#원하는 BGM을 선택해주세요.\r\n\r\n";
            musicask += "#b< Latale Music >#k\r\n";
            for (var i = 0; i < musiclist.size(); i++) {
                if (musiclist.get(i) == "[테일즈위버] Reminiscence")
                    musicask += "\r\n\r\n#b< Other Music >#k\r\n";
                musicask += "#L" + i + "#" + musiclist.get(i) + "#l\r\n";
            }
            cm.sendSimple(musicask);
        }
		/*
        if (seld == 2) {
            var mapmusic = cm.getMapMusicList();
            if (mapmusic.size() > 8) {
                cm.sendOk("#fs11#대기열은 최대 8개 까지 등록 가능합니다.");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4001715, 20)) {
                cm.sendOk("#fs11##i4001715# #z4001715# 20개가 부족합니다.");
                cm.dispose();
                return;
            }
            var musiclist = cm.getAllSound();
            var musicask = "#fs11#원하는 BGM을 선택해주세요.\r\n\r\n";
            musicask += "#b< Latale Music >#k\r\n";
            for (var i = 0; i < musiclist.size(); i++) {
                if (musiclist.get(i) == "[테일즈위버] Reminiscence")
                    musicask += "\r\n\r\n#b< Other Music >#k\r\n";
                musicask += "#L" + i + "#" + musiclist.get(i) + "#l\r\n";
            }
            cm.sendSimple(musicask);
        }
        if (seld == 3) {
            cm.askYesNo("#fs11#정말 BGM 현재맵(전체채널) 대기열을 초기화 하시겠습니까?", GameObjectType.Npc, ScriptMessageFlag.None);
        }
		*/
    }
    if (status == 2) {
        if (seld == 1) {
            var musiclist = cm.getAllSound();
            if (selection >= 0 && selection < musiclist.size()) {
                tempsel = selection;
                cm.askYesNo("#fs11#선택하신 노래가 " + musiclist.get(selection) + " 맞나요?\r\n\r\n#r#i4001715# #z4001715# 20개#b가 필요한데 진행하시겠어요?", GameObjectType.Npc, ScriptMessageFlag.None);
            }
        }
        if (seld == 2) {
            var musiclist = cm.getAllSound();
            if (selection >= 0 && selection < musiclist.size()) {
                tempsel = selection;
                cm.askYesNo("#fs11#선택하신 노래가 " + musiclist.get(selection) + " 맞나요?", GameObjectType.Npc, ScriptMessageFlag.None);
            }
        }
        if (seld == 3) {
            for (i = 0; i < GameServer.getAllInstances().size(); i++) {
                channel = GameServer.getAllInstances().get(i)
                channel.getMapFactory().getMap(cm.getPlayer().getMapId()).clearMusicList();
            }
            cm.dispose();
            cm.sendOk("#fs11#현재맵(전체채널) 리스트 초기화 완료");
        }
    }
    if (status == 3) {
        if (seld == 1) {
            if (!cm.haveItem(4001715, 20)) {
                cm.sendOk("#fs11##i4001715# #z4001715# 20개가 부족합니다.");
                cm.dispose();
                return;
            }
            cm.gainItem(4001715, -20);
            cm.addMapMusic(tempsel);
            var musiclist = cm.getAllSound();
            cm.sendOk("#fs11#" + musiclist.get(tempsel) + " 곡이\r\n현재맵(현재채널) 대기열에 등록되었습니다.");
            cm.dispose();
        }
        if (seld == 2) {
            cm.addServerMapMusicServer(tempsel);
            var musiclist = cm.getAllSound();
            cm.sendOk("#fs11#" + musiclist.get(tempsel) + " 곡이\r\n현재맵(전체채널) 대기열에 등록되었습니다.");
            cm.dispose();
        }
    }
}
