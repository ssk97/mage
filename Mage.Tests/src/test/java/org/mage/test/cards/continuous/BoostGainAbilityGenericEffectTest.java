package org.mage.test.cards.continuous;

import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.ForestwalkAbility;
import mage.abilities.keyword.HasteAbility;
import mage.abilities.keyword.LifelinkAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * Cards converted to BoostGainAbilityGenericEffect, which applies a boost in layer 7 and grants
 * abilities in layer 6 from a single effect. One case per target pointer shape.
 *
 * @author notgreat
 */
public class BoostGainAbilityGenericEffectTest extends CardTestPlayerBase {

    /**
     * Target creature gets +1/+0 and gains first strike until end of turn.
     */
    @Test
    public void testTargetCreature() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Kindled Fury");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Kindled Fury", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 3, 2);
        assertAbility(playerA, "Silvercoat Lion", FirstStrikeAbility.getInstance(), true);
    }

    /**
     * Both halves must expire together when the duration ends.
     */
    @Test
    public void testBothHalvesEndWithDuration() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Kindled Fury");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Kindled Fury", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 2, 2);
        assertAbility(playerA, "Silvercoat Lion", FirstStrikeAbility.getInstance(), false);
    }

    /**
     * Enchanted creature gets +2/+0 and has trample.
     */
    @Test
    public void testEnchanted() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 1);
        addCard(Zone.HAND, playerA, "Rancor");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Rancor", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 4, 2);
        assertAbility(playerA, "Silvercoat Lion", TrampleAbility.getInstance(), true);
    }

    /**
     * Equipped creature gets +3/+0 and has trample and lifelink -- two abilities from one effect.
     */
    @Test
    public void testEquippedWithTwoAbilities() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 3);
        addCard(Zone.BATTLEFIELD, playerA, "Loxodon Warhammer");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 5, 2);
        assertAbility(playerA, "Silvercoat Lion", TrampleAbility.getInstance(), true);
        assertAbility(playerA, "Silvercoat Lion", LifelinkAbility.getInstance(), true);
    }

    /**
     * Creatures you control get +3/+3 and gain trample until end of turn, and the opponent's do not.
     */
    @Test
    public void testCreaturesYouControl() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 5);
        addCard(Zone.HAND, playerA, "Overrun");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Overrun");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 5, 5);
        assertAbility(playerA, "Silvercoat Lion", TrampleAbility.getInstance(), true);

        assertPowerToughness(playerB, "Silvercoat Lion", 2, 2);
        assertAbility(playerB, "Silvercoat Lion", TrampleAbility.getInstance(), false);
    }

    /**
     * Other Elf creatures get +1/+1 and have forestwalk -- the lord must not boost itself.
     */
    @Test
    public void testLordExcludesItself() {
        addCard(Zone.BATTLEFIELD, playerA, "Elvish Champion");
        addCard(Zone.BATTLEFIELD, playerA, "Llanowar Elves");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Llanowar Elves", 2, 2);
        assertAbility(playerA, "Llanowar Elves", new ForestwalkAbility(), true);

        assertPowerToughness(playerA, "Elvish Champion", 2, 2);
        assertAbility(playerA, "Elvish Champion", new ForestwalkAbility(), false);
    }

    /**
     * Craterhoof Behemoth prints its abilities before the boost and uses a dynamic value:
     * creatures you control gain trample and get +X/+X, where X is the number of creatures you control.
     */
    @Test
    public void testAbilitiesBeforeDynamicBoost() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 8);
        addCard(Zone.HAND, playerA, "Craterhoof Behemoth");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Craterhoof Behemoth");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // two creatures on the battlefield once the Behemoth resolves, so +2/+2 each
        assertPowerToughness(playerA, "Silvercoat Lion", 4, 4);
        assertAbility(playerA, "Silvercoat Lion", TrampleAbility.getInstance(), true);
        assertPowerToughness(playerA, "Craterhoof Behemoth", 7, 7);
        assertAbility(playerA, "Craterhoof Behemoth", HasteAbility.getInstance(), true);
    }
}
