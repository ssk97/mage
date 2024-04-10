package mage.target.targetadjustment;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.game.Game;
import mage.target.Target;

/**
 * @author TheElk801, notgreat
 */
public class DynamicValueTargetsAdjuster implements TargetAdjuster {
    private Target blueprintTarget = null;
    private final DynamicValue dynamicValue;

    /**
     * Modifies the target to be X targets, where X is the dynamic value.
     * If the ability's target has min targets of 0, it's treated as "up to X targets".
     * Otherwise, it's exactly "X targets".
     *
     * @param value The number of targets
     */
    public DynamicValueTargetsAdjuster(DynamicValue value) {
        this.dynamicValue = value;
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (blueprintTarget == null) {
            blueprintTarget = ability.getTargets().get(0).copy();
        }
        Target newTarget = blueprintTarget.copy();
        int count = dynamicValue.calculate(game, ability, null);
        newTarget.setMaxNumberOfTargets(count);
        if (blueprintTarget.getMinNumberOfTargets() != 0) {
            newTarget.setMinNumberOfTargets(count);
        }
        ability.getTargets().clear();
        ability.addTarget(newTarget);
    }
}
