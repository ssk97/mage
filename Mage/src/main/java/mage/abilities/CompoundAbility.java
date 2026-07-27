package mage.abilities;

import mage.abilities.common.LinkedEffectIdStaticAbility;
import mage.util.CardUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author noxx
 */
public class CompoundAbility extends AbilitiesImpl<Ability> {

    private String ruleText;

    public CompoundAbility(Ability... abilities) {
        this(null, abilities);
    }

    public CompoundAbility(String ruleText, Ability... abilities) {
        addAll(Arrays.asList(abilities));
    }

    /**
     * Copying the ability and providing ability is needed in a few situations,
     * The copy in order to have internal fields be proper to that ability in particular.
     * Id must be different for the copy, for a few things like the GainAbilityTargetEffect gained
     * by a clone, or in the case of an activated ability, called multiple times on the same target,
     * and thus the ability should be gained multiple times.
     */
    protected CompoundAbility(final CompoundAbility compoundAbility) {
        for (Ability ability : compoundAbility) {
            Ability abilityToCopy = ability.copy();
            abilityToCopy.newId();
            if (abilityToCopy instanceof LinkedEffectIdStaticAbility) {
                ((LinkedEffectIdStaticAbility) abilityToCopy).setEffectIdManually();
            }
            this.add(abilityToCopy);
        }
        this.ruleText = compoundAbility.ruleText;
    }

    public String getMultiRule(String targetObjectName) {
        if (ruleText != null) {
            return ruleText;
        }

        StringBuilder sb = new StringBuilder();
        List<String> rules = new ArrayList<>();
        for (Ability ability : this) {
            String rule;
            if (targetObjectName == null) {
                rule = CardUtil.stripReminderText(ability.getRule());
            } else {
                rule = CardUtil.stripReminderText(ability.getRule("this " + targetObjectName));
            }
            if (rule.contains(",") || rule.contains(":")){
                rules.add('"'+rule+'"');
            } else {
                rules.add(rule);
            }
        }
        for (int index = 0; index < rules.size(); index++) {
            if (index > 0) {
                if (index < rules.size() - 1) {
                    sb.append(", ");
                } else if (rules.size() > 2) {
                    sb.append(", and ");
                } else {
                    sb.append(" and ");
                }
            }
            sb.append(rules.get(index));
        }

        // we can't simply cache it to this.ruleText as some cards may change abilities dynamically
        return sb.toString();
    }

    @Override
    public CompoundAbility copy() {
        return new CompoundAbility(this);
    }
}
