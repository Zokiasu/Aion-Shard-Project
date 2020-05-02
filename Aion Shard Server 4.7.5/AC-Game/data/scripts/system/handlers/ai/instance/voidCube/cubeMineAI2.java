package ai.instance.voidCube;

import ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;


@AIName("rootmine")
public class cubeMineAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleCreatureSee(Creature creature) {
        checkDistance(this, creature);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        checkDistance(this, creature);
    }

    protected void checkDistance(NpcAI2 ai, Creature creature) {
        Npc owner = ai.getOwner();
        if (creature == null || creature.getLifeStats() == null) {
            return;
        }
        if (creature.getLifeStats().isAlreadyDead()) {
            return;
        }
        if (!owner.canSee(creature)) {
            return;
        }
        if (!owner.getActiveRegion().isMapRegionActive()) {
            return;
        }
        if (!(creature instanceof Player)) {
            return;
        }
        if (MathUtil.isIn3dRange(owner, creature, owner.getAggroRange())) {
            if (GeoService.getInstance().canSee(owner, creature)) {
                    AI2Actions.targetCreature(this, creature);
                    if (getNpcId() == 230410){ //stasis mine
                        SkillEngine.getInstance().applyEffectDirectly(20052, getOwner(), creature, 0); //TODO right skill
                        getOwner().getController().die();
                    }
                    if (getNpcId() == 230409){ //destructive mine
                        AI2Actions.useSkill(this, 19659); //destructive mine TODO right skill
                    }
                    AI2Actions.targetCreature(this, null);
            }
        }
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
    @Override
    protected void handleDied() {

        super.handleDied();
    }
}
