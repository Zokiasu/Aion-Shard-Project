package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.WarehouseService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.mail.SystemMailService;
import com.aionemu.gameserver.utils.PacketSendUtility;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GoldPackAction")
public class GoldPackAction extends AbstractItemAction {

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        return true;
    }

    @Override
    public void act(Player player, Item parentItem, Item targetItem) {
        
        if(parentItem.getItemId() == 188710151){
            PacketSendUtility.sendMessage(player, "Ah que coucou.");
            return;
        }
        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            return;
        }
        ItemTemplate itemTemplate = parentItem.getItemTemplate();

        Connection con = null;

        try {
            final Timestamp deletionDate = new Timestamp(System.currentTimeMillis());
            final Player player1 = player;
            String LOGIN_DATABASE = GSConfig.LOGINSERVER_NAME;
            Calendar cal = Calendar.getInstance();

            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE " + LOGIN_DATABASE +".account_data set "+ LOGIN_DATABASE +".account_data.membership = ? where " + LOGIN_DATABASE + ".account_data.name = ?");
            stmt.setInt(1, 1);
            stmt.setString(2, player1.getAcountName());
            stmt.execute();
            stmt.close();

            PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM " + LOGIN_DATABASE + ".account_data WHERE " + LOGIN_DATABASE + ".account_data.name = ?");
            stmt1.setString(1, player1.getAcountName());
            ResultSet resultSet = stmt1.executeQuery();

            if(resultSet.next()) {
                if(resultSet.getTimestamp("expire") != null) {
                    cal.setTime(resultSet.getTimestamp("expire"));
                    cal.add(Calendar.DAY_OF_WEEK, 30);
                    deletionDate.setTime(cal.getTime().getTime());
                } else {
                    cal.setTime(deletionDate);
                    cal.add(Calendar.DAY_OF_WEEK, 30);
                    deletionDate.setTime(cal.getTime().getTime());
                }
            } else {
                cal.setTime(deletionDate);
                cal.add(Calendar.DAY_OF_WEEK, 30);
                deletionDate.setTime(cal.getTime().getTime());
            }
            resultSet.close();
            stmt1.close();

            PreparedStatement stmt2 = con.prepareStatement("UPDATE " + LOGIN_DATABASE +".account_data set "+ LOGIN_DATABASE +".account_data.expire = ? where " + LOGIN_DATABASE + ".account_data.name = ?");
            stmt2.setTimestamp(1, deletionDate);
            stmt2.setString(2, player1.getAcountName());
            stmt2.execute();
            stmt2.close();

            Date date = new Date(deletionDate.getTime());
            DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            PacketSendUtility.sendMessage(player, "Congratulations, you have just activated a new gold pack.");
            PacketSendUtility.sendMessage(player, "Your next gold pack will need to be activated on : " + shortDateFormat.format(date));

        } catch (Exception e) {
            return;
        }

        SystemMailService.getInstance().sendMail("GoldPack", player.getName(), "GoldPack Reward", "", 164002225, (long) 1, (long) 0, LetterType.BLACKCLOUD);
        SystemMailService.getInstance().sendMail("GoldPack", player.getName(), "GoldPack Reward", "", 169610093, (long) 1, (long) 0, LetterType.BLACKCLOUD);

        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);
    }
}
