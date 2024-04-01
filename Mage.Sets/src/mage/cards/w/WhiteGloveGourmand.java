package mage.cards.w;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfEndStepTriggeredAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.hint.ConditionHint;
import mage.abilities.hint.Hint;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.token.FoodToken;
import mage.game.permanent.token.HumanSoldierToken;
import mage.watchers.Watcher;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author notgreat
 */
public final class WhiteGloveGourmand extends CardImpl {

    public WhiteGloveGourmand(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{W}{B}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.NOBLE);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // When White Glove Gourmand enters the battlefield, create two 1/1 white Human Soldier creature tokens.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new CreateTokenEffect(new HumanSoldierToken(), 2)));

        // At the beginning of your end step, if another Human died under your control this turn, create a Food token.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(Zone.BATTLEFIELD, new CreateTokenEffect(new FoodToken()),
                        TargetController.YOU, WhiteGloveGourmandCondition.instance, false).addHint(WhiteGloveGourmandHint.instance),
                new WhiteGloveGourmandHumansDiedWatcher());
    }

    private WhiteGloveGourmand(final WhiteGloveGourmand card) {
        super(card);
    }

    @Override
    public WhiteGloveGourmand copy() {
        return new WhiteGloveGourmand(this);
    }
}

class WhiteGloveGourmandHumansDiedWatcher extends Watcher {

    private final Set<UUID> HumanDiedByController = new HashSet<>();

    WhiteGloveGourmandHumansDiedWatcher() {
        super(WatcherScope.GAME);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() != GameEvent.EventType.ZONE_CHANGE) {
            return;
        }
        ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
        if (!zEvent.isDiesEvent()
                || zEvent.getTarget() == null
                || !zEvent.getTarget().hasSubtype(SubType.HUMAN, game)) {
            return;
        }
        condition = true;
        HumanDiedByController.add(zEvent.getTarget().getControllerId());
    }

    @Override
    public void reset() {
        super.reset();
        HumanDiedByController.clear();
    }

    public boolean getHumanDiedThisTurnByController(UUID playerId) {
        return HumanDiedByController.contains(playerId);
    }
}

enum WhiteGloveGourmandCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        WhiteGloveGourmandHumansDiedWatcher watcher = game.getState().getWatcher(WhiteGloveGourmandHumansDiedWatcher.class);
        return watcher != null && watcher.getHumanDiedThisTurnByController(source.getControllerId());
    }

    @Override
    public String toString() {
        return "another Human died under your control this turn";
    }
}

enum WhiteGloveGourmandHint implements Hint {
    instance;
    private static final ConditionHint hint = new ConditionHint(
            WhiteGloveGourmandCondition.instance, "another Human died under your control this turn"
    );

    @Override
    public String getText(Game game, Ability ability) {
        return hint.getText(game, ability);
    }

    @Override
    public Hint copy() {
        return instance;
    }
}
