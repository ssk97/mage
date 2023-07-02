package mage.abilities.keyword;

import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.StaticAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.costs.*;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.CreateTokenCopySourceEffect;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;
import mage.util.CardUtil;

import java.util.Map;

/**
 * @author notgreat
 */
public class SquadAbility extends EntersBattlefieldTriggeredAbility {
    public SquadAbility() {
        super(new SquadEffectETB());
        addSubAbility(new SquadCostAbility());
		this.setRuleVisible(false);
    }

    private SquadAbility(final SquadAbility ability) {
        super(ability);
    }
    @Override
    public SquadAbility copy() {
        return new SquadAbility(this);
    }

    @Override
    public boolean checkInterveningIfClause(Game game) {
        Map<String, Integer> costTags = CardUtil.getSourceCostTags(game, this);
        if (costTags != null) {
            int squadCount = costTags.getOrDefault("Squad",0);
            if (squadCount > 0) {
                SquadEffectETB effect = (SquadEffectETB) getEffects().get(0);
                effect.activationCount = squadCount;
                return true;
            }
        }
        return false;
    }
    @Override
    public String getRule() {
        return "Squad <i>(When this creature enters the battlefield, if its squad cost was paid, "
                + "create a token that is a copy of it for each time its squad cost was paid.)</i>";
    }
}

class SquadEffectETB extends OneShotEffect {
    Integer activationCount;

    SquadEffectETB() {
        super(Outcome.Benefit);
    }

    private SquadEffectETB(final SquadEffectETB effect) {
        super(effect);
        this.activationCount = effect.activationCount;
    }

    @Override
    public SquadEffectETB copy() {
        return new SquadEffectETB(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        if (activationCount != null) {
            CreateTokenCopySourceEffect effect = new CreateTokenCopySourceEffect(activationCount);
            return effect.apply(game, source);
        }
        return true;
    }
}

class SquadCostAbility extends StaticAbility implements OptionalAdditionalSourceCosts {
    protected OptionalAdditionalCost cost;
    protected static final String SQUAD_KEYWORD = "Squad";
    protected static final String SQUAD_REMINDER = "You may pay an additional "
            + "{cost} any number of times as you cast this spell.";
    public SquadCostAbility() {
        this(new GenericManaCost(2));
    }

    public SquadCostAbility(Cost cost) {
        super(Zone.STACK, null);
        setSquadCost(cost);
    }

    private SquadCostAbility(final SquadCostAbility ability) {
        super(ability);
        this.cost = ability.cost.copy();
    }

    @Override
    public SquadCostAbility copy() {
        return new SquadCostAbility(this);
    }

    public final void setSquadCost(Cost cost) {
        OptionalAdditionalCost newCost = new OptionalAdditionalCostImpl(
                SQUAD_KEYWORD, SQUAD_REMINDER, cost);
        newCost.setRepeatable(true);
        newCost.setCostType(VariableCostType.ADDITIONAL);
        this.cost = newCost;
    }

    private void reset() {
        cost.reset();
    }

    @Override
    public void addOptionalAdditionalCosts(Ability ability, Game game) {
        if (!(ability instanceof SpellAbility)) {
            return;
        }
        Player player = game.getPlayer(ability.getControllerId());
        if (player == null) {
            return;
        }
        this.reset();
        boolean again = true;
        while (player.canRespond() && again) {
            String times = "";
            int activatedCount = cost.getActivateCount();
            times = (activatedCount + 1) + (activatedCount == 0 ? " time " : " times ");
            // TODO: add AI support to find max number of possible activations (from available mana)
            //  canPay checks only single mana available, not total mana usage
            if (cost.canPay(ability, this, ability.getControllerId(), game)
                    && player.chooseUse(/*Outcome.Benefit*/Outcome.AIDontUseIt,
                    "Pay " + times + cost.getText(false) + " ?", ability, game)) {
                cost.activate();
                if (cost instanceof ManaCostsImpl) {
                    ability.getManaCostsToPay().add((ManaCostsImpl) cost.copy());
                } else {
                    ability.getCosts().add(cost.copy());
                }
            } else {
                again = false;
            }
        }
        ability.getCostsTagMap().put("Squad",cost.getActivateCount());
    }

    @Override
    public String getCastMessageSuffix() {
        if (cost.isActivated()) {
            return cost.getCastSuffixMessage(0);
        }
        return "";
    }

    @Override
    public String getRule() {
        return "Squad "+cost.getText()+" <i>(As an additional cost to cast this spell, you may pay "+
            cost.getText()+"any number of times. When this creature enters the battlefield, "+
            "create that many tokens that are copies of it.)</i>";
    }
}