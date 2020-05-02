package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for weapons. It keeps info about slots that weapon can be
 * equipped to.
 *
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry {

    WeaponInfoBlobEntry() {
        super(ItemBlobType.SLOTS_WEAPON);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        Item item = ownerItem;

        ItemSlot[] slots = ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot());
        if (slots.length == 1) {
            writeQ(buf, slots[0].getSlotIdMask());
            writeQ(buf, item.hasFusionedItem() ? 0x00 : 0x02);
            return;
        }
        // must occupy two slots
        if (item.getItemTemplate().isTwoHandWeapon()) {
            writeQ(buf, slots[0].getSlotIdMask() | slots[1].getSlotIdMask());
            writeQ(buf, 0);
        } else {
            // primary and secondary slots
            writeQ(buf, slots[0].getSlotIdMask());
            writeQ(buf, slots[1].getSlotIdMask());
        }
        //writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
        // TODO: check this, seems wrong
        //writeQ(buf, item.hasFusionedItem() ? 0x00 : 0x02);
    }

    @Override
    public int getSize() {
        return 16;
    }
}
