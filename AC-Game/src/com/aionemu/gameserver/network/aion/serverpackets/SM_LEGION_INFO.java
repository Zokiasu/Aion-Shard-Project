package com.aionemu.gameserver.network.aion.serverpackets;

import java.sql.Timestamp;
import java.util.Map;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class SM_LEGION_INFO extends AionServerPacket {

    /**
     * Legion information *
     */
    private Legion legion;

    /**
     * This constructor will handle legion info
     *
     * @param legion
     */
    public SM_LEGION_INFO(Legion legion) {
        this.legion = legion;
    }

    @Override
    protected void writeImpl(AionConnection con) {
    	PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
        writeS(legion.getLegionName());
        writeC(legion.getLegionLevel());
        writeD(legion.getLegionRank());
        writeH(legion.getDeputyPermission());
        writeH(legion.getCenturionPermission());
        writeH(legion.getLegionaryPermission());
        writeH(legion.getVolunteerPermission());
        writeQ(legion.getContributionPoints());
        writeD(0x00);
        writeD(0x00);
        writeD(0x00); // unk 3.0
        
        /**
         * Get Announcements List From DB By Legion *
         */
        Map<Timestamp, String> announcementList = legion.getAnnouncementList().descendingMap();
        
        /**
         * Show max 7 announcements *
         */
        int i = 0;
        for (Timestamp unixTime : announcementList.keySet()) {
            writeS(announcementList.get(unixTime));
            writeD((int) (unixTime.getTime() / 1000));
            i++;
            if (i >= 7) {
                break;
            }
        }
        
        writeB(new byte[26]);//something like a spacer
    }
}
