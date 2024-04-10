
package mage.cards.s;

import mage.abilities.effects.common.CounterTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.target.TargetSpell;
import mage.target.targetadjustment.XMVAdjuster;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class SpellBlast extends CardImpl {

    public SpellBlast(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{X}{U}");

        // Counter target spell with converted mana cost X.
        this.getSpellAbility().addEffect(new CounterTargetEffect().setText("counter target spell with mana value X"));
        this.getSpellAbility().setTargetAdjuster(new XMVAdjuster());
        this.getSpellAbility().addTarget(new TargetSpell());
    }

    private SpellBlast(final SpellBlast card) {
        super(card);
    }

    @Override
    public SpellBlast copy() {
        return new SpellBlast(this);
    }
}
