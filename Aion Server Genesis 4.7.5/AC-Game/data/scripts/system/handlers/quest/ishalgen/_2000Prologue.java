package quest.ishalgen;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class _2000Prologue extends QuestHandler {

	private final static int questId = 2000;

	public _2000Prologue() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(2, questId);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			env.setQuestId(questId);
			QuestService.startQuest(env);
		}
		qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.START) {
			//PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 2));
			qs.setStatus(QuestStatus.REWARD);
			QuestService.finishQuest(env);
			return true;
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 2)
			return false;
		Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		qs.setStatus(QuestStatus.REWARD);
		QuestService.finishQuest(env);
		return true;
	}
}
