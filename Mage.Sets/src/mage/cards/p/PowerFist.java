package mage.cards.p;

import mage.abilities.Ability;
import mage.abilities.common.DealsCombatDamageToAPlayerTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.common.SavedDamageValue;
import mage.abilities.effects.common.continuous.GainAbilityAttachedEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.EquipAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AttachmentType;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.counters.CounterType;

import java.util.UUID;

/**
 * @author notgreat
 */
public final class PowerFist extends CardImpl {

    public PowerFist(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{1}{G}");

        this.subtype.add(SubType.EQUIPMENT);

        // Equipped creature has trample and "Whenever this creature deals combat damage to a player, put that many +1/+1 counters on it."
        Ability subability = new DealsCombatDamageToAPlayerTriggeredAbility(
                new AddCountersSourceEffect(
                        CounterType.P1P1.createInstance(),
                        SavedDamageValue.MANY, false
                ).setText("put that many +1/+1 counters on it"), false
        ).setTriggerPhrase("Whenever this creature deals combat damage to a player, ");

        Ability ability = new SimpleStaticAbility(new GainAbilityAttachedEffect(TrampleAbility.getInstance(), AttachmentType.EQUIPMENT));
        ability.addEffect(new GainAbilityAttachedEffect(subability, AttachmentType.EQUIPMENT).setText("and \"" + subability.getRule() + "\""));

        // Equip {2}
        this.abilities.add(new EquipAbility(2));
    }

    private PowerFist(final PowerFist card) {
        super(card);
    }

    @Override
    public PowerFist copy() {
        return new PowerFist(this);
    }
}
