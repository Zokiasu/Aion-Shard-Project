package com.aionemu.gameserver.model.templates.pet;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetRewards", propOrder = {"results"})
public class PetRewards {

    @XmlElement(name = "result")
    protected List<PetFeedResult> results;
    @XmlAttribute(name = "group", required = true)
    protected FoodType type;
    @XmlAttribute
    protected boolean loved = false;

    public List<PetFeedResult> getResults() {
        if (results == null) {
            results = new ArrayList<PetFeedResult>();
        }
        return this.results;
    }

    public FoodType getType() {
        return type;
    }

    public boolean isLoved() {
        return loved;
    }
}
