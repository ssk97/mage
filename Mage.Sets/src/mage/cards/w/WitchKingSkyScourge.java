package mage.cards.w;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AttacksWithCreaturesTriggeredAbility;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTopXMayPlayUntilEndOfTurnEffect;
import mage.abilities.hint.Hint;
import mage.abilities.hint.ValueHint;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.UndyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.constants.TargetController;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterAttackingCreature;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author notgreat
 */
public final class WitchKingSkyScourge extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterAttackingCreature("Wraiths");

    static {
        filter.add(TargetController.YOU.getControllerPredicate());
        filter.add(SubType.WRAITH.getPredicate());
    }
    public WitchKingSkyScourge(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{5}{B}{R}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.WRAITH);
        this.subtype.add(SubType.NOBLE);
        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // Whenever you attack with one or more Wraiths, exile the top X cards of your library, where X is their total power. You may play those cards this turn.
        this.addAbility(new AttacksWithCreaturesTriggeredAbility(
                new ExileTopXMayPlayUntilEndOfTurnEffect(new TotalPermanentsPowerValue(filter))
                        .setText("exile the top X cards of your library, where X is their total power. You may play those cards this turn.")
                ,1,filter));

        // Undying
        this.addAbility(new UndyingAbility());

    }

    private WitchKingSkyScourge(final WitchKingSkyScourge card) {
        super(card);
    }

    @Override
    public WitchKingSkyScourge copy() {
        return new WitchKingSkyScourge(this);
    }
}

class TotalPermanentsPowerValue implements DynamicValue {

    private final FilterPermanent filter;

    public TotalPermanentsPowerValue(FilterPermanent filter) {
        this.filter = filter.copy();
    }

    private TotalPermanentsPowerValue(final TotalPermanentsPowerValue value) {
        this.filter = value.filter.copy();
    }

    @Override
    public TotalPermanentsPowerValue copy() {
        return new TotalPermanentsPowerValue(this);
    }

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        int totalPower = 0;
        List<Permanent> permanents = game.getBattlefield().getActivePermanents(
                filter,
                sourceAbility.getControllerId(),
                sourceAbility,
                game);
        for (Permanent permanent : permanents) {
            totalPower += permanent.getPower().getValue();
        }
        return totalPower;
    }

    @Override
    public String getMessage() {
        return "their total power";
    }

    @Override
    public String toString() {
        return "X";
    }

    public Hint getHint() {
        return new ValueHint("Total power of " + filter.getMessage(), this);
    }
}