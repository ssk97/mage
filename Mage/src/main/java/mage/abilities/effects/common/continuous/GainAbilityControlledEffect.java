package mage.abilities.effects.common.continuous;

import mage.abilities.Ability;
import mage.abilities.CompoundAbility;
import mage.constants.Duration;
import mage.filter.FilterPermanent;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.target.targetpointer.FilterAllPermanentsTargetPointer;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class GainAbilityControlledEffect extends GainAbilityTargetEffect {

    public GainAbilityControlledEffect(Ability ability, Duration duration, FilterPermanent filter) {
        this(ability, duration, filter, false);
    }

    public GainAbilityControlledEffect(CompoundAbility ability, Duration duration, FilterPermanent filter) {
        this(ability, duration, filter, false);
    }

    public GainAbilityControlledEffect(Ability ability, Duration duration, FilterPermanent filter, boolean excludeSource) {
        this(new CompoundAbility(ability), duration, filter, excludeSource);
    }

    public GainAbilityControlledEffect(CompoundAbility ability, Duration duration, FilterPermanent filter, boolean excludeSource) {
        super((Ability)ability, duration);
        FilterPermanent filterCopy = filter.copy();
        if (excludeSource) {
            filterCopy.add(AnotherPredicate.instance);
        }
        this.setTargetPointer(new FilterAllPermanentsTargetPointer(filterCopy, duration!=Duration.WhileOnBattlefield));

        this.generateGainAbilityDependencies(ability, filter);
    }

    protected GainAbilityControlledEffect(final GainAbilityControlledEffect effect) {
        super(effect);
    }

    @Override
    public GainAbilityControlledEffect copy() {
        return new GainAbilityControlledEffect(this);
    }

}
