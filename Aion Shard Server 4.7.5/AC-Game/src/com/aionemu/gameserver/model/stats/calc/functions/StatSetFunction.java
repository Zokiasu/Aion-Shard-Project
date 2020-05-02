package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public class StatSetFunction extends StatFunction {

    public StatSetFunction() {
    }

    public StatSetFunction(StatEnum name, int value) {
        super(name, value, false);
    }

    @Override
    public void apply(Stat2 stat) {
        if (isBonus()) {
            stat.setBonus(getValue());
        } else {
            stat.setBase(getValue());
        }
    }

    @Override
    public final int getPriority() {
        return isBonus() ? Integer.MAX_VALUE : Integer.MAX_VALUE - 10;
    }

    @Override
    public String toString() {
        return "StatSetFunction [" + super.toString() + "]";
    }
}
