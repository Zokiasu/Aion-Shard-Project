package ai.events;

import ai.GeneralNpcAI2;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 * @rework Ever'
 */
@AIName("daeva_day_new")
public class DaevaDayAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
            case 831921:
            case 831922:
                super.handleDialogStart(player);
                break;
            default:
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
                break;
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
        env.setExtendedRewardIndex(extendedRewardIndex);
        if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
            return true;
        }
        if (dialogId == DialogAction.SETPRO1.id()) {
            switch (getNpcId()) {
                case 831921:
                case 831922:
                    switch (Rnd.get(1, 2)) {
                        case 1:
                            SkillEngine.getInstance().getSkill(getOwner(), 10825, 1, player).useWithoutPropSkill();
                            break;
                        case 2:
                            SkillEngine.getInstance().getSkill(getOwner(), 10826, 1, player).useWithoutPropSkill();
                            break;
                    }
                    break;
            }
        } else if (dialogId == DialogAction.QUEST_SELECT.id() && questId != 0) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
        }
        return true;
    }
}
