package mage.target.targetadjustment;

import mage.abilities.Ability;
import mage.filter.Filter;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.util.Copyable;

import java.util.UUID;

/**
 *
 * @author notgreat
 */
public class EachOpponentPermanentTargetsAdjuster implements TargetAdjuster, Copyable<TargetAdjuster> {
    TargetPermanent baseTarget;

    public EachOpponentPermanentTargetsAdjuster(TargetPermanent baseTarget) {
        this.baseTarget = baseTarget;
    }

    protected EachOpponentPermanentTargetsAdjuster(final EachOpponentPermanentTargetsAdjuster targetAdjuster) {
        this.baseTarget = targetAdjuster.baseTarget.copy();
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        ability.getTargets().clear();
        for (UUID opponentId : game.getOpponents(ability.getControllerId())) {
            Player opponent = game.getPlayer(opponentId);
            if (opponent == null) {
                continue;
            }
            TargetPermanent newTarget = baseTarget.copy();
            Filter<Permanent> filter = newTarget.getFilter();
            filter.add(new ControllerIdPredicate(opponentId));
            if (newTarget.canChoose(ability.getControllerId(), ability, game)) {
                filter.setMessage(filter.getMessage()+" controlled by " + opponent.getLogName());
                ability.addTarget(newTarget);
            }
        }
    }

    @Override
    public EachOpponentPermanentTargetsAdjuster copy() {
        return new EachOpponentPermanentTargetsAdjuster(this);
    }
}
