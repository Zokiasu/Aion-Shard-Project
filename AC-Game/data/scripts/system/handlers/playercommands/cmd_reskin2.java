package playercommands;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

/**
 * @author Wakizashi, Imaginary
 * @revork Alex
 * @rework Eloann
 */
public class cmd_reskin2 extends PlayerCommand {

    public cmd_reskin2() {
        super("reskin");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params.length != 2) {
            onFail(admin, null);
            return;
        }

        int oldItemId = 0;
        int newItemId = 0;

        try {
            String item = params[0];
            if (item.equals("[item:")) {
                item = params[1];
                Pattern id = Pattern.compile("(\\d{9})");
                Matcher result = id.matcher(item);
                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                }
            } else {
                Pattern id = Pattern.compile("\\[item:(\\d{9})");
                Matcher result = id.matcher(item);

                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                } else {
                    oldItemId = Integer.parseInt(params[0]);
                }
            }
            try {
                String items = params[1];
                if (items.equals("[item:")) {
                    items = params[2];
                    Pattern id = Pattern.compile("(\\d{9})");
                    Matcher result = id.matcher(items);
                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    }
                } else {
                    Pattern id = Pattern.compile("\\[item:(\\d{9})");
                    Matcher result = id.matcher(items);

                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    } else {
                        newItemId = Integer.parseInt(params[1]);
                    }
                }
            } catch (NumberFormatException ex) {
                PacketSendUtility.sendMessage(admin, "1 " + (admin.isGM() ? ex : ""));
                return;
            } catch (Exception ex2) {
                PacketSendUtility.sendMessage(admin, "2 " + (admin.isGM() ? ex2 : ""));
                return;
            }
        } catch (NumberFormatException ex) {
            PacketSendUtility.sendMessage(admin, "3 " + (admin.isGM() ? ex : ""));
            return;
        } catch (Exception ex2) {
            PacketSendUtility.sendMessage(admin, "4 " + (admin.isGM() ? ex2 : ""));
            return;
        }

        if (DataManager.ITEM_DATA.getItemTemplate(newItemId) == null) {
            PacketSendUtility.sendMessage(admin, "Item is incorrect: " + newItemId);
            return;
        }

        int tollPrice = 2;
        List<Item> items = admin.getInventory().getItemsByItemId(oldItemId);
        List<Item> itemnew = admin.getInventory().getItemsByItemId(newItemId);

        if (oldItemId == newItemId) {
            PacketSendUtility.sendMessage(admin, "You cannot reskin the same item :D");
            return;
        }

        //Change the appearance of any item. Gun on the mace, sword, shield and so on
        if (DataManager.ITEM_DATA.getItemTemplate(oldItemId).getItemSlot() != DataManager.ITEM_DATA.getItemTemplate(newItemId).getItemSlot()) {
            PacketSendUtility.sendMessage(admin, "You can't :D");
            return;
        }

        if (itemnew.isEmpty() && !admin.isGM()) {
            reskin(admin, tollPrice, newItemId, items);
            return;
        }

        if (items.isEmpty()) {
            PacketSendUtility.sendMessage(admin, "Old item Not Found in inventory.");
            return;
        }

        Iterator<Item> iter = items.iterator();
        Item item = iter.next();

        if (!admin.isGM() && !itemnew.isEmpty()) {
            reskin(admin, tollPrice, newItemId, items);
        } else {
            reskin(admin, tollPrice, newItemId, items);
        }
    }

    public void reskin(final Player admin, final int toll, final int itemId, final List<Item> items) {
        final long tolls = admin.getClientConnection().getAccount().getToll();
        RequestResponseHandler responseHandler = new RequestResponseHandler(admin) {
            @Override
            public void acceptRequest(Creature p2, Player p) {
                if (tolls < toll) {
                    PacketSendUtility.sendMessage(admin, "You don't have enought Shard Coin (" + tolls + "). You need : " + toll + " Shard Coin.");
                    return;
                }
                updateToll(admin, tolls - toll);
                Iterator<Item> iter = items.iterator();
                Item item = iter.next();
                item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(itemId));
                admin.getInventory().decreaseByItemId(itemId, 1);
                PacketSendUtility.sendMessage(admin, "Skin successfully changed!");
                PacketSendUtility.sendMessage(admin, DataManager.ITEM_DATA.getItemTemplate(itemId).getName() + " was deleted in your inventory.");
                PacketSendUtility.sendMessage(admin, "For changing the skin, you have use " + toll + " Shard Coins!");
            }

            @Override
            public void denyRequest(Creature p2, Player p) {

            }
        };
        boolean requested = admin.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(admin, new SM_QUESTION_WINDOW(902247, 0, 0, "In your inventory, there is no New Item. To change the look, for which you have not, you need to " + toll + " Shard Coin. On your account, you have : " + tolls + ". Want to reskin the item ?"));
        }
    }

    public void updateToll(Player player, long newToll) {
        Connection con = null;

        try {
            final Player player1 = player;
            String LOGIN_DATABASE = GSConfig.LOGINSERVER_NAME;
            DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE " + LOGIN_DATABASE + ".account_data SET " + LOGIN_DATABASE + ".account_data.toll = ? WHERE " + LOGIN_DATABASE + ".account_data.name = ?");
            stmt.setInt(1, (int) newToll);
            stmt.setString(2, player1.getAcountName());
            stmt.execute();
            stmt.close();

        } catch (Exception e) {
            PacketSendUtility.sendMessage(player, "update toll fail");
            return;
        }
    }

    @Override
    public void onFail(Player admin, String message) {
        PacketSendUtility.sendMessage(admin, "OldItem is item you want reksin");
        PacketSendUtility.sendMessage(admin, "NewItem is item you use to reksin");
        PacketSendUtility.sendMessage(admin, "syntax //reskin <Link@ | Old Item ID> <Link@ | New Item ID>");
    }
}
