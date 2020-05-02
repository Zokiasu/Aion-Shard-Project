package ai.instance.danuarMysticarium;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.services.SiegeService;

/**
 * @author DeathMagnestic
 */
@AIName("legion_mysticarium_portal")
public class LegionMysticariumPortalAI2 extends NpcAI2 {

    @Override
    public void think() {
        ThinkEventHandler.onThink(this);
    }

    @Override
    protected void handleDied() {
        DiedEventHandler.onDie(this);
    }

    @Override
    protected void handleAttack(Creature creature) {
        AttackEventHandler.onAttack(this, creature);
    }

    @Override
    protected boolean handleCreatureNeedsSupport(Creature creature) {
        return AggroEventHandler.onCreatureNeedsSupport(this, creature);
    }

    @Override
    protected void handleDialogStart(Player player) {
        final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(5011);
        Legion playerlegion = player.getLegion();
        if (player.getLegion() != null) {
            if (playerlegion.getLegionId() == loc.getLegionId()) {
                TalkEventHandler.onTalk(this, player);
            }
        } else {
            TalkEventHandler.onFinishTalk(this, player);
        }
    }

    @Override
    protected void handleDialogFinish(Player creature) {
        TalkEventHandler.onFinishTalk(this, creature);
    }

    @Override
    protected void handleFinishAttack() {
        AttackEventHandler.onFinishAttack(this);
    }

    @Override
    protected void handleAttackComplete() {
        AttackEventHandler.onAttackComplete(this);
    }

    @Override
    protected void handleTargetReached() {
        TargetEventHandler.onTargetReached(this);
    }

    @Override
    protected void handleNotAtHome() {
        ReturningEventHandler.onNotAtHome(this);
    }

    @Override
    protected void handleBackHome() {
        ReturningEventHandler.onBackHome(this);
    }

    @Override
    protected void handleTargetTooFar() {
        TargetEventHandler.onTargetTooFar(this);
    }

    @Override
    protected void handleTargetGiveup() {
        TargetEventHandler.onTargetGiveup(this);
    }

    @Override
    protected void handleTargetChanged(Creature creature) {
        super.handleTargetChanged(creature);
        TargetEventHandler.onTargetChange(this, creature);
    }

    @Override
    protected void handleMoveValidate() {
        MoveEventHandler.onMoveValidate(this);
    }

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        MoveEventHandler.onMoveArrived(this);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        CreatureEventHandler.onCreatureMoved(this, creature);
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
    }

    @Override
    protected boolean canHandleEvent(AIEventType eventType) {
        boolean canHandle = super.canHandleEvent(eventType);

        switch (eventType) {
            case CREATURE_MOVED:
                return canHandle
                        || DataManager.NPC_SHOUT_DATA.hasAnyShout(getOwner().getWorldId(), getOwner().getNpcId(), ShoutEventType.SEE);
            case CREATURE_NEEDS_SUPPORT:
                return canHandle
                        && isNonFightingState()
                        && DataManager.TRIBE_RELATIONS_DATA.hasSupportRelations(getOwner().getTribe());
        }
        return canHandle;
    }

    @Override
    public AttackIntention chooseAttackIntention() {
        VisibleObject currentTarget = getTarget();
        Creature mostHated = getAggroList().getMostHated();

        if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
            return AttackIntention.FINISH_ATTACK;
        }

        if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
            onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
            return AttackIntention.SWITCH_TARGET;
        }

        if (getOwner().getObjectTemplate().getAttackRange() == 0) {
            NpcSkillEntry skill = getOwner().getSkillList().getRandomSkill();
            if (skill != null) {
                skillId = skill.getSkillId();
                skillLevel = skill.getSkillLevel();
                return AttackIntention.SKILL_ATTACK;
            }
        } else {
            NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
            if (skill != null) {
                skillId = skill.getSkillId();
                skillLevel = skill.getSkillLevel();
                return AttackIntention.SKILL_ATTACK;
            }
        }
        return AttackIntention.SIMPLE_ATTACK;
    }
}