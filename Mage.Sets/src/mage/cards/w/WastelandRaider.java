package mage.cards.w;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.common.SacrificeAllEffect;
import mage.constants.SubType;
import mage.abilities.keyword.SquadAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.StaticFilters;
import mage.filter.common.FilterCreaturePermanent;

/**
 *
 * @author notgreat
 */
public final class WastelandRaider extends CardImpl {

    public WastelandRaider(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{B}{B}");
        
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.MERCENARY);
        this.power = new MageInt(4);
        this.toughness = new MageInt(3);

        // Squad {2}
        this.addAbility(new SquadAbility(new GenericManaCost(2)));

        // When Wasteland Raider enters the battlefield, each player sacrifices a creature.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new SacrificeAllEffect(StaticFilters.FILTER_PERMANENT_A_CREATURE)));
    }

    private WastelandRaider(final WastelandRaider card) {
        super(card);
    }

    @Override
    public WastelandRaider copy() {
        return new WastelandRaider(this);
    }
}
