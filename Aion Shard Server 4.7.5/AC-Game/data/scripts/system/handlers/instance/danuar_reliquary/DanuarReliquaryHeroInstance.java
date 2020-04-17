package instance.danuar_reliquary;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import java.util.Map;
import java.util.Set;

@InstanceID(301360000)
public class DanuarReliquaryHeroInstance extends GeneralInstanceHandler {
	
	private Map<Integer, StaticDoor> doors;
	private int guardGraendalKilled = 0;
	private int IllusionGraendalKilled = 0;
	private int skillId;
	
	@Override
	public void onEnterInstance(Player player) {
		super.onInstanceCreate(instance);
		spawn(284447, 256.5698f, 257.8559f, 241.9354f, (byte) 0); 		//Spawn Damage Circle - Invisible (Blue Water)
		skillId = 8698;
		if(player.getLastMapId() == 600100000) {
			SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
		}
    }
		
	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			 case 284377: //Novun
			 case 284378: //Lapilima
			 case 284379: //Obscura
				guardGraendalKilled ++;
				if (guardGraendalKilled == 3) {
				  spawn(234690, 256.52f, 257.87f, 241.78f, (byte) 90); //Vengeful Modor
				}
				despawnNpc(npc);
				break;
			 case 855244: //Clone
				despawnNpc(npc);
				despawnNpc(getNpc(855247));
				despawnNpc(getNpc(855248));
				despawnNpc(getNpc(855249));
				IllusionGraendalKilled ++;
				if (IllusionGraendalKilled == 1) {
					switch ((int)Rnd.get(1, 4)) {
						case 1:
							spawn(855245, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855247, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 2:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855245, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 3:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855245, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 4:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855249, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855245, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
					}
				}
				break;
			 case 855245: //clone
				despawnNpc(npc);
				despawnNpc(getNpc(855247));
				despawnNpc(getNpc(855248));
				despawnNpc(getNpc(855249));
				IllusionGraendalKilled ++;
				if (IllusionGraendalKilled == 2) {
					switch ((int)Rnd.get(1, 4)) {
						case 1:
							spawn(855246, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855247, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 2:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855246, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 3:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855246, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
						case 4:
							spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
							spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
							spawn(855249, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
							spawn(855246, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
							break;
					}
				}
				break;
			 case 855246: //clone
				IllusionGraendalKilled ++;
				if (IllusionGraendalKilled == 3) {
					spawn(234691, 256.5205f, 257.8747f, 241.7870f, (byte) 90);
				}
				despawnNpc(npc);
				despawnNpc(getNpc(855247));
				despawnNpc(getNpc(855248));
				despawnNpc(getNpc(855249));
				break;
			 case 855247: //clone
			 case 855248: //clone
			 case 855249: //clone
			 case 284380: //bodyguard
			 case 284381: //reaper
			 case 284382: //drake
			 case 284659: //jotun
			 case 284660: //lapilima
			 case 284661: //obscura
			 case 284662: //weakened modor guardian
			 case 284663: //ghost
			 case 284664: //drake
				despawnNpc(npc);
				break;
			 case 234691: //Crazed Modor
			 	sendMsg(1401893);
				if (Rnd.get(1, 100) < 30) {
					spawn(802184, 256.33f, 267.22f, 241.84f, (byte) 0);
				}
				despawnNpc(getNpc(284377));
				despawnNpc(getNpc(284378));
				despawnNpc(getNpc(284379));
				despawnNpc(getNpc(284380));
				despawnNpc(getNpc(284381));
				despawnNpc(getNpc(284382));
				despawnNpc(getNpc(284659));
				despawnNpc(getNpc(284660));
				despawnNpc(getNpc(284661));
				despawnNpc(getNpc(284662));
				despawnNpc(getNpc(284663));
				despawnNpc(getNpc(284664));
				spawn(730843, 256.2082f, 250.2959f, 241.8779f, (byte) 30);
				return;
		}
	}

	@Override
	public void onInstanceDestroy() {
		doors.clear();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(skillId);
	}

	@Override
	public void onPlayerLogOut(Player player) {
		player.getEffectController().removeEffect(skillId);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}