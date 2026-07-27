package mage.cards.l;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.ChooseModeEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.keyword.ProtectionAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.ModeChoice;
import mage.constants.SubType;
import mage.filter.FilterObject;
import mage.filter.predicate.mageobject.ManaValueParityPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class LavabrinkVenturer extends CardImpl {

    public LavabrinkVenturer(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{W}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.SOLDIER);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // As Lavabrink Venturer enters the battlefield, choose odd or even.
        this.addAbility(new AsEntersBattlefieldAbility(new ChooseModeEffect(ModeChoice.ODD, ModeChoice.EVEN)));

        // Lavabrink Venturer has protection from each converted mana cost of the chosen value.
        this.addAbility(new SimpleStaticAbility(new LavabrinkVenturerEffect()));
    }

    private LavabrinkVenturer(final LavabrinkVenturer card) {
        super(card);
    }

    @Override
    public LavabrinkVenturer copy() {
        return new LavabrinkVenturer(this);
    }
}

class LavabrinkVenturerEffect extends GainAbilitySourceEffect {

    private static final FilterObject nullFilter = new FilterObject("nothing");

    static {
        nullFilter.add(ManaValueParityPredicate.ODD);
        nullFilter.add(ManaValueParityPredicate.EVEN);
    }

    LavabrinkVenturerEffect() {
        super(new ProtectionAbility(nullFilter).setRuleVisible(false));
        staticText = "{this} has protection from each mana value of the chosen quality. <i>(Zero is even.)</i>";
    }

    private LavabrinkVenturerEffect(final LavabrinkVenturerEffect effect) {
        super(effect);
    }

    @Override
    public void afterGain(Game game, Ability source, Permanent permanent, Ability addedAbility) {
        //TODO: CHECK
        if (addedAbility instanceof ProtectionAbility) {
            if (ModeChoice.ODD.checkMode(game, source)) {
                ((ProtectionAbility)addedAbility).getFilter().add(ManaValueParityPredicate.ODD);
                ((ProtectionAbility)addedAbility).getFilter().setMessage("odd mana values");
            } else if (ModeChoice.EVEN.checkMode(game, source)) {
                ((ProtectionAbility)addedAbility).getFilter().add(ManaValueParityPredicate.EVEN);
                ((ProtectionAbility)addedAbility).getFilter().setMessage("even mana values");
            }
        }
    }

    @Override
    public LavabrinkVenturerEffect copy() {
        return new LavabrinkVenturerEffect(this);
    }
}
