package mage.abilities.effects.common.continuous;

import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.constants.Duration;

/**
 * Boost without granting any abilities. All the work is in the superclass.
 *
 * @author notgreat, BetaSteward_at_googlemail.com, North
 */
public class BoostGenericEffect extends BoostGainAbilityGenericEffect {

    public BoostGenericEffect(int power, int toughness) {
        this(power, toughness, Duration.EndOfTurn);
    }

    public BoostGenericEffect(int power, int toughness, Duration duration) {
        this(StaticValue.get(power), StaticValue.get(toughness), duration);
    }

    public BoostGenericEffect(DynamicValue power, DynamicValue toughness) {
        this(power, toughness, Duration.EndOfTurn);
    }

    public BoostGenericEffect(DynamicValue power, DynamicValue toughness, Duration duration) {
        super(power, toughness, duration);
    }

    protected BoostGenericEffect(final BoostGenericEffect effect) {
        super(effect);
    }

    @Override
    public BoostGenericEffect copy() {
        return new BoostGenericEffect(this);
    }
}
