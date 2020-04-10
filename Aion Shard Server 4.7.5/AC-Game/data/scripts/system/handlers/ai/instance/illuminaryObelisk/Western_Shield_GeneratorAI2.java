package ai.instance.illuminaryObelisk;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

/**
 * @author Rinzler
 * @rework Ever
 * @Blackfire
 */
@AIName("western_shield_generator")
public class Western_Shield_GeneratorAI2 extends NpcAI2 {

    private boolean isInstanceDestroyed;
	private boolean wave1 = true;
	private boolean wave2;
	private boolean wave3;
	private boolean restrict;
	private boolean isFull;
		
    @Override
    protected void handleDialogStart(Player player) {
		if (!restrict) {
			if (isFull) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402203));
			} else {
				if (player.getInventory().getFirstItemByItemId(164000289) != null) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				} else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402211));
				}
			}
		} else {
			String str = "You can start charging this again after 8 seconds";
			PacketSendUtility.sendMessage(player, str);
		}
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000 && player.getInventory().decreaseByItemId(164000289, 1)) {
			if (wave1) {
				restrict = true;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402195));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
							startWaveWesternShieldGenerator1();
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402198));
							wave1 = false;
							wave2 = true;
							restrict = false;
							
						}
                    }, 8000);
				
				ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402225));
							spawn(702015, 255.7034f, 171.83853f, 325.81653f, (byte) 0, 18); 
						}
                    }, 4000);
			}
			if (wave2) {
				restrict = true;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402195));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402198));
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, 0));
							startWaveWesternShieldGenerator2();
							wave2 = false;
							wave3 = true;
							restrict = false;
						}
                    }, 8000);
				
			}
			if (wave3) {
				restrict = true;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402195));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402198));
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, 0));
							startWaveWesternShieldGenerator3();
							wave3 = false;
							restrict = false;
							isFull = true;
						}
                    }, 8000);
				
			}
        }
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }
	
	 /**
     * The higher the phase of the charge will spawn more difficult monsters, in the 3rd phase elite monsters will spawn.
     * Charging a shield to the 3rd phase continuously can be hard because of all the mobs you will have to handle.
     * A few easy monsters will spawn after a certain time if you leave the shield unit alone.
     * After all units have been charged to the 3rd phase, defeat the remaining monsters.
     * ***************************
     * Western Shield Generator *
     * **************************
     */
	
	private void startWaveWesternShieldGenerator1() {
		sp(283809, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
		sp(283809, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
		sp(283809, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");

		ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					sp(283811, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
					sp(283811, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
					sp(283811, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");
				}
			}, 15000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawn(702221, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
				}
			}, 30000);
    }
	 
    private void startWaveWesternShieldGenerator2() {
		sp(283812, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
		sp(283812, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
		sp(283812, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(283814, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
				sp(283814, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
				sp(283814, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");
			}
		}, 15000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(283812, 174.50981f, 251.38982f, 292.43088f, (byte) 0, 1000, "NorthernShieldGenerator1");
				sp(283814, 174.9973f, 254.4739f, 292.3325f, (byte) 0, 1000, "NorthernShieldGenerator2");
				sp(283812, 174.84029f, 257.80832f, 292.4389f, (byte) 0, 1000, "NorthernShieldGenerator3");
			}
		}, 30000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				 spawn(702222, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
			}
		}, 30000);
    }

    private void startWaveWesternShieldGenerator3() {
		sp(283812, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
		sp(283812, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
		sp(283812, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");
		sp(283809, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 5000, "WesternShieldGenerator1");
		sp(283809, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 5000, "WesternShieldGenerator2");
		sp(283809, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 5000, "WesternShieldGenerator3");
		sp(283811, 174.50981f, 251.38982f, 292.43088f, (byte) 0, 10000, "NorthernShieldGenerator1");
		sp(283811, 174.9973f, 254.4739f, 292.3325f, (byte) 0, 10000, "NorthernShieldGenerator2");
		sp(283811, 174.84029f, 257.80832f, 292.4389f, (byte) 0, 10000, "NorthernShieldGenerator3");

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				sp(283812, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 6000, "WesternShieldGenerator1");
				sp(283817, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 6000, "WesternShieldGenerator2");
				sp(283812, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 6000, "WesternShieldGenerator3");
				sp(283809, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 23000, "WesternShieldGenerator1");
				sp(283809, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 23000, "WesternShieldGenerator2");
				sp(283809, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 23000, "WesternShieldGenerator3");
				sp(283814, 174.50981f, 251.38982f, 292.43088f, (byte) 0, 10000, "NorthernShieldGenerator1");
				sp(283814, 174.9973f, 254.4739f, 292.3325f, (byte) 0, 10000, "NorthernShieldGenerator2");
				sp(283814, 174.84029f, 257.80832f, 292.4389f, (byte) 0, 10000, "NorthernShieldGenerator3");
			}
		}, 15000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawn(702223, 255.38777f, 212.00926f, 321.37292f, (byte) 90);
			}
		}, 30000);
    }

    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time);
    }

    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }
}