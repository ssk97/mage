
package mage.cards.b;

import mage.MageInt;
import mage.abilities.common.CounterRemovedFromSourceWhileExiledTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.common.PermanentsOnBattlefieldCount;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.continuous.SetBasePowerToughnessSourceEffect;
import mage.abilities.keyword.SuspendAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterControlledPermanent;
import mage.game.permanent.token.SoldierToken;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class BenalishCommander extends CardImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent("Soldiers you control");

    static {
        filter.add(SubType.SOLDIER.getPredicate());
    }

    public BenalishCommander(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{W}");
        this.subtype.add(SubType.HUMAN, SubType.SOLDIER);

        this.power = new MageInt(0);
        this.toughness = new MageInt(0);

        // Benalish Commander's power and toughness are each equal to the number of Soldiers you control.
        this.addAbility(new SimpleStaticAbility(Zone.ALL, new SetBasePowerToughnessSourceEffect(new PermanentsOnBattlefieldCount(filter))));

        // Suspend X-{X}{W}{W}. X can't be 0.
        this.addAbility(new SuspendAbility(Integer.MAX_VALUE, new ManaCostsImpl<>("{W}{W}"), this, true));

        // Whenever a time counter is removed from Benalish Commander while it's exiled, create a 1/1 white Soldier creature token.
        this.addAbility(new CounterRemovedFromSourceWhileExiledTriggeredAbility(CounterType.TIME, new CreateTokenEffect(new SoldierToken())));
    }

    private BenalishCommander(final BenalishCommander card) {
        super(card);
    }

    @Override
    public BenalishCommander copy() {
        return new BenalishCommander(this);
    }
}