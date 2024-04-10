package mage.target.targetadjustment;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.ManacostVariableValue;
import mage.constants.ComparisonType;
import mage.filter.Filter;
import mage.filter.predicate.mageobject.PowerPredicate;
import mage.game.Game;
import mage.target.Target;

/**
 * @author TheElk801, notgreat
 */
public class PowerTargetAdjuster implements TargetAdjuster {
    private Target blueprintTarget = null;
    private final DynamicValue dynamicValue;
    private final ComparisonType comparison;

    /**
     * Modifies the target to also require a power that satisfies the comparison to the dynamic value.
     *
     * @param value   The value to be compared against
     * @param compare Which comparison to use
     */
    public PowerTargetAdjuster(DynamicValue value, ComparisonType compare) {
        this.dynamicValue = value;
        this.comparison = compare;
    }

    public PowerTargetAdjuster(ComparisonType comparison) {
        this(ManacostVariableValue.REGULAR, comparison);
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (blueprintTarget == null) {
            blueprintTarget = ability.getTargets().get(0).copy();
        }
        Target newTarget = blueprintTarget.copy();
        int amount = dynamicValue.calculate(game, ability, null);
        Filter<MageObject> filter = newTarget.getFilter();
        filter.add(new PowerPredicate(comparison, amount));
        filter.setMessage(filter.getMessage() + " (Power " + comparison + " " + amount);
        ability.getTargets().clear();
        ability.addTarget(newTarget);
    }
}
