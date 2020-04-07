package instance.abyss;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.Npc;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ever'
 */
@InstanceID(301290000)
public class MirenBarracksInstance extends GeneralInstanceHandler {

    private boolean rewarded = false;
    private int numberBossDie = 0;

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 233710:
            case 233698:
            case 233687:
            case 233709:
            case 233711:
            case 233717:
                numberBossDie++;
                if(numberBossDie == 4){
                    spawn(233719, 526, 845, 199.7f, (byte) 5);
                    numberBossDie = 0;
                }
                break;
            case 233719: // Miren Duke lvl.65
            case 233718: // weakened boss lvl.65
                spawnChests(npc);
                break;
            case 215415: // artifact spawns weak boss
                Npc boss = getNpc(233719);
                if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
                    spawn(233718, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
                    boss.getController().onDelete();
                }
                break;
        }
    }

    private void spawnChests(Npc npc) {
        if (!rewarded) {
            rewarded = true; //safety mechanism
            if (npc.getAi2().getRemainigTime() != 0) {
                long rtime = (600000 - npc.getAi2().getRemainigTime()) / 30000;
                spawn(702298, 478.7917f, 815.5538f, 199.70894f, (byte) 8);
                if (rtime > 1) {
                    spawn(702298, 471, 853, 199f, (byte) 115);
                }
                if (rtime > 2) {
                    spawn(702298, 477, 873, 199.7f, (byte) 109);
                }
                if (rtime > 3) {
                    spawn(702298, 507, 899, 199.7f, (byte) 96);
                }
                if (rtime > 4) {
                    spawn(702296, 548, 889, 199.7f, (byte) 83);
                }
                if (rtime > 5) {
                    spawn(702296, 565, 889, 199.7f, (byte) 76);
                }
                if (rtime > 6) {
                    spawn(702296, 585, 855, 199.7f, (byte) 63);
                }
                if (rtime > 7) {
                    spawn(702296, 578, 874, 199.7f, (byte) 11);
                }
                if (rtime > 8) {
                    spawn(702297, 528, 903, 199.7f, (byte) 30);
                }
                if (rtime > 9) {
                    spawn(702297, 490, 899, 199.7f, (byte) 44);
                }
                if (rtime > 10) {
                    spawn(702297, 470, 834, 199.7f, (byte) 63);
                }
                if (rtime > 11 && npc.getNpcId() == 233719) {
                    spawn(702299, 576.8508f, 836.40424f, 199.7f, (byte) 44);
                }
            }
        }
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 15, 15, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), 527f, 120f, 176f, (byte) 75);
        return true;
    }

    @Override
    public void onPlayerLogOut(Player player){
        TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), 527f, 120f, 176f, (byte) 75);
    }
}
