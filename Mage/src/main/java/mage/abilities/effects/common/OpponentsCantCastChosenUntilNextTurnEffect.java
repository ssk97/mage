package mage.abilities.effects.common;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.util.CardUtil;

import java.util.Optional;

/**
 * This effect must be used in tandem with ChooseACardNameEffect
 */
public class OpponentsCantCastChosenUntilNextTurnEffect extends ContinuousRuleModifyingEffectImpl {

    public OpponentsCantCastChosenUntilNextTurnEffect() {
        super(Duration.UntilYourNextTurn, Outcome.Benefit);
        staticText = "Until your next turn, your opponents can't cast spells with the chosen name";
    }

    protected OpponentsCantCastChosenUntilNextTurnEffect(final OpponentsCantCastChosenUntilNextTurnEffect effect) {
        super(effect);
    }

    @Override
    public OpponentsCantCastChosenUntilNextTurnEffect copy() {
        return new OpponentsCantCastChosenUntilNextTurnEffect(this);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        MageObject mageObject = game.getObject(source);
        String cardName = (String) game.getState().getValue(source.getSourceId().toString() + ChooseACardNameEffect.INFO_KEY);
        if (mageObject != null && cardName != null) {
            return "You can't cast a card named " + cardName + " (" + mageObject.getIdName() + ").";
        }
        return null;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.CAST_SPELL_LATE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Optional<Ability> maybeSpellAbility = game.getAbility(event.getTargetId(),event.getSourceId());
        String cardName = (String) game.getState().getValue(source.getSourceId().toString() + ChooseACardNameEffect.INFO_KEY);
        return maybeSpellAbility.filter(ability ->
                CardUtil.haveSameNames(((SpellAbility) ability).getCharacteristics(game), cardName, game)).isPresent();
    }
}
