package mage.cards.l;

import mage.abilities.Ability;
import mage.abilities.condition.common.GiftWasPromisedCondition;
import mage.abilities.effects.common.CounterTargetEffect;
import mage.abilities.keyword.GiftAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.GiftType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.target.TargetSpell;
import mage.target.targetadjustment.TargetAdjuster;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class LongRiversPull extends CardImpl {

    public LongRiversPull(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}{U}");

        // Gift a card
        this.addAbility(new GiftAbility(this, GiftType.CARD));

        // Counter target creature spell. If the gift was promised, instead counter target spell.
        this.getSpellAbility().addEffect(new CounterTargetEffect()
                .setText("counter target creature spell. If the gift was promised, instead counter target spell"));
        this.getSpellAbility().addTarget(new TargetSpell(StaticFilters.FILTER_SPELL_CREATURE));
        this.getSpellAbility().setTargetAdjuster(LongRiversPullAdjuster.instance);
    }

    private LongRiversPull(final LongRiversPull card) {
        super(card);
    }

    @Override
    public LongRiversPull copy() {
        return new LongRiversPull(this);
    }
}

enum LongRiversPullAdjuster implements TargetAdjuster {
    instance;

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (GiftWasPromisedCondition.TRUE.apply(game, ability)) {
            ability.getTargets().clear();
            ability.addTarget(new TargetSpell());
        }
    }
}
