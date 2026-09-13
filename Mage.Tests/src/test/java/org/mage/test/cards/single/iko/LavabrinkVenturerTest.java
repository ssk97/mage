package org.mage.test.cards.single.iko;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * Lavabrink Venturer {2}{W} 3/3
 * As Lavabrink Venturer enters the battlefield, choose odd or even.
 * Lavabrink Venturer has protection from each mana value of the chosen quality.
 *
 * @author code-review
 */
public class LavabrinkVenturerTest extends CardTestPlayerBase {

    /**
     * Having chosen "even", damage from a mana value 4 creature must be prevented.
     */
    @Test
    public void test_protectionFromEvenManaValues() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 3);
        addCard(Zone.HAND, playerA, "Lavabrink Venturer");
        addCard(Zone.BATTLEFIELD, playerB, "Hill Giant"); // {3}{R}, mana value 4, 3/3

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lavabrink Venturer");
        setChoice(playerA, "even");

        attack(2, playerB, "Hill Giant");
        block(2, playerA, "Lavabrink Venturer", "Hill Giant");

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.END_TURN);
        execute();

        // protection prevents the Giant's damage, but not the Venturer's
        assertPermanentCount(playerA, "Lavabrink Venturer", 1);
        assertGraveyardCount(playerB, "Hill Giant", 1);
    }
}
