
package mage.cards.u;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.common.PlaneswalkerEntersWithLoyaltyCountersAbility;
import mage.abilities.dynamicvalue.common.ControllerLifeCount;
import mage.abilities.dynamicvalue.common.PermanentsOnBattlefieldCount;
import mage.abilities.dynamicvalue.common.PermanentsTargetOpponentControlsCount;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.BrainstormEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.DamageAllControlledTargetEffect;
import mage.abilities.effects.common.DamageAllEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.DestroyAllControlledTargetEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.ExileFromZoneTargetEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.GainLifeEffect;
import mage.abilities.effects.common.GetEmblemEffect;
import mage.abilities.effects.common.LookLibraryAndPickControllerEffect;
import mage.abilities.effects.common.LoseLifeSourceControllerEffect;
import mage.abilities.effects.common.PutCardFromHandOntoBattlefieldEffect;
import mage.abilities.effects.common.PutLibraryIntoGraveTargetEffect;
import mage.abilities.effects.common.ReturnFromGraveyardToBattlefieldTargetEffect;
import mage.abilities.effects.common.RevealLibraryPutIntoHandEffect;
import mage.abilities.effects.common.SacrificeEffect;
import mage.abilities.effects.common.ShuffleHandGraveyardAllEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.counter.DistributeCountersEffect;
import mage.abilities.effects.common.discard.DiscardControllerEffect;
import mage.abilities.effects.common.discard.DiscardTargetEffect;
import mage.abilities.effects.common.search.SearchLibraryPutInHandEffect;
import mage.abilities.effects.common.turn.ControlTargetPlayerNextTurnEffect;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.LifelinkAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.*;
import mage.counters.CounterType;
import mage.filter.FilterCard;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterArtifactCard;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterControlledLandPermanent;
import mage.filter.common.FilterCreatureCard;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.common.FilterLandPermanent;
import mage.filter.common.FilterPermanentCard;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.game.Game;
import mage.game.command.emblems.AjaniSteadfastEmblem;
import mage.game.command.emblems.DomriRadeEmblem;
import mage.game.command.emblems.ElspethKnightErrantEmblem;
import mage.game.command.emblems.GideonAllyOfZendikarEmblem;
import mage.game.command.emblems.KioraMasterOfTheDepthsEmblem;
import mage.game.command.emblems.VenserTheSojournerEmblem;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.*;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.TargetPlayer;
import mage.target.common.TargetAnyTarget;
import mage.target.common.TargetCardInGraveyard;
import mage.target.common.TargetCardInLibrary;
import mage.target.common.TargetCreaturePermanent;
import mage.target.common.TargetCreaturePermanentAmount;
import mage.target.common.TargetNonlandPermanent;
import mage.target.common.TargetOpponent;
import mage.target.common.TargetPlayerOrPlaneswalker;
import mage.util.RandomUtil;

public final class UrzaAcademyHeadmaster extends CardImpl {

    public UrzaAcademyHeadmaster(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{W}{U}{B}{R}{G}");
        this.addSuperType(SuperType.LEGENDARY);
        this.subtype.add(SubType.URZA);

        this.addAbility(new PlaneswalkerEntersWithLoyaltyCountersAbility(4));

        // +1: Head to AskUrza.com and click +1.
        this.addAbility(new LoyaltyAbility(new UrzaAcademyHeadmasterRandomEffect(1, setInfo), 1));

        // -1: Head to AskUrza.com and click -1.
        this.addAbility(new LoyaltyAbility(new UrzaAcademyHeadmasterRandomEffect(2, setInfo), -1));

        // -6: Head to AskUrza.com and click -6.
        this.addAbility(new LoyaltyAbility(new UrzaAcademyHeadmasterRandomEffect(3, setInfo), -6));

    }

    public UrzaAcademyHeadmaster(final UrzaAcademyHeadmaster card) {
        super(card);
    }

    @Override
    public UrzaAcademyHeadmaster copy() {
        return new UrzaAcademyHeadmaster(this);
    }
}

class UrzaAcademyHeadmasterRandomEffect extends OneShotEffect {
    
    private int selection;
    private CardSetInfo setInfo;
    private static final FilterCreaturePermanent filter1 = new FilterCreaturePermanent("creatures you control");
    private static final FilterPermanent filter2 = new FilterPermanent("noncreature permanent");
    private static final FilterCard filter3 = new FilterCard("creature and/or land cards");
    private static final FilterPermanent filter4 = new FilterPermanent("creatures and/or planeswalkers");
    static {
        filter1.add(new ControllerPredicate(TargetController.YOU));
        filter2.add(Predicates.not(new CardTypePredicate(CardType.CREATURE)));
        filter3.add(Predicates.or(
                new CardTypePredicate(CardType.CREATURE),
                new CardTypePredicate(CardType.LAND)));
        filter4.add(Predicates.or(
                new CardTypePredicate(CardType.CREATURE),
                new CardTypePredicate(CardType.PLANESWALKER)));
    }

    public UrzaAcademyHeadmasterRandomEffect(int selection, CardSetInfo setInfo) {
        super(Outcome.Neutral);
        this.selection = selection;
        this.setInfo = setInfo;
        switch (selection) {
            case 1:
                staticText = "Head to AskUrza.com and click +1";
                break;
            case 2:
                staticText = "Head to AskUrza.com and click -1";
                break;
            case 3:
                staticText = "Head to AskUrza.com and click -6";
        }
    }

    public UrzaAcademyHeadmasterRandomEffect(final UrzaAcademyHeadmasterRandomEffect effect) {
        super(effect);
        this.selection = effect.selection;
        this.setInfo = effect.setInfo;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            int result = RandomUtil.nextInt(20) + 1;
            List<Effect> effects = new ArrayList<>();
            Target target = null;
            StringBuilder sb = new StringBuilder("[URZA] ");
            
            switch (selection) {
                // ABILITY +1
                case 1:
                    switch (result) {
                        case 1: // AJANI STEADFAST 1
                            sb.append("Until end of turn, up to one target creature gets +1/+1 and gains first strike, vigilance, and lifelink.");
                            effects.add(new BoostTargetEffect(1, 1, Duration.EndOfTurn));
                            effects.add(new GainAbilityTargetEffect(FirstStrikeAbility.getInstance(), Duration.EndOfTurn));
                            effects.add(new GainAbilityTargetEffect(VigilanceAbility.getInstance(), Duration.EndOfTurn));
                            effects.add(new GainAbilityTargetEffect(LifelinkAbility.getInstance(), Duration.EndOfTurn));
                            target = new TargetCreaturePermanent(0, 1);
                            break;
                        case 2: // AJANI MENTOR OF HEROES 1
                            sb.append("Distribute three +1/+1 counters among one, two, or three target creatures you control.");
                            effects.add(new DistributeCountersEffect(CounterType.P1P1, 3, false, "one, two, or three target creatures you control"));
                            target = new TargetCreaturePermanentAmount(3, filter1);
                            break;
                        case 3: // NICOL BOLAS PLANESWALKER 1
                            sb.append("Destroy target noncreature permanent.");
                            effects.add(new DestroyTargetEffect());
                            target = new TargetPermanent(filter2);
                            break;
                        case 4: // CHANDRA FLAMECALLER 1
                            sb.append("Create two 3/1 red Elemental creature tokens with haste. Exile them at the beginning of the next end step.");
                            effects.add(new mage.cards.c.ChandraFlamecaller(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 5: // ELSPETH SUNS CHAMPION 1
                            sb.append("Create three 1/1 white Soldier creature tokens.");
                            effects.add(new CreateTokenEffect(new SoldierToken(), 3));
                            break;
                        case 6: // GARRUK APEX PREDATOR 2
                            sb.append("Create a 3/3 black Beast creature token with deathtouch.");
                            effects.add(new CreateTokenEffect(new GarrukApexPredatorBeastToken()));
                            break;
                        case 7: // GARRUK CALLER OF BEASTS 1
                            sb.append("Reveal the top five cards of your library. Put all creature cards revealed this way into your hand and the rest on the bottom of your library in any order.");
                            effects.add(new RevealLibraryPutIntoHandEffect(5, new FilterCreatureCard("creature cards"), Zone.LIBRARY));
                            break;
                        case 8: // GIDEON JURA 1
                            sb.append("During target opponent’s next turn, creatures that player controls attack Urza if able.");
                            effects.add(new mage.cards.g.GideonJura(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            target = new TargetOpponent();
                            break;
                        case 9: // GIDEON CHAMPION OF JUSTICE 1
                            sb.append("Put a loyalty counter on Urza for each creature target opponent controls.");
                            effects.add(new AddCountersSourceEffect(CounterType.LOYALTY.createInstance(0), new PermanentsTargetOpponentControlsCount(new FilterCreaturePermanent()), true));
                            target = new TargetOpponent();
                            break;
                        case 10: // JACE ARCHITECT OF THOUGHT 1
                            sb.append("Until your next turn, whenever a creature an opponent controls attacks, it gets -1/-0 until end of turn.");
                            effects.add(new mage.cards.j.JaceArchitectOfThought(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 11: // KARN LIBERATED 1
                            sb.append("Target player exiles a card from his or her hand.");
                            effects.add(new ExileFromZoneTargetEffect(Zone.HAND, null, "", new FilterCard()));
                            target = new TargetPlayer();
                            break;
                        case 12: // NISSA SAGE ANIMIST 1
                            sb.append("Reveal the top card of your library. If it’s a land card, put it onto the battlefield. Otherwise, put it into your hand.");
                            effects.add(new mage.cards.n.NissaSageAnimist(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 13: // NISSA WORLDWAKER 1
                            sb.append("Target land you control becomes a 4/4 Elemental creature with trample. It’s still a land.");
                            effects.add(new mage.cards.n.NissaWorldwaker(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            target = new TargetPermanent(new FilterControlledLandPermanent());
                            break;
                        case 14: // SARKHAN UNBROKEN 1
                            sb.append("Draw a card, then add one mana of any color to your mana pool.");
                            effects.add(new mage.cards.s.SarkhanUnbroken(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 15: // SARKHAN THE DRAGONSPEAKER 1
                            sb.append("Until end of turn, Urza becomes a legendary 4/4 red Dragon creature with flying, indestructible, and haste. (He doesn’t lose loyalty while he’s not a planeswalker.)");
                            effects.add(new mage.cards.s.SarkhanTheDragonspeaker(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 16: // SORIN SOLEMN VISITOR 1
                            sb.append("Until your next turn, creatures you control get +1/+0 and gain lifelink.");
                            effects.add(new BoostControlledEffect(1, 0, Duration.UntilYourNextTurn, StaticFilters.FILTER_PERMANENT_CREATURES));
                            effects.add(new GainAbilityControlledEffect(LifelinkAbility.getInstance(), Duration.UntilYourNextTurn, StaticFilters.FILTER_PERMANENT_CREATURES));
                            break;
                        case 17: // TEZZERET AGENT OF BOLAS 1
                            sb.append("Look at the top five cards of your library. You may reveal an artifact card from among them and put it into your hand. Put the rest on the bottom of your library in any order.");
                            effects.add(new LookLibraryAndPickControllerEffect(5, 1, new FilterArtifactCard(), true));
                            break;
                        case 18: // UGIN 1
                            sb.append("Urza deals 3 damage to any target.");
                            effects.add(new DamageTargetEffect(3));
                            target = new TargetAnyTarget();
                            break;
                        case 19: // VRASKA 1
                            sb.append("Until your next turn, whenever a creature deals combat damage to Urza, destroy that creature.");
                            effects.add(new mage.cards.v.VraskaTheUnseen(controller.getId(), setInfo).getAbilities().get(2).getEffects().get(0));
                            break;
                        case 20: // (altered) XENAGOS 1
                            sb.append("Add X mana in any combination of colors to your mana pool, where X is the number of creatures you control.");
                            effects.add(new UrzaAcademyHeadmasterManaEffect());
                    }
                    break;
                // ABILITY -1
                case 2:
                    switch (result) {
                        case 1: // (altered) CHANDRA FLAMECALLER 3
                            sb.append("Urza deals 3 damage to each creature.");
                            effects.add(new DamageAllEffect(3, StaticFilters.FILTER_PERMANENT_CREATURE));
                            break;
                        case 2: // NICOL BOLAS PLANESWALKER 2
                            sb.append("Gain control of target creature.");
                            effects.add(new GainControlTargetEffect(Duration.Custom));
                            target = new TargetCreaturePermanent();
                            break;
                        case 3: // (double) SORIN MARKOV 1
                            sb.append("Urza deals 4 damage to target creature or player and you gain 4 life.");
                            effects.add(new DamageTargetEffect(4));
                            effects.add(new GainLifeEffect(4));
                            target = new TargetAnyTarget();
                            break;
                        case 4: // GARRUK APEX PREDATOR 3
                            sb.append("Destroy target creature. You gain life equal to its toughness.");
                            effects.add(new DestroyTargetEffect());
                            effects.add(new mage.cards.g.GarrukApexPredator(controller.getId(), setInfo).getAbilities().get(4).getEffects().get(1));
                            target = new TargetCreaturePermanent();
                            break;
                        case 5: // GIDEON ALLY OF ZENDIKAR 3
                            sb.append("You get an emblem with “Creatures you control get +1/+1.”");
                            effects.add(new GetEmblemEffect(new GideonAllyOfZendikarEmblem()));
                            break;
                        case 6: // (altered) GARRUK CALLER OF BEASTS 2
                            sb.append("You may put a creature card from your hand onto the battlefield.");
                            effects.add(new PutCardFromHandOntoBattlefieldEffect(new FilterCreatureCard()));
                            break;
                        case 7: // (altered) JACE THE MIND SCULPTOR 2
                            sb.append("Draw three cards, then put a card from your hand on top of your library.");
                            effects.add(new UrzaAcademyHeadmasterBrainstormEffect());
                            break;
                        case 8: // JACE MEMORY ADEPT 2
                            sb.append("Target player puts the top ten cards of his or her library into his or her graveyard.");
                            effects.add(new PutLibraryIntoGraveTargetEffect(10));
                            target = new TargetPlayer();
                            break;
                        case 9: // JACE ARCHITECT OF THOUGHT 2
                            sb.append("Reveal the top five cards of your library. An opponent separates those cards into two piles. Put one pile into your hand and the other on the bottom of your library in any order.");
                            effects.add(new mage.cards.j.JaceArchitectOfThought(controller.getId(), setInfo).getAbilities().get(3).getEffects().get(0));
                            break;
                        case 10: // KARN LIBERATED 2
                            sb.append("Exile target permanent.");
                            effects.add(new ExileTargetEffect());
                            target = new TargetPermanent();
                            break;
                        case 11: // (altered) GARRUK CALLER OF BEASTS 1
                            sb.append("Reveal the top five cards of your library. You may put all creature cards and/or land cards from among them into your hand. Put the rest into your graveyard.");
                            effects.add(new RevealLibraryPutIntoHandEffect(5, filter3, Zone.LIBRARY));
                            break;
                        case 12: // (altered) LILIANA VESS 2
                            sb.append("Search your library for a card and put that card into your hand. Then shuffle your library.");
                            effects.add(new SearchLibraryPutInHandEffect(new TargetCardInLibrary(new FilterCard("a card")), false, true));
                            break;
                        case 13: // (double) LILIANA OF THE VEIL 2
                            sb.append("Target player sacrifices two creatures.");
                            effects.add(new SacrificeEffect(StaticFilters.FILTER_PERMANENT_CREATURE, 2, "Target player"));
                            target = new TargetPlayer();
                            break;
                        case 14: // OB NIXILIS OF THE BLACK OATH 2
                            sb.append("Create a 5/5 black Demon creature token with flying. You lose 2 life.");
                            effects.add(new CreateTokenEffect(new DemonToken()));
                            effects.add(new LoseLifeSourceControllerEffect(2));
                            break;
                        case 15: // (gold) SARKHAN UNBROKEN 2
                            sb.append("Create a 4/4 gold Dragon creature token with flying.");
                            effects.add(new CreateTokenEffect(new DragonTokenGold(), 1));
                            break;
                        case 16: // SORIN MARKOV 2
                            sb.append("Target player’s life total becomes 10.");
                            effects.add(new mage.cards.s.SorinMarkov(controller.getId(), setInfo).getAbilities().get(3).getEffects().get(0));
                            target = new TargetPlayer();
                            break;
                        case 17: // VRASKA 2
                            sb.append("Destroy target nonland permanent.");
                            effects.add(new DestroyTargetEffect());
                            target = new TargetNonlandPermanent();
                            break;
                        case 18: // UNIQUE
                            sb.append("Return target permanent from a graveyard to the battlefield under your control.");
                            effects.add(new ReturnFromGraveyardToBattlefieldTargetEffect());
                            target = new TargetCardInGraveyard(new FilterPermanentCard());
                            break;
                        case 19: // (double) GARRUK WILDSPEAKER 2
                            sb.append("Create two 3/3 green Beast creature tokens.");
                            effects.add(new CreateTokenEffect(new BeastToken(), 2));
                            break;
                        case 20: // UNIQUE
                            sb.append("Draw four cards and discard two cards.");
                            effects.add(new DrawCardSourceControllerEffect(4));
                            effects.add(new DiscardControllerEffect(2));
                    }
                    break;
                // ABILITY -6
                case 3:
                    switch (result) {
                        case 1: // NICOL BOLAS PLANESWALKER 3
                            sb.append("Urza deals 7 damage to target player. That player discards seven cards, then sacrifices seven permanents.");
                            effects.add(new DamageTargetEffect(7));
                            effects.add(new DiscardTargetEffect(7));
                            effects.add(new SacrificeEffect(new FilterPermanent(), 7, "then"));
                            target = new TargetPlayerOrPlaneswalker();
                            break;
                        case 2: // AJANI STEADFAST 3
                            sb.append("You get an emblem with “If a source would deal damage to you or a planeswalker you control, prevent all but 1 of that damage.”");
                            effects.add(new GetEmblemEffect(new AjaniSteadfastEmblem()));
                            break;
                        case 3: // AJANI VENGEANT 3
                            sb.append("Destroy all lands target player controls.");
                            effects.add(new DestroyAllControlledTargetEffect(new FilterLandPermanent()));
                            target = new TargetPlayer();
                            break;
                        case 4: // AJANI CALLER OF THE PRIDE 3
                            sb.append("Create X 2/2 white Cat creature tokens, where X is your life total.");
                            effects.add(new CreateTokenEffect(new CatToken(), new ControllerLifeCount()));
                            break;
                        case 5: // AJANI MENTOR OF HEROES 3
                            sb.append("You gain 100 life.");
                            effects.add(new GainLifeEffect(100));
                            break;
                        case 6: // CHANDRA NALAAR 3
                            sb.append("Urza deals 10 damage to target player and each creature he or she controls.");
                            effects.add(new DamageTargetEffect(10));
                            effects.add(new DamageAllControlledTargetEffect(10, new FilterCreaturePermanent()));
                            target = new TargetPlayerOrPlaneswalker();
                            break;
                        case 7: // DOMRI RADE 3
                            sb.append("You get an emblem with “Creatures you control have double strike, trample, hexproof, and haste.”");
                            effects.add(new GetEmblemEffect(new DomriRadeEmblem()));
                            break;
                        case 8: // ELSPETH KNIGHT ERRANT 3
                            sb.append("You get an emblem with “Artifacts, creatures, enchantments, and lands you control have indestructible.”");
                            effects.add(new GetEmblemEffect(new ElspethKnightErrantEmblem()));
                            break;
                        case 9: // GARRUK PRIMAL HUNTER 3
                            sb.append("Create a 6/6 green Wurm creature token for each land you control.");
                            effects.add(new CreateTokenEffect(new WurmToken(), new PermanentsOnBattlefieldCount(new FilterControlledLandPermanent())));
                            break;
                        case 10: // JACE THE LIVING GUILDPACT 3
                            sb.append("Each player shuffles his or her hand and graveyard into his or her library. You draw seven cards.");
                            effects.add(new ShuffleHandGraveyardAllEffect());
                            effects.add(new DrawCardSourceControllerEffect(7));
                            break;
                        case 11: // SORIN LORD OF INNISTRAD 3
                            sb.append("Destroy up to three target creatures and/or other planeswalkers. Return each card put into a graveyard this way to the battlefield under your control.");
                            effects.add(new mage.cards.s.SorinLordOfInnistrad(controller.getId(), setInfo).getAbilities().get(4).getEffects().get(0));
                            target = new TargetPermanent(0, 3, filter4, false);
                            break;
                        case 12: // VENSER 3
                            sb.append("You get an emblem with “Whenever you cast a spell, exile target permanent.”");
                            effects.add(new GetEmblemEffect(new VenserTheSojournerEmblem()));
                            break;
                        case 13: // KIORA MASTER OF THE DEPTHS 3
                            sb.append("You get an emblem with “Whenever a creature enters the battlefield under your control, you may have it fight target creature.” Then create three 8/8 blue Octopus creature tokens.");
                            effects.add(new CreateTokenEffect(new OctopusToken(), 3));
                            effects.add(new GetEmblemEffect(new KioraMasterOfTheDepthsEmblem()));
                            break;
                        case 14: // SORIN MARKOV 3
                            sb.append("You control target player during that player’s next turn.");
                            effects.add(new ControlTargetPlayerNextTurnEffect());
                            target = new TargetPlayer();
                            break;
                        case 15: // JACE THE MIND SCULPTOR 4
                            sb.append("Exile all cards from target player’s library, then that player shuffles his or her hand into his or her library.");
                            effects.add(new mage.cards.j.JaceTheMindSculptor(controller.getId(), setInfo).getAbilities().get(5).getEffects().get(0));
                            target = new TargetPlayer();
                            break;
                        case 16: // VRASKA 3
                            sb.append("Create three 1/1 black Assassin creature tokens with “Whenever this creature deals combat damage to a player, that player loses the game.”");
                            effects.add(new CreateTokenEffect(new AssassinToken()));
                            break;
                        case 17: // LILIANA VESS 3
                            sb.append("Put all creature cards from all graveyards onto the battlefield under your control.");
                            effects.add(new mage.cards.l.LilianaVess(controller.getId(), setInfo).getAbilities().get(4).getEffects().get(0));
                            break;
                        case 18: // NISSA VOICE OF ZENDIKAR 3
                            sb.append("You gain X life and draw X cards, where X is the number of lands you control.");
                            effects.add(new GainLifeEffect(new PermanentsOnBattlefieldCount(new FilterControlledLandPermanent())));
                            effects.add(new DrawCardSourceControllerEffect(new PermanentsOnBattlefieldCount(new FilterControlledLandPermanent())));
                            break;
                        case 19: // RAL ZAREK 3
                            sb.append("Flip five coins. Take an extra turn after this one for each coin that comes up heads.");
                            effects.add(new mage.cards.r.RalZarek(controller.getId(), setInfo).getAbilities().get(4).getEffects().get(0));
                            break;
                        case 20: // UGIN 3
                            sb.append("You gain 7 life, draw seven cards, then put up to seven permanent cards from your hand onto the battlefield.");
                            effects.add(new mage.cards.u.UginTheSpiritDragonEffect3());
                    }
            }
            
            game.informPlayers(sb.toString());
            if (target != null) {
                if (target.canChoose(source.getSourceId(), controller.getId(), game)) {
                    while (!target.isChosen() && target.canChoose(controller.getId(), game) && controller.canRespond()) {
                        controller.chooseTarget(outcome, target, source, game);
                    }
                }
                source.addTarget(target);
            }
            if (target == null || target.isChosen()) {
                for (Effect effect : effects) {
                    if (effect instanceof ContinuousEffect) {
                        game.addEffect((ContinuousEffect) effect, source);
                    } else {
                        effect.apply(game, source);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public UrzaAcademyHeadmasterRandomEffect copy() {
        return new UrzaAcademyHeadmasterRandomEffect(this);
    }
}

class UrzaAcademyHeadmasterManaEffect extends OneShotEffect {

    public UrzaAcademyHeadmasterManaEffect() {
        super(Outcome.PutManaInPool);
    }

    public UrzaAcademyHeadmasterManaEffect(final UrzaAcademyHeadmasterManaEffect effect) {
        super(effect);
    }

    @Override
    public UrzaAcademyHeadmasterManaEffect copy() {
        return new UrzaAcademyHeadmasterManaEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            int x = game.getBattlefield().count(new FilterControlledCreaturePermanent(), source.getSourceId(), source.getControllerId(), game);
            Choice manaChoice = new ChoiceImpl();
            Set<String> choices = new LinkedHashSet<>();
            choices.add("White");
            choices.add("Blue");
            choices.add("Black");
            choices.add("Red");
            choices.add("Green");
            manaChoice.setChoices(choices);
            manaChoice.setMessage("Select color of mana to add");

            for (int i = 0; i < x; i++) {
                Mana mana = new Mana();
                if (!player.choose(Outcome.Benefit, manaChoice, game)) {
                    return false;
                }
                if (manaChoice.getChoice() == null) {  // can happen if player leaves game
                    return false;
                }
                switch (manaChoice.getChoice()) {
                    case "White":
                        mana.increaseWhite();
                        break;
                    case "Blue":
                        mana.increaseBlue();
                        break;
                    case "Black":
                        mana.increaseBlack();
                        break;
                    case "Red":
                        mana.increaseRed();
                        break;
                    case "Green":
                        mana.increaseGreen();
                        break;
                }
                player.getManaPool().addMana(mana, game, source);
            }
            return true;
        }
        return false;
    }
}

class UrzaAcademyHeadmasterBrainstormEffect extends BrainstormEffect {

    public UrzaAcademyHeadmasterBrainstormEffect() {
        super();
    }

    public UrzaAcademyHeadmasterBrainstormEffect(final UrzaAcademyHeadmasterBrainstormEffect effect) {
        super(effect);
    }

    @Override
    public UrzaAcademyHeadmasterBrainstormEffect copy() {
        return new UrzaAcademyHeadmasterBrainstormEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            player.drawCards(3, game);
            putOnLibrary(player, source, game);
            return true;
        }
        return false;
    }
}
