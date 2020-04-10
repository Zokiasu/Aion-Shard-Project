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
package ai.instance.infinityShard;

import ai.AggressiveNpcAI2;
import ai.ActionItemNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eloann
 * @rework Swig
 */
@AIName("hyperion")
public class HyperionAI2 extends AggressiveNpcAI2 {

    private AtomicBoolean isHome = new AtomicBoolean(true);
    private Future<?> skillTask;
    private Future<?> BlasterTask;
    private Future<?> EnergyTask;
    protected List<Integer> percents = new ArrayList<Integer>();

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            startSkillTask();
            startBlasterTask();
            startEnergyTask();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 80:
                        AI2Actions.useSkill(this, 21245);
                        spawnHyperionEasy();
                        spawnAssaultPod();
                        break;
                    case 75:
                        spawnHyperionNormal1();
                        break;
                    case 70:
                        spawnHyperionNormal1();
                        spawnAssaultPod();
                        break;
                    case 60:
                        spawnHyperionNormal1();
                        spawnAssaultPod();
                        break;
                    case 55:
                        spawnHyperionNormal1();
                        break;
                    case 52:
                        spawnHyperionNormal();
                        break;
                    case 50:
                        AI2Actions.useSkill(this, 21244);
                        spawnHyperionHard();
                        spawnAssaultPod();
                        break;
                    case 47:
                        AI2Actions.useSkill(this, 21245);
                        spawnHyperionEasy();
                        break;
                    case 40:
                        AI2Actions.useSkill(this, 21244);
                        spawnHyperionNormal();
                        spawnAssaultPod();
                        break;
                    case 35:
                        spawnHyperionNormal();
                        break;
                    case 30:
                        cancelEnergyTask();
                        AI2Actions.useSkill(this, 21248);
                        spawnHyperionHard();
                        spawnAssaultPod();
                        break;
                    case 25:
                        AI2Actions.useSkill(this, 21244);
                        spawnHyperionHard();
                        spawnHyperionNormal();
                        break;
                    case 20:
                        spawnHyperionHard();
                        spawnAssaultPod();
                        break;
                    case 10:
                        AI2Actions.useSkill(this, 21246);
                        spawnHyperionNormal();
                        spawnAssaultPod();
                        break;
                    case 5:
                        AI2Actions.useSkill(this, 21249);
                        spawnHyperionHard();
                        break;
                    case 2:
                        AI2Actions.useSkill(this, 21249);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void spawnHyperionEasy() {

        spawn(231103, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(231103, 147.41049f, 131.2569f, 114.49583f, (byte) 16);
        spawn(231103, 153.60158f, 129.60774f, 114.49583f, (byte) 16);

        sp(233289, 110.090965f, 128.28905f, 124.15179f, (byte) 43, 1000, "A2142518112");
        sp(233292, 110.090965f, 128.28905f, 124.15179f, (byte) 43, 1000, "A2142518112");
        sp(231096, 110.090965f, 128.28905f, 124.15179f, (byte) 43, 1000, "A2142518112");
    }

    private void spawnHyperionNormal() {
        spawn(233288, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(231103, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(231103, 147.41049f, 131.2569f, 114.49583f, (byte) 16);
        spawn(231103, 153.60158f, 129.60774f, 114.49583f, (byte) 16);
        spawn(233296, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnHyperionNormal1() {
        spawn(233292, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233295, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(231103, 147.41049f, 131.2569f, 114.49583f, (byte) 16);
        spawn(231103, 153.60158f, 129.60774f, 114.49583f, (byte) 16);
        spawn(233295, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnHyperionHard() {
        spawn(233288, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233299, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233298, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(231103, 147.41049f, 131.2569f, 114.49583f, (byte) 16);
        spawn(231103, 147.41049f, 131.2569f, 114.49583f, (byte) 16);
        spawn(231103, 153.60158f, 129.60774f, 114.49583f, (byte) 16);
        spawn(233298, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnAssaultPod() {
        int rnd = Rnd.get(1, 8);
        switch (rnd) {
            case 1:
                spawn(284070, 136.94958f, 133.78249f, 112.12359f, (byte) 74);
                break;
            case 2:
                spawn(284070, 121.06398f, 133.20741f, 112.12359f, (byte) 52);
                break;
            case 3:
                spawn(284070, 124.02135f, 146.44595f, 112.12359f, (byte) 99);
                break;
            case 4:
                spawn(284070, 107.20429f, 140.68904f, 112.12359f, (byte) 71);
                break;
            case 5:
                spawn(284070, 151.51714f, 135.24718f, 112.12359f, (byte) 114);
                break;
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{80, 75, 60, 55, 52, 50, 40, 35, 30, 25, 20, 10, 5, 2});
    }

    private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelskillTask();
                } else {
                    Throw();
                }
            }
        }, 30000, 120000);
    }

    private void startBlasterTask() {
        BlasterTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelBlasterTask();
                } else {
                    Blaster();
                }
            }
        }, 2000, 90000);
    }

    private void startEnergyTask() {
        EnergyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelEnergyTask();
                } else {
                    Energy();
                }
            }
        }, 10000, 160000);
    }

    private void cancelskillTask() {
        if (skillTask != null && !skillTask.isCancelled()) {
            skillTask.cancel(true);
        }
    }

    private void cancelBlasterTask() {
        if (BlasterTask != null && !BlasterTask.isCancelled()) {
            BlasterTask.cancel(true);
        }
    }

    private void cancelEnergyTask() {
        if (EnergyTask != null && !EnergyTask.isCancelled()) {
            EnergyTask.cancel(true);
        }
    }

    private void Throw() {
        SkillEngine.getInstance().getSkill(getOwner(), 21250, 55, getOwner()).useNoAnimationSkill();
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                SkillEngine.getInstance().getSkill(getOwner(), 21251, 55, getOwner()).useNoAnimationSkill();
            }
        }, 5000);
    }

    private void Blaster() {
        SkillEngine.getInstance().getSkill(getOwner(), 21241, 60, getOwner().getTarget()).useNoAnimationSkill();
        Player target = getRandomTarget();
        if (target == null) {
        }
    }

    private void Energy() {
        SkillEngine.getInstance().getSkill(getOwner(), 21247, 60, getOwner().getTarget()).useNoAnimationSkill();
        Player target = getRandomTarget();
        if (target == null) {
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    private void despawnAdds() {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        deleteNpcs(instance.getNpcs(231096));
        deleteNpcs(instance.getNpcs(233292));
        deleteNpcs(instance.getNpcs(231103));
        deleteNpcs(instance.getNpcs(233289));
        deleteNpcs(instance.getNpcs(233288));
        deleteNpcs(instance.getNpcs(233294));
        deleteNpcs(instance.getNpcs(233296));
        deleteNpcs(instance.getNpcs(233295));
        deleteNpcs(instance.getNpcs(233299));
        deleteNpcs(instance.getNpcs(233298));
        deleteNpcs(instance.getNpcs(231104));
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        addPercent();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
        isHome.set(true);
        despawnAdds();
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
        percents.clear();
        despawnAdds();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        percents.clear();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
        despawnAdds();
    }

    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                Npc npc = (Npc) spawn(npcId, x, y, z, h);
                npc.getSpawn().setWalkerId(walkerId);
                WalkManager.startWalking((NpcAI2) npc.getAi2());
            }
        }, time);
    }
}
