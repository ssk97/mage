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
    private final boolean allowCard;

    /**
     * Target pointer that always "targets" whatever the source of the ability is to.
     */
    public SourceTargetPointer() {
        this(false);
    }
    public SourceTargetPointer(boolean fixTarget) {
        this(fixTarget, false);
    }

    /**
     * @param allowCard also point at the source while it is not a permanent, for effects that grant
     *                  an ability to the source card itself (a spell on the stack, a card in hand)
     */
    public SourceTargetPointer(boolean fixTarget, boolean allowCard) {
        super();
        this.fixTarget = fixTarget;
        this.allowCard = allowCard;
        this.targetDescription = "{this}";
    }
    public SourceTargetPointer(final SourceTargetPointer other) {
        super(other);
        this.fixTarget = other.fixTarget;
        this.allowCard = other.allowCard;
        this.mor = other.mor;
    }


    @Override
    public void init(Game game, Ability source) {
        if (isInitialized()) {
            return;
        }
        // resolve now, while the source is still findable
        if (fixTarget) {
            if (game.getPermanentEntering(source.getSourceId()) != null) {
                // the permanent is still on its way in, so reference the zone it is about to reach
                mor = new MageObjectReference(source.getSourceId(),
                        game.getState().getZoneChangeCounter(source.getSourceId()) + 1, game);
            } else {
                Permanent permanent = source.getSourcePermanentIfItStillExists(game);
                if (permanent != null) {
                    mor = new MageObjectReference(permanent, game);
                }
            }
        }
        setInitialized();
    }

    @Override
    public List<UUID> getTargets(Game game, Ability source) {
        Permanent permanent = (mor == null) ? source.getSourcePermanentIfItStillExists(game) : mor.getPermanent(game);
        if (permanent == null) {
            // the source is not a permanent: only an ability granted to the card itself still applies
            if (allowCard && source.getSourceId() != null) {
                List<UUID> cardList = new ArrayList<>();
                cardList.add(source.getSourceId());
                return cardList;
            }
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
