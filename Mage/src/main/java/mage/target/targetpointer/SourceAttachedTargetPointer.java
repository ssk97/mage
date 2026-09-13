package mage.target.targetpointer;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SourceAttachedTargetPointer extends TargetPointerImpl {
    private MageObjectReference mor;
    private final boolean fixTarget;

    /**
     * Target pointer that always "targets" whatever the source of the ability is attached to.
     */
    public SourceAttachedTargetPointer(boolean fixTarget, String description) {
        super();
        this.fixTarget = fixTarget;
        this.targetDescription = description;
    }
    public SourceAttachedTargetPointer(final SourceAttachedTargetPointer other) {
        super(other);
        this.fixTarget = other.fixTarget;
        this.mor = other.mor;
    }


    @Override
    public void init(Game game, Ability source) {
        if (isInitialized()) {
            return;
        }
        // An ability that sacrifices its own Aura/Equipment as a cost has already lost the
        // permanent by the time it resolves, so the attachment is only reachable through LKI.
        if (fixTarget) {
            Permanent attachment = game.getPermanentOrLKIBattlefield(source.getSourceId());
            if (attachment != null && attachment.getAttachedTo() != null) {
                mor = new MageObjectReference(attachment.getAttachedTo(), game);
            }
        }
        setInitialized();
    }

    @Override
    public List<UUID> getTargets(Game game, Ability source) {
        UUID attached = null;
        if (mor == null) {
            Permanent permanent = source.getSourcePermanentIfItStillExists(game);
            if (permanent == null) {
                return Collections.emptyList();
            }
            attached = permanent.getAttachedTo();
        } else if (mor.zoneCounterIsCurrent(game)) {
            attached = mor.getSourceId();
        }
        if (attached == null) {
            return Collections.emptyList();
        }
        List<UUID> list = new ArrayList<>();
        list.add(attached);
        return list;
    }

    @Override
    public UUID getFirst(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target on SourceAttachedTargetPointer (bad Effect usage)");
    }

    @Override
    public SourceAttachedTargetPointer copy() {
        return new SourceAttachedTargetPointer(this);
    }

    @Override
    public Permanent getFirstTargetPermanentOrLKI(Game game, Ability source) {
        throw new IllegalStateException("Attempted to get first target or LKI on SourceAttachedTargetPointer (bad Effect usage)");
    }
    @Override
    public boolean isSpecial() {
        return true;
    }
}
