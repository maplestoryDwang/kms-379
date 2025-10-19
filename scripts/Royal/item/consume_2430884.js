importPackage(Packages.handling.channel);

function start()
{
	status = -1;
	action (1, 0, 0);	
}

function action(mode, type, selection)
{
	if (mode == -1)
	{
		cm.dispose();
		return;
	}
	if (mode == 0)
	{
		status --;
	}
	if (mode == 1)
	{
		status++;
	}

	if (status == 0)
	{

		i = 0;
		for (j = 0; j < ChannelServer.getAllInstances().size(); j++) {
        		i += ChannelServer.getAllInstances().get(j).getPlayerStorage().getAllCharacters().size();
        	}
		txt =   " #fUI/UIWindow5.img/Disguise/backgrnd1##l\r\n\r\n"
			+ "#L00#10성초기화#l#L01#13성초기화#l#L02#15성초기화#l#L03#17성초기화\r\n"
                                    if (cm.getPlayer().isGM())
			{
		//	txt += "#k\r\n#e　 　후원　　　　홍보　　　　템작　　　　키값　#n\r\n"
			+ " #L100#후원보상#l   #L101#홍보보상#l   #L102##r템작하기#d   #L103#키값설정#l\r\n"
			}
		cm.sendSimpleS(txt, 2);
	}

	else if (status == 1)
	{
		cm.dispose();
		switch(selection)
		{

			/* 육성 */
			case 0:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "reset10");
					break;

			case 1:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "reset13");
					break;

			case 2:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "reset15");
					break;

			case 3:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "reset17");
					break;

			case 4:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "딜량체크");
					break;

			case 5:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "추천인");
					break;

			case 6:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "장비백업");
					break;

			case 7:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "템버리기");
					break;

			case 8:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "창고이용");
					break;

			case 9:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "원클큐브");
					break;

			case 10:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "원클환불");
					break;

			case 11:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "잠재부여");
					break;

			case 12:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "직업변경");
					break;

			case 13:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "뽑기이용");
					break;

			case 14:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "택배거래");
					break;

			case 15:
						cm.dispose();
						cm.warp(200000301);
					break;
			case 16:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "닉변하기");
					break;

			case 17:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "분양받기");
					break;

			case 1919:
						cm.dispose();
			cm.openNpcCustom(cm.getClient(), 2510024, "확률뽑기");
					break;
			case 41:
			cm.openNpc(1012001);
			break;

			case 42:
			cm.openNpc(9000161);
			break;

			/* 제작 */
			case 50:
			cm.openNpc(9062000);
			break;

			case 51:
			cm.openNpc(1530541);
			break;
			
			case 52:
			cm.openNpc(1022005);
			break;

			/* 안내 */
			case 60:
			cm.openNpc(1540405);
			break;

			case 61:
			cm.openNpc("npc_9010057");
			break;

			/* 확장 */
			case 70:
			cm.openNpc(9000100);
			break;

			case 71:
			if(cm.getPlayer().getQuestStatus(6500) == 2)
			{
				cm.sendOkS("당신은 이미 모든 것을 갖춘 패션 피플입니다. 더 멋진 아이템을 구해 주머니에 넣어 당신만의 패션을 완성시킨다면 더 멋질 거예요.", 4, 1012117);
				return;
			}

			cm.forceCompleteQuest(6500);
			cm.sendOkS("진정한 멋쟁이라면 보이는 곳뿐만 아니라 보이지 않는 곳, 주머니 안쪽까지 신경을 쓰는 법이죠. 주머니 안에 뭘 넣느냐에 따라 자신의 인상이 달라진다는 것! 잊지 마세요. 보이지 않는 곳까지 세심하게 연출하는 것이야말로 패션의 완성이죠.", 4, 1012117);
			break;

			default:
			cm.getPlayer().dropMessage(1, "현재 이용할 수 없습니다.");
			break;
		}
	}
}
