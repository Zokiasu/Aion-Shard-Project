/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, regarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.gameserver.services.siegeservice;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeAbyssRace;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegePlayerReward;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLegionReward;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.base.Base;
import com.aionemu.gameserver.services.mail.AbyssSiegeLevel;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.services.mail.SiegeResult;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.google.common.collect.Lists;

import com.aionemu.gameserver.spawnengine.*;
import com.aionemu.gameserver.model.templates.spawns.*;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Object that controls siege of certain fortress. Siege object is not reusable.
 * New siege = new instance.
 * <p/>
 *
 * @author SoulKeeper
 */
public class FortressSiege extends Siege<FortressLocation> {

    private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
    private final AbyssPointsListener addAPListener = new AbyssPointsListener(this);
    private final GloryPointsListener addGPListener = new GloryPointsListener(this);
    protected SiegePlayerReward instanceReward;

    private FastMap<Integer, VisibleObject> AhserionTeleporter = new FastMap<Integer, VisibleObject>();

    public FortressSiege(FortressLocation fortress) {
        super(fortress);
    }

    @Override
    public void onSiegeStart() {
        if (LoggingConfig.LOG_SIEGE) {
            log.info("[SIEGE] > Siege started. [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:" + getSiegeLocation().getLegionId() + "]");
        }
        // Mark fortress as vulnerable
        getSiegeLocation().setVulnerable(true);

        // Let the world know where the siege are
        broadcastState(getSiegeLocation());

        // Clear fortress from enemys
        getSiegeLocation().clearLocation();

        // Register abyss points listener
        // We should listen for abyss point callbacks that players are earning
        GlobalCallbackHelper.addCallback(addAPListener);
        GlobalCallbackHelper.addCallback(addGPListener);

     // Remove all and spawn siege NPCs
        deSpawnNpcs(getSiegeLocationId());
        spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
        initSiegeBoss();
		
		if (getSiegeLocation().getLocationId() == 7011) {
			captureBasePosts();
        }
    }

	private void captureBasePosts() {
		BaseService.getInstance().capture(90, Race.ASMODIANS);
		BaseService.getInstance().capture(91, Race.ELYOS);
		BaseService.getInstance().capture(113, Race.getRaceByString(getSiegeLocation().getRace().toString()));
		BaseService.getInstance().capture(114, Race.getRaceByString(getSiegeLocation().getRace().toString()));
		BaseService.getInstance().capture(115, Race.getRaceByString(getSiegeLocation().getRace().toString()));
    }

    @Override
    public void onSiegeFinish() {
        SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
        SiegeRace looser = getSiegeLocation().getRace();
        if (LoggingConfig.LOG_SIEGE) {
            if (winner != null) {
                log.info("[SIEGE] > Siege finished. [FORTRESS:" + getSiegeLocationId() + "] [OLD RACE: " + getSiegeLocation().getRace() + "] [OLD LegionId:" + getSiegeLocation().getLegionId() + "] [NEW RACE: " + winner.getSiegeRace() + "] [NEW LegionId:" + (winner.getWinnerLegionId() == null ? 0 : winner.getWinnerLegionId()) + "]");
            } else {
                log.info("[SIEGE] > Siege finished. No winner found [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:" + getSiegeLocation().getLegionId() + "]");
            }
        }

        // Unregister abyss points listener callback
        // We really don't need to add abyss points anymore
        GlobalCallbackHelper.removeCallback(addAPListener);
        GlobalCallbackHelper.removeCallback(addGPListener);

        // Unregister siege boss listeners
        // cleanup :)
        unregisterSiegeBossListeners();

        // despawn protectors and make fortress invulnerable
        SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
        getSiegeLocation().setVulnerable(false);
        getSiegeLocation().setUnderShield(false);

        // Guardian deity general was not killed, fortress stays with previous
        if (isBossKilled()) {
            onCapture();
            broadcastUpdate(getSiegeLocation());
        } else {
            broadcastState(getSiegeLocation());
        }

        SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);

        // Reward players and owning legion
        // If fortress was not captured by balaur
        if (SiegeRace.BALAUR != getSiegeLocation().getRace()) {
            giveRewardsToLegion();
            giveRewardsToPlayers(getSiegeCounter().getRaceCounter(getSiegeLocation().getRace()));
        }
        
        // Remove gp for players that lost the fortress
        if (winner.getSiegeRace() != looser) {
            giveLossToPlayers(looser);
        }

        // Update outpost status
        // Certain fortresses are changing outpost ownership
        updateOutpostStatusByFortress(getSiegeLocation());

        // Update data in the DB
        DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());

        getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                player.unsetInsideZoneType(ZoneType.SIEGE);
                if (isBossKilled() && (SiegeRace.getByRace(player.getRace()) == getSiegeLocation().getRace())) {
                    QuestEngine.getInstance().onKill(new QuestEnv(getBoss(), player, 0, 0));
                }
            }
        });
		
		if (getSiegeLocation().getLocationId() == 7011) {
			BaseService.getInstance().capture(113, Race.NPC);
			BaseService.getInstance().capture(114, Race.NPC);
			BaseService.getInstance().capture(115, Race.NPC);
		}
        final SiegeService srv = SiegeService.getInstance();

        if( (srv.getSiegeLocation(3011) != null && srv.getSiegeLocation(3011).getRace() == SiegeRace.ASMODIANS) && (srv.getSiegeLocation(3021) != null && srv.getSiegeLocation(3021).getRace() == SiegeRace.ASMODIANS)
            && ((srv.getSiegeLocation(2011) != null && srv.getSiegeLocation(2011).getRace() == SiegeRace.ASMODIANS) || (srv.getSiegeLocation(2021) != null && srv.getSiegeLocation(2021).getRace() == SiegeRace.ASMODIANS)) ) {
            log.warn("Ahserion Teleporter for Asmodian");

            if (AhserionTeleporter.containsKey(297273) && AhserionTeleporter.get(297273).isSpawned()) {
                log.warn("Ahserion Teleporter was already spawned...");
            } else {
                //Teleporter
                AhserionTeleporter.put(297273, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210050000, 297273, 1079.38f, 1492.68f, 404.861f, (byte) 90), 1));
                //PNJ
                AhserionTeleporter.put(209218, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210050000, 209238, 1086.6732f, 1499.8425f, 404.86078f, (byte) 15), 1));
                AhserionTeleporter.put(209208, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210050000, 209238, 1071.1923f, 1500.6755f, 404.86078f, (byte) 45), 1));
                AhserionTeleporter.put(209209, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210050000, 209238, 1071.6067f, 1484.9974f, 404.86078f, (byte) 76), 1));
                AhserionTeleporter.put(209238, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210050000, 209238, 1086.8112f, 1485.7357f, 404.86078f, (byte) 103), 1));
                log.warn("Ahserion Teleporter spawned in Inggison");
                announceEveryOne("Ahserion", "Ahserion's Asmodian teleporter has appeared in Inggison.");
            }

            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (VisibleObject vo : AhserionTeleporter.values()) {
                        vo.getController().onDelete();
                    }
                    AhserionTeleporter.clear();
                    announceEveryOne("Ahserion", "Ahserion's Asmodian teleporter is now missing.");
                }
            }, 3600 * 1000);

        } else if( (srv.getSiegeLocation(3011) != null && srv.getSiegeLocation(3011).getRace() == SiegeRace.ELYOS) && (srv.getSiegeLocation(3021) != null && srv.getSiegeLocation(3021).getRace() == SiegeRace.ELYOS)
                && ((srv.getSiegeLocation(2011) != null && srv.getSiegeLocation(2011).getRace() == SiegeRace.ELYOS) || (srv.getSiegeLocation(2021) != null && srv.getSiegeLocation(2021).getRace() == SiegeRace.ELYOS)) ) {
            log.warn("Ahserion Teleporter for Elyos");

            if (AhserionTeleporter.containsKey(297273) && AhserionTeleporter.get(297273).isSpawned()) {
                log.warn("Ahserion Teleporter was already spawned...");
            } else {
                //Teleporter
                AhserionTeleporter.put(297274, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220070000, 297274, 1822.23f, 1976.96f, 392.035f, (byte) 0), 1));
                //PNJ
                AhserionTeleporter.put(209018, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220070000, 209038, 1826.4636f, 1968.9579f, 392.0354f, (byte) 100), 1));
                AhserionTeleporter.put(209008, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220070000, 209038, 1817.4279f, 1984.7587f, 392.0354f, (byte) 40), 1));
                AhserionTeleporter.put(209009, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220070000, 209038, 1815.3062f, 1971.6099f, 392.0354f, (byte) 69), 1));
                AhserionTeleporter.put(209038, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220070000, 209038, 1829.2606f, 1980.844f, 392.0354f, (byte) 10), 1));
                log.warn("Ahserion Teleporter spawned in Gelkmaros");
                announceEveryOne("Ahserion", "Ahserion's Elyos teleporter has appeared in Gelkmaros.");
            }

            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (VisibleObject vo : AhserionTeleporter.values()) {
                        if (vo != null) {
                            Npc npc = (Npc) vo;
                            if (!npc.getLifeStats().isAlreadyDead()) {
                                npc.getController().onDelete();
                            }
                        }
                    }
                    AhserionTeleporter.clear();
                    announceEveryOne("Ahserion", "Ahserion's Elyos teleporter is now missing.");
                }
            }, 3600 * 1000);
        }
    }

    public void announceEveryOne(final String senderName,final String Message){
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player object) {
                PacketSendUtility.sendSys2Message(object, senderName, Message);
            }
        });
    }

    protected SiegeAbyssRace getPlayerReward(Integer object) {
        instanceReward.regPlayerReward(object);
        return (SiegeAbyssRace) instanceReward.getPlayerReward(object);
    }

    public void onCapture() {
        SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();

        // Set new fortress and artifact owner race
        getSiegeLocation().setRace(winner.getSiegeRace());
        getArtifact().setRace(winner.getSiegeRace());

        log.info("Global Elyos Influence = " + Influence.getInstance().getGlobalElyosInfluence() + ".");
        log.info("Global Asmodians Influence = " + Influence.getInstance().getGlobalAsmodiansInfluence() + ".");
        log.info("Global Balaurs Influence = " + Influence.getInstance().getGlobalBalaursInfluence() + ".");

        final Influence inf = Influence.getInstance();
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                instanceReward = new SiegePlayerReward(player.getWorldId());
                SiegeAbyssRace ownerReward = getPlayerReward(player.getObjectId());
                Integer object = player.getObjectId();
                if (inf.getGlobalElyosInfluence() >= 0.30f) {
                    if (player.getRace() == Race.ASMODIANS) {
                        getPlayerReward(object).applyBoostEffect(player);
                        log.info("Abyss Race buff start Asmodians.");
                    }
                } else if (inf.getGlobalElyosInfluence() < 0.30f) {
                    if (player.getRace() == Race.ASMODIANS) {
                        getPlayerReward(object).endBoostEffect(player);
                        log.info("Abyss Race buff end Asmodians.");
                    }
                }

                if (inf.getGlobalAsmodiansInfluence() >= 0.30f) {
                    if (player.getRace() == Race.ELYOS) {
                        getPlayerReward(object).applyBoostEffect(player);
                        log.info("Abyss Race buff start Elyos.");
                    }
                } else if (inf.getGlobalAsmodiansInfluence() < 0.30f) {
                    if (player.getRace() == Race.ELYOS) {
                        getPlayerReward(object).endBoostEffect(player);
                        log.info("Abyss Race buff end Elyos.");
                    }
                }
            }
        });

     // If new race is balaur
        if (SiegeRace.BALAUR == winner.getSiegeRace()) {
            getSiegeLocation().setLegionId(0);
            getArtifact().setLegionId(0);
        } else {
            Integer topLegionId = winner.getWinnerLegionId();
            getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
            getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
        }
		
		if (getSiegeLocation().getLocationId() == 7011) {
			BaseService.getInstance().capture(113, Race.NPC);
			BaseService.getInstance().capture(114, Race.NPC);
			BaseService.getInstance().capture(115, Race.NPC);
		}



    }
		

    @Override
    public boolean isEndless() {
        return false;
    }

    @Override
    public void addAbyssPoints(Player player, int abysPoints) {
        getSiegeCounter().addAbyssPoints(player, abysPoints);
    }

    @Override
    public void addGloryPoints(Player player, int gloryPoints) {
        getSiegeCounter().addGloryPoints(player, gloryPoints);
    }

    protected void giveRewardsToLegion() {
        // Legion with id 0 = not exists?
        if (getSiegeLocation().getLegionId() == 0) {
            if (LoggingConfig.LOG_SIEGE) {
                log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LEGION :" + getSiegeLocation().getLegionId() + "] Legion Reward not sending because fortress not owned by any legion.");
            }
            return;
        }

        List<SiegeLegionReward> legionRewards = getSiegeLocation().getLegionReward();
        List<SiegeLegionReward> legionRewardsOnOccupy = getSiegeLocation().getLegionRewardOnOccupy();
        int legionBGeneral = LegionService.getInstance().getLegionBGeneral(getSiegeLocation().getLegionId());
        if (legionBGeneral != 0) {
            PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
            if (LoggingConfig.LOG_SIEGE) {
                log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Legion Reward in process... LegionId:"
                        + getSiegeLocation().getLegionId() + " General Name:" + BGeneral.getName());
            }
            if (legionRewards != null) {
                for (SiegeLegionReward medalsType : legionRewards) {
                    if (LoggingConfig.LOG_SIEGE) {
                        log.info("[SIEGE] > [Legion Reward to: " + BGeneral.getName() + "] ITEM RETURN "
                                + medalsType.getItemId() + " ITEM COUNT " + medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE);
                    }
                    MailFormatter.sendAbyssRewardMail(getSiegeLocation(), BGeneral, AbyssSiegeLevel.NONE, SiegeResult.PROTECT, System.currentTimeMillis(), medalsType.getItemId(), medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);
                }
            }

            SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
            if (legionRewardsOnOccupy != null &&
                SiegeRace.BALAUR != winner.getSiegeRace() &&
                getSiegeLocation().getLegionId() == winner.getWinnerLegionId()) {

                for (SiegeLegionReward medalsType : legionRewardsOnOccupy) {
                    if (LoggingConfig.LOG_SIEGE) {
                        log.info("[SIEGE] > [Legion Reward on occupy to: " + BGeneral.getName() + "] ITEM RETURN "
                                + medalsType.getItemId() + " ITEM COUNT " + 1);
                    }
                    MailFormatter.sendAbyssRewardMail(getSiegeLocation(), BGeneral, AbyssSiegeLevel.NONE, SiegeResult.OCCUPY, System.currentTimeMillis(), medalsType.getItemId(), 1, 0);
                }
            }
        }
    }
    
    protected void giveLossToPlayers(final SiegeRace race) {
        if (race == SiegeRace.BALAUR)// this shouldn't happen, but secure is secure :)
            return;
        getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getRace().name() == race.name()) {//dont know if this works...
                    switch (player.getAbyssRank().getRank()) {
                        case SUPREME_COMMANDER:
                            AbyssPointsService.addGp(player, 40);
                            break;
                        case COMMANDER:
                            AbyssPointsService.addGp(player, 40);
                            break;
                        case GREAT_GENERAL:
                            AbyssPointsService.addGp(player, 35);
                            break;
                        case GENERAL:
                            AbyssPointsService.addGp(player, 35);
                            break;
                        case STAR5_OFFICER:
                            AbyssPointsService.addGp(player, 30);
                            break;
                        case STAR4_OFFICER:
                            AbyssPointsService.addGp(player, 25);
                            break;
                        case STAR3_OFFICER:
                            AbyssPointsService.addGp(player, 20);
                            break;
                        case STAR2_OFFICER:
                            AbyssPointsService.addGp(player, 20);
                            break;
                        case STAR1_OFFICER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE1_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE2_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE3_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE4_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE5_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE6_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE7_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE8_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        case GRADE9_SOLDIER:
                            AbyssPointsService.addGp(player, 10);
                            break;
                        default:
                            break;
                    }
                } else {
                    return;
                }
            }
        });
    }
    protected void giveRewardsToPlayers(SiegeRaceCounter winnerDamage) {
        // Get the map with playerId to siege reward
        Map<Integer, Long> playerAbyssPoints = winnerDamage.getPlayerAbyssPoints();
        List<Integer> topPlayersIds = Lists.newArrayList(playerAbyssPoints.keySet());
        Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());
        SiegeResult result = isBossKilled() ? SiegeResult.OCCUPY : SiegeResult.DEFENDER;

        // Black Magic Here :)
        int i = 0;
        List<SiegeReward> playerRewards = getSiegeLocation().getReward();
        int rewardLevel = 0;
        for (SiegeReward topGrade : playerRewards) {
            AbyssSiegeLevel level = AbyssSiegeLevel.getLevelById(++rewardLevel);
            for (int rewardedPC = 0; i < topPlayersIds.size() && rewardedPC < topGrade.getTop(); ++i) {
                Integer playerId = topPlayersIds.get(i);
                PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
                ++rewardedPC;
                if (LoggingConfig.LOG_SIEGE) {
                    log.info("[SIEGE]  > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Player Reward to: " + playerNames.get(playerId) + "] ITEM RETURN " + topGrade.getItemId() + " ITEM COUNT " + topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE);
                }
                MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, level, result, System.currentTimeMillis(), topGrade.getItemId(), topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);

                switch (level) {//gp reward for fotress occupation
                    case HERO_DECORATION:
                        AbyssPointsService.addGp(pcd.getPlayer(), 300);
                        break;
                    case MEDAL:
                        AbyssPointsService.addGp(pcd.getPlayer(), 200);
                        break;
                    case ELITE_SOLDIER:
                        AbyssPointsService.addGp(pcd.getPlayer(), 150);
                        break;
                    case VETERAN_SOLDIER:
                        AbyssPointsService.addGp(pcd.getPlayer(), 100);
                        break;
                    default:
                        AbyssPointsService.addGp(pcd.getPlayer(), 50);
                        break;
                }
            }
        }
        if (!isBossKilled()) {
            while (i < topPlayersIds.size()) {
                i++;
                Integer playerId = topPlayersIds.get(i);
                PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
                //Send Announcement Mails without reward to the rest
                MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, AbyssSiegeLevel.NONE, SiegeResult.EMPTY, System.currentTimeMillis(), 0, 0, 0);
            }
        }
    }

    protected ArtifactLocation getArtifact() {
        return SiegeService.getInstance().getFortressArtifacts().get(getSiegeLocationId());
    }

    protected boolean hasArtifact() {
        return getArtifact() != null;
    }
}
