package com.aionemu.gameserver.model.templates.itemgroups;

import com.aionemu.gameserver.model.templates.rewards.IdLevelReward;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rolandas
 *
 */

/**
 * <p/>
 * Java class for EnchantGroup complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="EnchantGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{}BonusItemGroup">
 *       &lt;sequence>
 *         &lt;element name="item" type="{}IdLevelReward" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantGroup")
public class EnchantGroup extends BonusItemGroup {

    @XmlElement(name = "item")
    protected List<IdLevelReward> items;

    /**
     * Gets the value of the item property.
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the item property.
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getItems().add(newItem);
     * </pre>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link IdLevelReward
     * }
     */
    public List<IdLevelReward> getItems() {
        if (items == null) {
            items = new ArrayList<IdLevelReward>();
        }
        return this.items;
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.model.templates.itemgroups.ItemGroup#getRewards()
     */
    @Override
    public ItemRaceEntry[] getRewards() {
        return getItems().toArray(new ItemRaceEntry[0]);
    }
}
