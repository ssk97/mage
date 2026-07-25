package mage.target.targetpointer;

import mage.abilities.Ability;
import mage.filter.FilterPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FilterAllPermanentsTargetPointer extends TargetPointerImpl {
    private FilterPermanent filter;

    /**
     * Target pointer that always "targets" the source of the ability.
     * WARNING: Do NOT use with MageSingleton abilities
     */
    public FilterAllPermanentsTargetPointer() {
        super();
    }
    public FilterAllPermanentsTargetPointer(final FilterAllPermanentsTargetPointer other) {
        super(other);
        filter = other.filter;
        setTargetDescription(filter.getMessage());
    }


    @Override
    public void init(Game game, Ability source) {
        if (isInitialized()) {
            return;
        }
        setInitialized();
    }

    /**
     * This returns a list of the targetIds (but only if the targets are still
     * have the same zoneChangeCounter). So if the target has changed zone
     * meanwhile there is no id returned for this target and the list is empty.
     *
     * @param game
     * @param source
     * @return
     */
    @Override
    public List<UUID> getTargets(Game game, Ability source) {
        return game.getBattlefield().getAllActivePermanents(filter, source.getControllerId(), game)
                .stream().map(Permanent::getId).collect(Collectors.toList());
    }

    @Override
    public UUID getFirst(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target on FilterAllPermanentsTargetPointer");
    }

    @Override
    public FilterAllPermanentsTargetPointer copy() {
        return new FilterAllPermanentsTargetPointer(this);
    }

    @Override
    public Permanent getFirstTargetPermanentOrLKI(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target (or LKI) on FilterAllPermanentsTargetPointer");
    }
}
