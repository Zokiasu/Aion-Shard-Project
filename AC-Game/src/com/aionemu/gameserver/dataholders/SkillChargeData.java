package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.skillengine.model.ChargeSkillEntry;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"chargeSkills"})
@XmlRootElement(name = "skill_charge")
public class SkillChargeData {

    @XmlElement(name = "charge", required = true)
    protected List<ChargeSkillEntry> chargeSkills;
    @XmlTransient
    private TIntObjectHashMap<ChargeSkillEntry> skillChargeData = new TIntObjectHashMap<ChargeSkillEntry>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (ChargeSkillEntry chargeSkill : chargeSkills) {
            skillChargeData.put(chargeSkill.getId(), chargeSkill);
        }
        chargeSkills.clear();
        chargeSkills = null;
    }

    public ChargeSkillEntry getChargedSkillEntry(int chargeId) {
        return skillChargeData.get(chargeId);
    }

    public int size() {
        return skillChargeData.size();
    }
}
