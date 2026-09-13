package org.mage.test.cards.single.cmd;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * Archangel of Strife {5}{W}{W} 6/6
 * As Archangel of Strife enters the battlefield, each player chooses war or peace.
 * Creatures controlled by players who chose war get +3/+0.
 * Creatures controlled by players who chose peace get +0/+3.
 * <p>
 * The two boosts are filtered per creature by what that creature's *controller* chose, which no
 * other card does.
 *
 * @author code-review
 */
public class ArchangelOfStrifeTest extends CardTestPlayerBase {

    @Test
    public void test_eachBoostFollowsItsControllersChoice() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 7);
        addCard(Zone.HAND, playerA, "Archangel of Strife");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion"); // 2/2
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");   // 2/2

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Archangel of Strife");
        setChoice(playerA, "war");
        setChoice(playerB, "peace");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 2 + 3, 2);
        assertPowerToughness(playerA, "Archangel of Strife", 6 + 3, 6);
        assertPowerToughness(playerB, "Grizzly Bears", 2, 2 + 3);
    }

    /**
     * Swapping the choices swaps the boosts -- neither is tied to the Archangel's controller.
     */
    @Test
    public void test_choicesAreIndependentOfWhoCastIt() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 7);
        addCard(Zone.HAND, playerA, "Archangel of Strife");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Archangel of Strife");
        setChoice(playerA, "peace");
        setChoice(playerB, "war");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 2, 2 + 3);
        assertPowerToughness(playerA, "Archangel of Strife", 6, 6 + 3);
        assertPowerToughness(playerB, "Grizzly Bears", 2 + 3, 2);
    }

    /**
     * A static ability keeps a dynamic set, so a creature cast afterwards is boosted too.
     */
    @Test
    public void test_creatureCastLaterIsBoosted() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 9);
        addCard(Zone.HAND, playerA, "Archangel of Strife");
        addCard(Zone.HAND, playerA, "Silvercoat Lion"); // {1}{W}

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Archangel of Strife");
        setChoice(playerA, "war");
        setChoice(playerB, "peace");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPowerToughness(playerA, "Silvercoat Lion", 2 + 3, 2);
    }
}
