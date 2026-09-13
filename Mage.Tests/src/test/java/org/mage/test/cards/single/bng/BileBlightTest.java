package org.mage.test.cards.single.bng;

import mage.constants.EmptyNames;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * Bile Blight {B}{B}
 * Target creature and all other creatures with the same name as that creature get -3/-3 until end
 * of turn.
 *
 * @author code-review
 */
public class BileBlightTest extends CardTestPlayerBase {

    @Test
    public void test_hitsEveryCreatureSharingTheName() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 2);
        addCard(Zone.HAND, playerA, "Bile Blight");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion", 2); // 2/2
        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");      // 2/2, different name

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Bile Blight", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // every Lion took -3/-3 whoever controls it, and whichever one was targeted
        assertGraveyardCount(playerA, "Silvercoat Lion", 2);
        assertGraveyardCount(playerB, "Silvercoat Lion", 1);
        assertPowerToughness(playerB, "Grizzly Bears", 2, 2);
    }

    /**
     * A face down creature has no name, so it shares one with nothing -- but it is still the target
     * and must take the -3/-3 itself.
     */
    @Test
    public void test_faceDownTargetIsHitAndSweepsNothingElse() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 3);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 2);
        addCard(Zone.HAND, playerA, "Pine Walker"); // Morph {4}{G}, face down for {3}
        addCard(Zone.HAND, playerA, "Bile Blight");
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        // morph on turn 1 and the Blight on turn 3, so its generic {3} cannot eat the black mana
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Pine Walker using Morph");
        castSpell(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Bile Blight",
                EmptyNames.FACE_DOWN_CREATURE.getTestCommand());

        setStrictChooseMode(true);
        setStopAt(3, PhaseStep.BEGIN_COMBAT);
        execute();

        // the 2/2 face down creature died, and went to the graveyard face up
        assertGraveyardCount(playerA, "Pine Walker", 1);
        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 0);
        assertPowerToughness(playerB, "Grizzly Bears", 2, 2);
    }
}
