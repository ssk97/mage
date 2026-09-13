package mage.abilities.effects.common.continuous;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.SubLayer;
import mage.filter.FilterPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.Targets;
import mage.target.targetpointer.FilterAllPermanentsTargetPointer;
import mage.target.targetpointer.TargetPointer;
import mage.util.CardUtil;

import java.util.Locale;
import java.util.UUID;

/**
 * @author notgreat, BetaSteward_at_googlemail.com, North
 */
public class BoostGenericEffect extends ContinuousEffectImpl {

    private DynamicValue power;
    private DynamicValue toughness;

    public BoostGenericEffect(int power, int toughness) {
        this(power, toughness, Duration.EndOfTurn);
    }

    public BoostGenericEffect(int power, int toughness, Duration duration) {
        this(StaticValue.get(power), StaticValue.get(toughness), duration);
    }

    public BoostGenericEffect(DynamicValue power, DynamicValue toughness) {
        this(power, toughness, Duration.EndOfTurn);
    }

    public BoostGenericEffect(DynamicValue power, DynamicValue toughness, Duration duration) {
        super(duration, Layer.PTChangingEffects_7, SubLayer.ModifyPT_7c, CardUtil.getBoostOutcome(power, toughness));
        this.power = power;
        this.toughness = toughness;
        this.staticText = null; // so that setText("") can suppress the generated text
    }

    protected BoostGenericEffect(final BoostGenericEffect effect) {
        super(effect);
        this.power = effect.power.copy();
        this.toughness = effect.toughness.copy();
    }

    @Override
    public BoostGenericEffect copy() {
        return new BoostGenericEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        if (getAffectedObjectsSet()) {
            // Boost must be locked in (if it's a dynamic value) for non-static ability
            power = StaticValue.get(power.calculate(game, source, this));
            toughness = StaticValue.get(toughness.calculate(game, source, this));
        }
    }

    @Override
    public boolean apply(Game game, Ability source) {
        int affectedTargets = 0;
        for (UUID permanentId : getTargetPointer().getTargets(game, source)) {
            Permanent target = game.getPermanent(permanentId);
            if (target != null && target.isCreature(game)) {
                target.addPower(power.calculate(game, source, this));
                target.addToughness(toughness.calculate(game, source, this));
                affectedTargets++;
            }
        }
        return affectedTargets > 0;
    }

    /**
     * Whether a filter message needs " you control" appended to it. The two readings are not
     * interchangeable: "Skeletons you control and other Zombies" must gain the suffix because its
     * trailing noun has no controller, while "Permanents you control with counters on them" must not.
     */
    public enum ControlSuffix {
        /** Never append; the filter message stands on its own. */
        NONE,
        /** Append unless the message already ends with it, or carries a "you control that's" clause. */
        UNLESS_TRAILING,
        /** Append only when the message never mentions "you control" at all. */
        UNLESS_MENTIONED
    }

    /**
     * Builds the target description for an effect that acts on every permanent matching a filter,
     * as "other creatures you control". An "each ..." filter already names a single permanent, so
     * it never takes the "other " prefix.
     */
    public static String describeFiltered(FilterPermanent filter, boolean excludeSource, ControlSuffix controlSuffix) {
        String message = filter.getMessage();
        String lower = message.toLowerCase(Locale.ENGLISH);
        StringBuilder sb = new StringBuilder();
        if (excludeSource && !lower.startsWith("each")
                && !(controlSuffix != ControlSuffix.NONE && lower.startsWith("all "))) {
            sb.append("other ");
        }
        sb.append(message);
        if (needsControlSuffix(message, controlSuffix)) {
            sb.append(" you control");
        }
        return sb.toString();
    }

    private static boolean needsControlSuffix(String message, ControlSuffix controlSuffix) {
        switch (controlSuffix) {
            case UNLESS_TRAILING:
                return !message.endsWith("you control") && !message.contains("you control that's");
            case UNLESS_MENTIONED:
                return !message.contains("you control");
            default:
                return false;
        }
    }

    /**
     * Whether the described targets take plural verbs. A filter whose message starts with "each"
     * describes one permanent at a time, so it stays singular ("each creature gets", not "get").
     */
    public static boolean isPluralTarget(TargetPointer targetPointer, Targets targets) {
        if (targetPointer instanceof FilterAllPermanentsTargetPointer) {
            String description = targetPointer.getTargetDescription();
            return description == null || !description.toLowerCase(Locale.ENGLISH).startsWith("each");
        }
        return targetPointer.isPlural(targets);
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null) {
            return staticText;
        }
        String getStr;
        if (!isPluralTarget(getTargetPointer(), mode.getTargets())) {
            getStr = " gets ";
        } else if (getTargetPointer() instanceof FilterAllPermanentsTargetPointer) {
            getStr = " get ";
        } else {
            getStr = " each get ";
        }
        String describedTargets = getTargetPointer().describeTargets(mode.getTargets(), "it");
        return describedTargets + (describedTargets.isEmpty() ? getStr.substring(1) : getStr)
                + CardUtil.getBoostText(power, toughness, duration);
    }
}
