package ai.instance.shugoImperialTomb;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

@AIName("awakened_guardian_tomb_ghost")
public class AwakenedGuardianTombGhostAI2 extends AggressiveNpcAI2 {
	
	Npc towerCenter;
	
	@Override
	protected  void handleDeactivate() {
	}
	
	@Override
	public int modifyDamage(int damage) {
		return 500;
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		towerCenter = getPosition().getWorldMapInstance().getNpc(831130);
		AI2Actions.targetCreature(AwakenedGuardianTombGhostAI2.this, towerCenter);
		getAggroList().addHate(towerCenter, 100000);
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}
	
	@Override
	protected void handleActivate() {
		super.handleActivate();
		towerCenter = getPosition().getWorldMapInstance().getNpc(831130);
		AI2Actions.targetCreature(AwakenedGuardianTombGhostAI2.this, towerCenter);
	}
}
