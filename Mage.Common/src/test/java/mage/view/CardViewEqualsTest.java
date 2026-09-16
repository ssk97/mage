package mage.view;

import mage.constants.AbilityType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * cardViewEquals is the view half of the rendered card image cache key
 * (CardPanelRenderModeMTGO.ImageKey). Anything the renderers draw differently must be
 * compared here, or two zones showing the same card will share one rendered image.
 */
public class CardViewEqualsTest {

    @Test
    public void emptyViewsAreEqual() {
        assertThat(CardView.cardViewEquals(new CardView(true), new CardView(true))).isTrue();
    }

    @Test
    public void abilityIsNotEqualToItsSourceCard() {
        // the stack renders an ability by reusing the source card's view with this flag set,
        // so the two differ only here once the rule texts happen to match
        CardView card = new CardView(true);
        CardView ability = new CardView(true);
        ability.setIsAbility(true);

        assertThat(CardView.cardViewEquals(card, ability)).isFalse();
        assertThat(CardView.cardViewEquals(ability, card)).isFalse();
    }

    @Test
    public void abilityTypeIsCompared() {
        CardView triggered = new CardView(true);
        triggered.setIsAbility(true);
        triggered.setAbilityType(AbilityType.TRIGGERED_NONMANA);

        CardView activated = new CardView(true);
        activated.setIsAbility(true);
        activated.setAbilityType(AbilityType.ACTIVATED_NONMANA);

        assertThat(CardView.cardViewEquals(triggered, activated)).isFalse();
    }

    @Test
    public void abilitiesOfTheSameTypeAreEqual() {
        CardView one = new CardView(true);
        one.setIsAbility(true);
        one.setAbilityType(AbilityType.TRIGGERED_NONMANA);

        CardView two = new CardView(true);
        two.setIsAbility(true);
        two.setAbilityType(AbilityType.TRIGGERED_NONMANA);

        assertThat(CardView.cardViewEquals(one, two)).isTrue();
    }
}
