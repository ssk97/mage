package mage.target.targetpointer;

import mage.MageObject;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.constants.Zone;
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
     * Target pointer that always "targets" whatever the source of the ability is  to.
     * WARNING: Do NOT use with MageSingleton abilities
     */
    public SourceTargetPointer() {
        this(false);
    }
    public SourceTargetPointer(boolean fixTarget) {
        super();
        this.fixTarget = fixTarget;
        setTargetDescription(" creature");
    }
    public SourceTargetPointer(final SourceTargetPointer other) {
        super(other);
        fixTarget = other.fixTarget;
        mor = other.mor;
    }


    @Override
    public void init(Game game, Ability source) {
        if (isInitialized()) {
            return;
        }
        if (fixTarget) {
            Permanent permanent = game.getPermanent(source.getSourceId());
            if (permanent != null) {
                mor = new MageObjectReference(permanent, game);
                setInitialized();
            }
        }
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
        Permanent permanent = (mor == null) ? game.getPermanent(source.getSourceId()) : mor.getPermanent(game);
        if (permanent == null) {
            return Collections.emptyList();
        }
        List<UUID> list = new ArrayList<>();
        list.add(permanent.getId());
        return list;
    }

    @Override
    public UUID getFirst(Game game, Ability source) {
        Permanent permanent = (mor == null) ? game.getPermanent(source.getSourceId()) : mor.getPermanent(game);
        if (permanent == null) {
            return null;
        }
        return permanent.getId();
    }

    @Override
    public SourceTargetPointer copy() {
        return new SourceTargetPointer(this);
    }

    @Override
    public Permanent getFirstTargetPermanentOrLKI(Game game, Ability source) {
        init(game, source);
        Permanent permanent = (mor == null) ? game.getPermanent(source.getSourceId()) : mor.getPermanent(game);
        if  (permanent != null) {
            return permanent;
        }
        MageObject mageObject = game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD, source.getStackMomentSourceZCC());
        if (mageObject instanceof Permanent) {
            return (Permanent) mageObject;
        }
        return null;
    }
}
