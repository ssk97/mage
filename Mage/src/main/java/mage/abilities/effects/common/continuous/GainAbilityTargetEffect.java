package mage.abilities.effects.common.continuous;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.CompoundAbility;
import mage.abilities.Mode;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.cards.Card;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.*;

/**
 * @author JayDi85
 */
public class GainAbilityTargetEffect extends ContinuousEffectImpl {

    protected CompoundAbility abilities;

    // shall a card gain the ability (otherwise a permanent)
    private final boolean useOnCard; // only one card per ability supported
    private boolean waitingCardPermanent = false; // wait the permanent from card's resolve (for inner usage only)

    protected String targetObjectName = null;
    protected boolean durationRuleAtStart = false; // put duration rule to the start of the rules instead end

    public GainAbilityTargetEffect(Ability ability) {
        this(ability, Duration.EndOfTurn);
    }

    public GainAbilityTargetEffect(Ability ability, Duration duration) {
        this(ability, duration, null);
    }

    public GainAbilityTargetEffect(Ability ability, Duration duration, String rule) {
        this(ability, duration, rule, false);
    }
    public GainAbilityTargetEffect(Ability ability, Duration duration, boolean useOnCard) {
        this(ability, duration, null, useOnCard);
    }

    public GainAbilityTargetEffect(Ability ability, Duration duration, String rule, boolean useOnCard) {
        this(new CompoundAbility(ability), duration, rule, useOnCard);
    }
    public GainAbilityTargetEffect(CompoundAbility abilities, Duration duration) {
        this(abilities, duration, null, false);
    }
    public GainAbilityTargetEffect(CompoundAbility abilities, Duration duration, String rule, boolean useOnCard) {
        super(duration, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.abilities = abilities.copy();

        this.staticText = rule;
        this.useOnCard = useOnCard;

        this.generateGainAbilityDependencies(abilities, null);
    }

    protected GainAbilityTargetEffect(final GainAbilityTargetEffect effect) {
        super(effect);
        this.abilities = effect.abilities.copy(); // See the method's comment, ability.copy() is not enough.
        this.useOnCard = effect.useOnCard;
        this.waitingCardPermanent = effect.waitingCardPermanent;
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);

        // must support dynamic targets from static ability and static targets from activated abilities
        if (getAffectedObjectsSet()) {
            // target permanents (by default)
            getTargetPointer().getTargets(game, source)
                    .stream()
                    .map(game::getPermanent)
                    .filter(Objects::nonNull)
                    .forEach(permanent -> {
                        this.affectedObjectList.add(new MageObjectReference(permanent, game));
                    });

            // target cards with linked permanents
            if (this.useOnCard) {
                getTargetPointer().getTargets(game, source)
                        .stream()
                        .map(game::getCard)
                        .filter(Objects::nonNull)
                        .forEach(card -> {
                            this.affectedObjectList.add(new MageObjectReference(card, game));
                        });
                waitingCardPermanent = true;
                if (this.affectedObjectList.size() > 1) {
                    throw new IllegalArgumentException("Gain ability can't target a multiple cards (unsupported)");
                }
            }
        }
    }

    @Override
    public GainAbilityTargetEffect copy() {
        return new GainAbilityTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        int affectedTargets = 0;
        if (getAffectedObjectsSet()) {
            // STATIC TARGETS
            List<MageObjectReference> newWaitingPermanents = new ArrayList<>();
            for (Iterator<MageObjectReference> it = affectedObjectList.iterator(); it.hasNext(); ) {
                MageObjectReference mor = it.next();

                // look for permanent
                Permanent permanent = mor.getPermanent(game);
                if (permanent != null) {
                    this.waitingCardPermanent = false;
                    for (Ability ability:abilities) {
                        permanent.addAbility(ability, source.getSourceId(), game);
                        afterGain(game, source, permanent, ability);
                    }
                    affectedTargets++;
                    continue;
                }

                // look for card with linked permanent
                if (this.useOnCard) {
                    Card card = mor.getCard(game);
                    if (card != null) {
                        for (Ability ability:abilities) {
                            game.getState().addOtherAbility(card, ability);
                        }
                        affectedTargets++;
                        continue;
                    } else {
                        // start waiting a spell's permanent (example: Tyvar Kell's emblem)
                        Permanent perm = game.getPermanent(mor.getSourceId());
                        if (perm != null) {
                            for (Ability ability:abilities) {
                                perm.addAbility(ability, source.getSourceId(), game);
                                afterGain(game, source, perm, ability);
                            }
                            affectedTargets++;
                            newWaitingPermanents.add(new MageObjectReference(perm, game));
                            this.waitingCardPermanent = false;
                        }
                    }
                }
                // bad target, can be removed
                it.remove();
            }

            // add new linked permanents to targets
            if (!newWaitingPermanents.isEmpty()) {
                this.affectedObjectList.addAll(newWaitingPermanents);
                return affectedTargets > 0;
            }

            // no more valid targets
            if (this.affectedObjectList.isEmpty()) {
                discard();
            }

            // no more valid permanents (card was countered without new permanent)
            if (duration == Duration.Custom && affectedTargets == 0 && !this.waitingCardPermanent) {
                discard();
            }
        } else {
            // DYNAMIC TARGETS
            for (UUID objectId : getTargetPointer().getTargets(game, source)) {
                Permanent permanent = game.getPermanent(objectId);
                if (permanent != null) {
                    for (Ability ability:abilities) {
                        permanent.addAbility(ability, source.getSourceId(), game);
                        afterGain(game, source, permanent, ability);
                    }
                    affectedTargets++;
                    continue;
                }
                if (this.useOnCard) {
                    Card card = game.getCard(objectId);
                    if (card != null) {
                        for (Ability ability:abilities) {
                            game.getState().addOtherAbility(card, ability);
                        }
                        affectedTargets++;
                    }
                }
            }
        }
        return affectedTargets > 0;
    }
    /**
     * Calls after ability gain. Override it to apply additional data (example: transfer ability's settings from original to destination source)
     *
     * @param game
     * @param source
     * @param permanent
     * @param addedAbility
     */
    public void afterGain(Game game, Ability source, Permanent permanent, Ability addedAbility) {
        //
    }

    public GainAbilityTargetEffect withDurationRuleAtStart(boolean durationRuleAtStart) {
        this.durationRuleAtStart = durationRuleAtStart;
        return this;
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null && !staticText.isEmpty()) {
            return staticText;
        }
        StringBuilder sb = new StringBuilder();
        if (durationRuleAtStart && !duration.toString().isEmpty() && duration != Duration.EndOfGame) {
            sb.append(duration).append(", ");
        }
        sb.append(getTargetPointer().describeTargets(mode.getTargets(), "it"));
        if (duration == Duration.WhileOnBattlefield) {
            sb.append(getTargetPointer().isPlural(mode.getTargets()) ? " has " : " have ");
        } else {
            sb.append(getTargetPointer().isPlural(mode.getTargets()) ? " gain " : " gains ");
        }
        sb.append(abilities.getMultiRule(targetObjectName));
        if (!durationRuleAtStart && !duration.toString().isEmpty() && duration != Duration.EndOfGame) {
            sb.append(' ').append(duration);
        }
        return sb.toString();
    }
}
