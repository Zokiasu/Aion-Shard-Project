package ai.siege.sillus;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Eloann
 */
@AIName("sillus_mercenary_elyos")
public class SillusMercenaryElyosAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getInventory().getItemCountByItemId(186000236) > 0) {
            super.handleDialogStart(player);
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            PacketSendUtility.sendMessage(player, "You need item [item: 186000236] to ask reinforcements");
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (DialogAction.getActionByDialogId(dialogId)) {
            case SETPRO1:
                spawn(272070, 2160.2812f, 1874.4827f, 311.00818f, (byte) 0); // Sniper
                spawn(272070, 2161.8381f, 1879.8955f, 311.00815f, (byte) 0); // Sniper
                spawn(272070, 2161.9878f, 1876.8461f, 311.00815f, (byte) 0); // Sniper
                spawn(272070, 2117.2002f, 1874.6698f, 332.05746f, (byte) 105); // Sniper
                spawn(272070, 2103.5344f, 1859.9352f, 332.03583f, (byte) 105); // Sniper
                spawn(272070, 1999.8871f, 1724.2051f, 318.26813f, (byte) 105); // Sniper
                break;
            case SETPRO2:
                spawn(272080, 2310.5618f, 1891.5841f, 297.4462f, (byte) 0); // Archpriest
                spawn(272080, 2248.4426f, 1856.122f, 279.89194f, (byte) 15); // Archpriest
                spawn(272080, 2199.7979f, 1874.4004f, 290.16302f, (byte) 105); // Archpriest
                spawn(272080, 2009.3325f, 1787.3176f, 316.03876f, (byte) 90); // Archpriest
                spawn(272080, 2024.449f, 1726.4705f, 308.93158f, (byte) 90); // Archpriest
                spawn(272080, 2012.5654f, 1689.2875f, 299.40112f, (byte) 63); // Archpriest
                break;
            case SETPRO3:
                spawn(272036, 1998.9874f, 1790.6902f, 317.125f, (byte) 95); // Cannoneer
                spawn(272036, 2011.273f, 1793.6339f, 317.29504f, (byte) 95); // Cannoneer
                spawn(272036, 2105.16f, 1863.1708f, 317.5952f, (byte) 107); // Cannoneer
                spawn(272036, 2113.8284f, 1872.8129f, 317.79007f, (byte) 107); // Cannoneer
                break;
        }
        return true;
    }
}
