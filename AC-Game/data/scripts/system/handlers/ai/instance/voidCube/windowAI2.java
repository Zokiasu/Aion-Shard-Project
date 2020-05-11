package ai.instance.voidCube;

import ai.ActionItemNpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;


@AIName("window")
public class windowAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
      if(getOwner().getNpcId() == 701581)
      {
          if (player.getInventory().getItemCountByItemId(164000271) > 0)
          {
              player.getInventory().decreaseByItemId(164000271, 1);
              getOwner().getController().die();
          }
          else {
              PacketSendUtility.sendBrightYellowMessageOnCenter(player, "\u041d\u0443\u0436\u043d\u0430 \u0411\u043e\u043c\u0431\u0430 \u0434\u043b\u044f \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0438\u044f \u043a\u0440\u0430\u0441\u043d\u044b\u0445 \u0432\u043e\u0440\u043e\u0442 \u0431\u0430\u0440\u044c\u0435\u0440\u0430.");
          }
      }
        if(getOwner().getNpcId() == 701582)
        {
            if (player.getInventory().getItemCountByItemId(164000272) > 0)
            {
                player.getInventory().decreaseByItemId(164000272, 1);
                getOwner().getController().die();
            }
            else {
                PacketSendUtility.sendBrightYellowMessageOnCenter(player, "\u041d\u0443\u0436\u043d\u0430 \u0411\u043e\u043c\u0431\u0430 \u0434\u043b\u044f \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0438\u044f \u0441\u0438\u043d\u0438\u0445 \u0432\u043e\u0440\u043e\u0442 \u0431\u0430\u0440\u044c\u0435\u0440\u0430.");
            }
        }
        if(getOwner().getNpcId() == 701583)
        {
            if (player.getInventory().getItemCountByItemId(164000273) > 0)
            {
                player.getInventory().decreaseByItemId(164000273, 1);
                getOwner().getController().die();
            }
            else {
                PacketSendUtility.sendBrightYellowMessageOnCenter(player, "\u041d\u0443\u0436\u043d\u0430 \u0411\u043e\u043c\u0431\u0430 \u0434\u043b\u044f \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0438\u044f \u0436\u0435\u043b\u0442\u044b\u0445 \u0432\u043e\u0440\u043e\u0442 \u0431\u0430\u0440\u044c\u0435\u0440\u0430.");
            }
        }
          else
            return;

    }

    @Override
    protected void handleUseItemFinish(Player player) {
        AI2Actions.deleteOwner(this);
    }
}
