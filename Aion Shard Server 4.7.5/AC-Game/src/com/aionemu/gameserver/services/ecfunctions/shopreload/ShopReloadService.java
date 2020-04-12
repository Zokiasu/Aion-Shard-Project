package com.aionemu.gameserver.services.ecfunctions.shopreload;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Kill3r
 */
public class ShopReloadService implements ShopReloadStruct{

    private static final Logger log = LoggerFactory.getLogger(ShopReloadService.class);
    private static final ShopReloadService Service = new ShopReloadService();
    public List<Player> playersToArena = new FastList<Player>();
    public boolean Initialized = false;

    public static ShopReloadService getInstance(){
        return Service;
    }

    @Override
    public ScheduledFuture autoReload(){
        Initialized = true;
        log.info("Starting Shop Reload Service...");
        return ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                announceEveryOne("Shop", "ShopReload"); // later make config for this
                rechargeDB();
            }
        }, 10000, 10000);  // also config for delay Timmer
    }

    public void announceEveryOne(final String senderName,final String Message){
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player object) {
                PacketSendUtility.sendSys2Message(object, senderName, Message);
            }
        });
    }

    public void rechargeDB() {
        try {
            DB.select("SELECT object_id, item_id, item_count, player_name FROM myshop", new ParamReadStH() {

                @Override
                public void setParams(PreparedStatement stmt) throws SQLException {
                    stmt.setInt(1, player.getObjectId());
                }

                @Override
                public void handleRead(ResultSet rset) throws SQLException {
                    while (rset.next()) {
                        final int id = rset.getInt("object_id");
                        int itemId = rset.getInt("item_id");
                        int item_count = rset.getInt("item_count");
                        String player_name = rset.getString("player_name");

                        SystemMailService.getInstance().sendMail("AionShard", player_name, "ShardShop", "", itemId, item_count, 0, 2);
                        //SystemMailService.getInstance().sendMail(sender, player.getName(), title, message, item, count, kinah, letterType);

                        DB.insertUpdate("DELETE FROM player_shop WHERE object_id = ?", new IUStH() {
                            @Override
                            public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                                ps.setInt(1, id);
                                ps.execute();
                            }
                        });
                    }
                }

            });
        } catch (Exception ex) {
            PacketSendUtility.sendMessage(player, "Only numbers are allowed");
        }
    }
}