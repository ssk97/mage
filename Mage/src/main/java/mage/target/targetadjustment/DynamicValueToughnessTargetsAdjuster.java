package mage.target.targetadjustment;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.ManacostVariableValue;
import mage.constants.ComparisonType;
import mage.filter.predicate.mageobject.ToughnessPredicate;
import mage.game.Game;
import mage.target.Target;

/**
 * @author TheElk801, notgreat
 */
public class DynamicValueToughnessTargetsAdjuster implements TargetAdjuster {
    private Target blueprintTarget = null;
    private final DynamicValue dynamicValue;
    private final ComparisonType comparison;

    /**
     * Modifies the target to also require a toughness that satisfies the comparison to the dynamic value.
     *
     * @param value   The value to be compared against
     * @param compare Which comparison to use
     */
    public DynamicValueToughnessTargetsAdjuster(DynamicValue value, ComparisonType compare) {
        this.dynamicValue = value;
        this.comparison = compare;
    }

    public DynamicValueToughnessTargetsAdjuster(ComparisonType comparison) {
        this(ManacostVariableValue.REGULAR, comparison);
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (blueprintTarget == null) {
            blueprintTarget = ability.getTargets().get(0).copy();
        }
        Target newTarget = blueprintTarget.copy();
        int mv = dynamicValue.calculate(game, ability, null);
        newTarget.getFilter().add(new ToughnessPredicate(comparison, mv));
        ability.getTargets().clear();
        ability.addTarget(newTarget);
    }
}
