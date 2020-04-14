package playercommands;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class goldpack extends PlayerCommand {

    public goldpack() {
        super("goldpack");
    }

    @Override
    public void execute(Player player, String... params) {
        Connection con = null;
        try {
            final Player player1 = player;
            String LOGIN_DATABASE = GSConfig.LOGINSERVER_NAME;
            DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

            PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM " + LOGIN_DATABASE + ".account_data WHERE " + LOGIN_DATABASE + ".account_data.name = ?");
            stmt1.setString(1, player1.getAcountName());
            ResultSet resultSet = stmt1.executeQuery();

            if(resultSet.next()) {
                if(resultSet.getInt("membership") > 0) {
                    Timestamp deletionDate = resultSet.getTimestamp("expire");
                    Date date = new Date(deletionDate.getTime());
                    PacketSendUtility.sendMessage(player, "You are currently under the goldpack effect.");
                    PacketSendUtility.sendMessage(player, "Your goldpack will last until " + shortDateFormat.format(date));
                } else {
                    PacketSendUtility.sendMessage(player, "You are not under the goldpack effect.");
                }
            }
            resultSet.close();
            stmt1.close();

        } catch (Exception e) {
            log.error("Cmd goldpack fail");
            return;
        }
    }

    @Override
    public void onFail(Player player, String message) {
        // TODO Auto-generated method stub
    }
}
