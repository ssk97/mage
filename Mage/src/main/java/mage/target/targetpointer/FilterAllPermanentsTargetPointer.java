package mage.target.targetpointer;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.filter.FilterPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.Targets;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FilterAllPermanentsTargetPointer extends TargetPointerImpl {
    private final FilterPermanent filter;
    private final boolean fixTargets;
    private List<MageObjectReference> affectedObjectList = null;

    /**
     * Target pointer that always "targets" all permanents that match the given filter
     */
    public FilterAllPermanentsTargetPointer(FilterPermanent filter, boolean fixTargets) {
        super();
        this.filter = filter;
        this.fixTargets = fixTargets;
    }
    public FilterAllPermanentsTargetPointer(final FilterAllPermanentsTargetPointer other) {
        super(other);
        this.filter = other.filter;
        this.fixTargets = other.fixTargets;
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
        if (fixTargets){
            if (affectedObjectList == null) {
                affectedObjectList = game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source, game)
                        .stream().map(x -> new MageObjectReference(x, game)).collect(Collectors.toList());
            }
            return affectedObjectList.stream().filter(x -> x.zoneCounterIsCurrent(game))
                    .map(MageObjectReference::getSourceId).collect(Collectors.toList());
        } else {
            return game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source, game)
                .stream().map(Permanent::getId).collect(Collectors.toList());
        }
    }

    @Override
    public UUID getFirst(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target on FilterAllPermanentsTargetPointer (bad Effect usage)");
    }

    @Override
    public FilterAllPermanentsTargetPointer copy() {
        return new FilterAllPermanentsTargetPointer(this);
    }

    @Override
    public Permanent getFirstTargetPermanentOrLKI(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target (or LKI) on FilterAllPermanentsTargetPointer (bad Effect usage)");
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isPlural(Targets targets) {
        return true;
    }
}
