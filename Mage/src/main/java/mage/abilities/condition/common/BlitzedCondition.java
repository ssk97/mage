package mage.abilities.condition.common;

import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.game.Game;
import mage.util.CardUtil;


/**
 * @author TheElk801
 */
public enum BlitzedCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        return CardUtil.getSourceCostTags(game, source).containsKey("Blitz");
    }
}
