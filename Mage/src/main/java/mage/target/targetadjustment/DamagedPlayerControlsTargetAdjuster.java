package mage.target.targetadjustment;

import mage.abilities.Ability;
import mage.filter.Filter;
import mage.filter.predicate.card.OwnerIdPredicate;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.game.Game;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.targetpointer.FirstTargetPointer;

import java.util.UUID;

/**
 * @author notgreat
 */
public class DamagedPlayerControlsTargetAdjuster implements TargetAdjuster {
    private TargetPermanent blueprintTarget = null;
    private boolean owner;
    /**
     * Use with DealsCombatDamageToAPlayerTriggeredAbility with setTargetPointer enabled.
     * Adjusts the target to only target something the damaged player controls (or owns with alternative constructor)
     * And then removes the effects' target pointer that the triggered ability set
     */
    public DamagedPlayerControlsTargetAdjuster() {
        this(false);
    }
    public DamagedPlayerControlsTargetAdjuster(boolean owner) {
        this.owner = owner;
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (blueprintTarget == null) {
            blueprintTarget = (TargetPermanent) ability.getTargets().get(0).copy();
        }
        UUID opponentId = ability.getEffects().get(0).getTargetPointer().getFirst(game, ability);
        Player opponent = game.getPlayer(opponentId);
        ability.getTargets().clear();
        ability.getAllEffects().setTargetPointer(new FirstTargetPointer());
        if (opponent == null) {
            return;
        }
        Target newTarget = blueprintTarget.copy();
        Filter filter = newTarget.getFilter();
        if (owner) {
            filter.add(new OwnerIdPredicate(opponentId));
            newTarget.withTargetName(filter.getMessage() + " (owned by " + opponent.getLogName() + ")");
        } else {
            filter.add(new ControllerIdPredicate(opponentId));
            newTarget.withTargetName(filter.getMessage() + " (controlled by " + opponent.getLogName() + ")");
        }
        ability.addTarget(newTarget);
    }
}
