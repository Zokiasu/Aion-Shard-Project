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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DashEffect")
public class DashEffect extends DamageEffect {

    @Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        final Player effector = (Player) effect.getEffector();

        // Move Effector to Effected
        Skill skill = effect.getSkill();
        World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
    }

    @Override
    public void calculate(Effect effect) {
        if (effect.getEffected() == null) {
            return;
        }
        if (!(effect.getEffector() instanceof Player)) {
            return;
        }

        if (!super.calculate(effect, DamageType.PHYSICAL)) {
            return;
        }

        final Player effector = (Player) effect.getEffector();

        Creature effected = effect.getEffected();

        if(effector.getActingCreature() == effected) {
            log.warn("La target du joueur est la bonne");
        } else {
            log.warn("La target du joueur n'est pas la bonne");
        }
        effect.setDashStatus(DashStatus.DASH);
        effect.getSkill().setTargetPosition(effected.getX(), effected.getY(), effected.getZ(), effected.getHeading());
    }
}
