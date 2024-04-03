package mage.cards.h;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.VariableCostImpl;
import mage.abilities.costs.VariableCostType;
import mage.abilities.costs.common.PayEnergyCost;
import mage.abilities.costs.common.PayLifeCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.counter.GetEnergyCountersControllerEffect;
import mage.abilities.mana.ColorlessManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.counters.CounterType;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetNonlandPermanent;
import mage.target.targetadjustment.XCMCPermanentAdjuster;

import java.util.UUID;

/**
 * @author notgreat
 */
public final class HELIOSOne extends CardImpl {

    public HELIOSOne(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        // {T}: Add {C}.
        this.addAbility(new ColorlessManaAbility());

        // {1}, {T}: You get {E}.
        Ability abilityEnergy = new SimpleActivatedAbility(new GetEnergyCountersControllerEffect(1), new GenericManaCost(1));
        abilityEnergy.addCost(new TapSourceCost());
        this.addAbility(abilityEnergy);

        // {3}, {T}, Pay X {E}, Sacrifice HELIOS One: Destroy target nonland permanent with mana value X. Activate only as a sorcery.
        Ability abilityDestroy = new SimpleActivatedAbility(new DestroyTargetEffect(), new GenericManaCost(3));
        abilityDestroy.addCost(new TapSourceCost());
        abilityDestroy.addCost(new HeliosPayVariableEnergyCost());
        abilityDestroy.addTarget(new TargetNonlandPermanent());
        abilityDestroy.setTargetAdjuster(XCMCPermanentAdjuster.instance);
        this.addAbility(abilityDestroy);
    }

    private HELIOSOne(final HELIOSOne card) {
        super(card);
    }

    @Override
    public HELIOSOne copy() {
        return new HELIOSOne(this);
    }
}

//Based on PayVariableLifeCost
class HeliosPayVariableEnergyCost extends VariableCostImpl {

    HeliosPayVariableEnergyCost() {
        this(false);
    }

    HeliosPayVariableEnergyCost(boolean useAsAdditionalCost) {
        super(useAsAdditionalCost ? VariableCostType.ADDITIONAL : VariableCostType.NORMAL,
                "{E} to pay");
        this.text = (useAsAdditionalCost ? "As an additional cost to cast this spell, pay " : "Pay ") +
                xText + ' ' + "{E}";
    }

    private HeliosPayVariableEnergyCost(final HeliosPayVariableEnergyCost cost) {
        super(cost);
    }

    @Override
    public HeliosPayVariableEnergyCost copy() {
        return new HeliosPayVariableEnergyCost(this);
    }

    @Override
    public Cost getFixedCostsFromAnnouncedValue(int xValue) {
        return new PayEnergyCost(xValue);
    }

    @Override
    public int getMaxValue(Ability source, Game game) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            return controller.getCounters().getCount(CounterType.ENERGY);
        }
        return 0;
    }

}