package mage.abilities.effects.common.continuous;

import mage.abilities.Ability;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.keyword.MorphAbility;
import mage.cards.Card;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.game.stack.Spell;

import java.util.UUID;

/**
 * @author LevelX2
 */

public class CastAsThoughItHadFlashAllEffect extends AsThoughEffectImpl {

    private final FilterCard filter;
    private final boolean anyPlayer;

    public CastAsThoughItHadFlashAllEffect(Duration duration, FilterCard filter) {
        this(duration, filter, false);
    }

    public CastAsThoughItHadFlashAllEffect(Duration duration, FilterCard filter, boolean anyPlayer) {
        super(AsThoughEffectType.CAST_AS_INSTANT, duration, Outcome.Benefit);
        this.filter = filter;
        this.anyPlayer = anyPlayer;
        staticText = setText();
    }

    protected CastAsThoughItHadFlashAllEffect(final CastAsThoughItHadFlashAllEffect effect) {
        super(effect);
        this.filter = effect.filter;
        this.anyPlayer = effect.anyPlayer;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public CastAsThoughItHadFlashAllEffect copy() {
        return new CastAsThoughItHadFlashAllEffect(this);
    }

    @Override
    public boolean applies(UUID affectedSpellId, Ability source, UUID affectedControllerId, Game game) {
        if (anyPlayer || source.isControlledBy(affectedControllerId)) {
            Spell spell = game.getSpell(affectedSpellId);
            if (spell != null) {
                Card card = spell.getCardCharacteristics(game);
                if (card != null) {
                    return filter.match(card, affectedControllerId, source, game);
                }
            }
        }
        return false;
    }

    private String setText() {
        StringBuilder sb = new StringBuilder(anyPlayer ? "any player" : "you");
        sb.append(" may cast ");
        sb.append(filter.getMessage());
        if (!duration.toString().isEmpty()) {
            if (duration == Duration.EndOfTurn) {
                sb.append(" this turn");
            } else {
                sb.append(' ');
                sb.append(duration.toString());
            }
        }
        return sb.append(" as though they had flash").toString();
    }
}
