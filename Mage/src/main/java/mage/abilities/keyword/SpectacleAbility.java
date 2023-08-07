package mage.abilities.keyword;

import mage.abilities.SpellAbility;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.dynamicvalue.common.OpponentsLostLifeCount;
import mage.abilities.hint.common.OpponentsLostLifeHint;
import mage.cards.Card;
import mage.constants.SpellAbilityType;
import mage.constants.Zone;
import mage.game.Game;

import java.util.UUID;

/**
 * @author TheElk801
 */
public class SpectacleAbility extends SpellAbility {

    public static final String SPECTACLE_ACTIVATION_VALUE_KEY = "spectacleActivation";

    private final String rule;

    public SpectacleAbility(Card card, ManaCost spectacleCosts) {
        super(card.getSpellAbility());
        this.newId();
        this.setCardName(card.getName() + " with spectacle");
        zone = Zone.HAND;
        spellAbilityType = SpellAbilityType.BASE_ALTERNATE;

        this.getManaCosts().clear();
        this.getManaCostsToPay().clear();
        this.addManaCost(spectacleCosts.copy());

        this.setRuleAtTheTop(true);
        this.rule = "Spectacle " + spectacleCosts.getText()
                + " <i>(You may cast this spell for its spectacle cost rather than its mana cost if an opponent lost life this turn.)</i>";
        this.addHint(OpponentsLostLifeHint.instance);
    }

    protected SpectacleAbility(final SpectacleAbility ability) {
        super(ability);
        this.rule = ability.rule;
    }

    @Override
    public ActivationStatus canActivate(UUID playerId, Game game) {
        if (OpponentsLostLifeCount.instance.calculate(game, playerId) > 0
                && super.canActivate(playerId, game).canActivate()) {
            return ActivationStatus.getTrue(this, game);
        }
        return ActivationStatus.getFalse();
    }

    @Override
    public boolean activate(Game game, boolean noMana) {
        if (super.activate(game, noMana)) {
            this.costsTagMap.put(SPECTACLE_ACTIVATION_VALUE_KEY,1);
            return true;
        }
        return false;
    }

    @Override
    public SpectacleAbility copy() {
        return new SpectacleAbility(this);
    }

    @Override
    public String getRule(boolean all) {
        return getRule();
    }

    @Override
    public String getRule() {
        return rule;
    }

}
