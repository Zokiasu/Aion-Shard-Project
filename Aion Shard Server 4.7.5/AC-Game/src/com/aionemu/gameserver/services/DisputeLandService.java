package com.aionemu.gameserver.services;

import java.util.Calendar;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DISPUTE_LAND;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneAttributes;

/**
 * @author Source
 * @rework Eloann
 */
public class DisputeLandService {

    private boolean active;
    private FastList<Integer> worlds = new FastList<Integer>();
    private static /*final*/ int chance;// = CustomConfig.DISPUTE_RND_CHANCE;
    private static final String rnd = CustomConfig.DISPUTE_RND_SCHEDULE; //11h to 16h
    private static final String rnd2 = CustomConfig.DISPUTE_RND2_SCHEDULE; //21h to 2h
    private static final String rnd3 = CustomConfig.DISPUTE_RND3_SCHEDULE; //2h to 7h
    private static final String rnd4 = CustomConfig.DISPUTE_RND4_SCHEDULE; //7h to 11h
    private static final String fxd = CustomConfig.DISPUTE_FXD_SCHEDULE; //16h to 21h
    private static final Logger log = LoggerFactory.getLogger(DisputeLandService.class);

    private DisputeLandService() {
    }

    public static DisputeLandService getInstance() {
        return DisputeLandServiceHolder.INSTANCE;
    }

    public void init() {
        if (!CustomConfig.DISPUTE_ENABLED) {
            return;
        }

        log.info("Init Dispute Lands...");

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            chance = CustomConfig.DISPUTE_WEEKEND_RND_CHANCE; //75%
        } else {
            chance = CustomConfig.DISPUTE_RND_CHANCE; //50%
        }

        // Dispute worldId's
        //worlds.add(600020000); // Sarpan
        worlds.add(600020001);
        worlds.add(600030000); // Tiamaranta

        CronService.getInstance().schedule(new Runnable() { //11h to 16h
            @Override
            public void run() {
                setActive(chance > Rnd.get(100));

                if (isActive()) {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            setActive(false);
                        }
                    }, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000);// 5 hours
                }
            }
        }, rnd);

        CronService.getInstance().schedule(new Runnable() { //21h to 2h
            @Override
            public void run() {
                setActive(chance > Rnd.get(100));

                if (isActive()) {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            setActive(false);
                        }
                    }, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000); // 5 hours
                }
            }
        }, rnd2);

        CronService.getInstance().schedule(new Runnable() { //2h to 7h
            @Override
            public void run() {
                setActive(chance > Rnd.get(100));

                if (isActive()) {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            setActive(false);
                        }
                    }, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000); // 5 hours
                }
            }
        }, rnd3);

        CronService.getInstance().schedule(new Runnable() { //7h to 11h
            @Override
            public void run() {
                setActive(chance > Rnd.get(100));

                if (isActive()) {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            setActive(false);
                        }
                    }, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000); // 5 hours
                }
            }
        }, rnd4);

        CronService.getInstance().schedule(new Runnable() { //16h to 21h Always
            @Override
            public void run() {
                setActive(true);

                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        setActive(false);
                    }
                }, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000); // 5 hours
            }
        }, fxd);
    }

    public boolean isActive() {
        if (!CustomConfig.DISPUTE_ENABLED) {
            return false;
        }

        return active;
    }

    public void setActive(boolean value) {
        active = value;
        syncState();
        broadcast();
    }

    private void syncState() {
        for (int world : worlds) {
            if (world == 600020001) {
                continue;
            }

            if (active) {
                World.getInstance().getWorldMap(world).setWorldOption(ZoneAttributes.PVP_ENABLED);
            } else {
                World.getInstance().getWorldMap(world).removeWorldOption(ZoneAttributes.PVP_ENABLED);
            }
        }
    }

    private void broadcast(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DISPUTE_LAND(worlds, active));
    }

    private void broadcast() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                broadcast(player);
            }
        });
    }

    public void onLogin(Player player) {
        if (!CustomConfig.DISPUTE_ENABLED) {
            return;
        }

        broadcast(player);
    }

    private static class DisputeLandServiceHolder {

        private static final DisputeLandService INSTANCE = new DisputeLandService();
    }
}
