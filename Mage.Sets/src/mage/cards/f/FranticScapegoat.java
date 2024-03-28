package mage.cards.f;

import mage.MageInt;
import mage.MageItem;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.SuspectSourceEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.PermanentReferenceInCollectionPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeGroupEvent;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author notgreat
 */
public final class FranticScapegoat extends CardImpl {

    public FranticScapegoat(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{R}");
        
        this.subtype.add(SubType.GOAT);
        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // Haste
        this.addAbility(HasteAbility.getInstance());

        // When Frantic Scapegoat enters the battlefield, suspect it.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new SuspectSourceEffect()));

        // Whenever one or more other creatures enter the battlefield under your control, if Frantic Scapegoat is suspected, you may suspect one of the other creatures. If you do, Frantic Scapegoat is no longer suspected.
        this.addAbility(new FranticScapegoatTriggeredAbility());

    }

    private FranticScapegoat(final FranticScapegoat card) {
        super(card);
    }

    @Override
    public FranticScapegoat copy() {
        return new FranticScapegoat(this);
    }
}

//Based on Ingenious Artillerist and Lightmine Field
class FranticScapegoatTriggeredAbility extends TriggeredAbilityImpl {

    FranticScapegoatTriggeredAbility() {
        super(Zone.BATTLEFIELD, new FranticScapegoatSuspectEffect(), true);
    }

    private FranticScapegoatTriggeredAbility(final FranticScapegoatTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE_GROUP;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (!getSourcePermanentIfItStillExists(game).isSuspected()){
            return false;
        }
        ZoneChangeGroupEvent zEvent = (ZoneChangeGroupEvent) event;
        if (zEvent.getToZone() != Zone.BATTLEFIELD
                || !this.controllerId.equals(event.getPlayerId())) {
            return false;
        }
        Set<MageObjectReference> enteringCreatures = Stream.concat(
                zEvent.getTokens()
                        .stream(),
                zEvent.getCards()
                        .stream()
                        .map(MageItem::getId)
                        .map(game::getPermanent)
                        .filter(Objects::nonNull)
        ).filter(permanent -> permanent.isCreature(game)).map(s -> new MageObjectReference(s, game)).collect(Collectors.toSet());
        if (enteringCreatures.size() > 0) {
            this.getEffects().setValue("franticScapegoatEnteringCreatures", enteringCreatures);
            return true;
        }
        return false;
    }

    @Override
    public FranticScapegoatTriggeredAbility copy() {
        return new FranticScapegoatTriggeredAbility(this);
    }

    @Override
    public String getRule() {
        return "Whenever one or more other creatures enter the battlefield under your control, if {this} is suspected, you may suspect one of the other creatures. If you do, {this} is no longer suspected.";
    }
}
class FranticScapegoatSuspectEffect extends OneShotEffect {

    public FranticScapegoatSuspectEffect() {
        super(Outcome.Benefit);
        this.staticText = "Suspect one of the other creatures";
    }

    private FranticScapegoatSuspectEffect(final FranticScapegoatSuspectEffect effect) {
        super(effect);
    }

    @Override
    public FranticScapegoatSuspectEffect copy() {
        return new FranticScapegoatSuspectEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Set<MageObjectReference> enteringSet = (Set<MageObjectReference>) getValue("franticScapegoatEnteringCreatures");
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            if (enteringSet.size() > 1) {
                FilterCreaturePermanent filter = new FilterCreaturePermanent("one of those creatures");
                filter.add(new PermanentReferenceInCollectionPredicate(enteringSet));
                Target target = new TargetPermanent(filter);
                target.withNotTarget(true);
                if (target.canChoose(source.getControllerId(), source, game)) {
                    if (controller.choose(outcome, target, source, game)) {
                        game.getPermanent(target.getFirstTarget()).setSuspected(true, game, source);
                    }
                }
            } else {
                enteringSet.forEach(s -> s.getPermanent(game).setSuspected(true, game, source));
            }
        }
        return true;
    }
}
