package mage.target.targetpointer;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SourceTargetPointer extends TargetPointerImpl {
    private MageObjectReference mor;
    private final boolean fixTarget;

    /**
     * Target pointer that always "targets" whatever the source of the ability is to.
     */
    public SourceTargetPointer() {
        this(false);
    }
    public SourceTargetPointer(boolean fixTarget) {
        super();
        this.fixTarget = fixTarget;
        this.targetDescription = "{this}";
    }
    public SourceTargetPointer(final SourceTargetPointer other) {
        super(other);
        this.fixTarget = other.fixTarget;
        this.mor = other.mor;
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
        if (fixTarget && mor == null) {
            Permanent permanent = source.getSourcePermanentIfItStillExists(game);
            if (permanent != null) {
                mor = new MageObjectReference(permanent, game);
            }
        }
        Permanent permanent = (mor == null) ? source.getSourcePermanentIfItStillExists(game) : mor.getPermanent(game);
        if (permanent == null) {
            return Collections.emptyList();
        }
        List<UUID> list = new ArrayList<>();
        list.add(permanent.getId());
        return list;
    }

    @Override
    public UUID getFirst(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target on SourceTargetPointer (bad Effect usage)");
    }

    @Override
    public SourceTargetPointer copy() {
        return new SourceTargetPointer(this);
    }

    @Override
    public Permanent getFirstTargetPermanentOrLKI(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target on SourceTargetPointer (bad Effect usage)");
    }
    @Override
    public boolean isSpecial() {
        return true;
    }
}
