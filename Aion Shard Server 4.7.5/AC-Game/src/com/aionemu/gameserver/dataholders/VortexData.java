package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.vortex.VortexTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dimensional_vortex")
public class VortexData {

    @XmlElement(name = "vortex_location")
    private List<VortexTemplate> vortexTemplates;
    @XmlTransient
    private FastMap<Integer, VortexLocation> vortex = new FastMap<Integer, VortexLocation>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (VortexTemplate template : vortexTemplates) {
            vortex.put(template.getId(), new VortexLocation(template));
        }
    }

    public int size() {
        return vortex.size();
    }

    public VortexLocation getVortexLocation(int invasionWorldId) {
        for (VortexLocation loc : vortex.values()) {
            if (loc.getInvasionWorldId() == invasionWorldId) {
                return loc;
            }
        }
        return null;
    }

    public FastMap<Integer, VortexLocation> getVortexLocations() {
        return vortex;
    }
}
