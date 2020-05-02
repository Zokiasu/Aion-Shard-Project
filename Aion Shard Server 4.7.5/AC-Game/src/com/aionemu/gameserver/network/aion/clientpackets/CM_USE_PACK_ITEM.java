package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ever'
 */
public class CM_USE_PACK_ITEM extends AionClientPacket {

    private int uniqueItemId;

    /**
     * Constructs new instance of <tt>CM_UNPACK_ITEM </tt> packet
     *
     * @param opcode
     */
    public CM_USE_PACK_ITEM(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
        uniqueItemId = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if (player == null) {
            return;
        }
        if (uniqueItemId == 0) {
            return;
        }
        if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        }
        Item item = player.getInventory().getItemByObjId(uniqueItemId);
        if (item == null) {
            return;
        }

        // check use item multicast delay exploit cast (spam)
        if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        }
        item.setPacked(false);
        item.setPersistentState(PersistentState.UPDATE_REQUIRED);
        PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
    }
}
