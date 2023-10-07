
package mage.game.match;

import mage.cards.decks.DeckCardInfo;
import mage.constants.MatchBufferTime;
import mage.constants.MatchTimeLimit;
import mage.constants.MultiplayerAttackOption;
import mage.constants.RangeOfInfluence;
import mage.constants.SkillLevel;
import mage.game.mulligan.MulliganType;
import mage.game.result.ResultProtos;
import mage.players.PlayerType;

import java.io.Serializable;
import java.util.*;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class MatchOptions implements Serializable {

    protected String name;
    protected MultiplayerAttackOption attackOption;
    protected RangeOfInfluence range;
    protected int winsNeeded;
    protected int freeMulligans;
    protected boolean customStartLifeEnabled;
    protected int customStartLife; // Only use if customStartLifeEnabled is True
    protected boolean customStartHandSizeEnabled;
    protected int customStartHandSize; // Only use if customStartHandSizeEnabled is True
    protected String gameType;
    protected String deckType;
    protected boolean limited;
    protected List<PlayerType> playerTypes = new ArrayList<>();
    protected boolean multiPlayer;
    protected int numSeats;
    protected String password;
    protected SkillLevel skillLevel;
    protected boolean rollbackTurnsAllowed;
    protected boolean spectatorsAllowed;
    protected boolean planeChase;
    protected int quitRatio;
    protected int minimumRating;
    protected int edhPowerLevel;
    protected boolean rated;
    protected int numSeatsForMatch;
    protected Set<String> bannedUsers = new HashSet<>();

    /**
     * Time each player has during the game to play using his\her priority.
     */
    protected MatchTimeLimit matchTimeLimit; // 0 = no priorityTime handling
    protected MatchBufferTime matchBufferTime; // Amount of time each player gets before their normal time limit counts down. Refreshes each time the normal timer is invoked.
    protected MulliganType mulliganType;

    protected Collection<DeckCardInfo> perPlayerEmblemCards;
    protected Collection<DeckCardInfo> globalEmblemCards;

    public MatchOptions(String name, String gameType, boolean multiPlayer, int numSeats) {
        this.name = name;
        this.gameType = gameType;
        this.password = "";
        this.multiPlayer = multiPlayer;
        this.numSeats = numSeats;
        this.perPlayerEmblemCards = Collections.emptySet();
        this.globalEmblemCards = Collections.emptySet();
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public void setMultiPlayer(boolean multiPlayer) {
        this.multiPlayer = multiPlayer;
    }

    public boolean getMultiPlayer() {
        return multiPlayer;
    }

    public String getName() {
        return name;
    }

    public MultiplayerAttackOption getAttackOption() {
        return attackOption;
    }

    public void setAttackOption(MultiplayerAttackOption attackOption) {
        this.attackOption = attackOption;
    }

    public RangeOfInfluence getRange() {
        return range;
    }

    public void setRange(RangeOfInfluence range) {
        this.range = range;
    }

    public int getWinsNeeded() {
        return winsNeeded;
    }

    public void setWinsNeeded(int winsNeeded) {
        this.winsNeeded = winsNeeded;
    }

    public int getFreeMulligans() {
        return freeMulligans;
    }

    public void setFreeMulligans(int freeMulligans) {
        this.freeMulligans = freeMulligans;
    }

    public boolean isCustomStartLifeEnabled() {
        return customStartLifeEnabled;
    }

    public void setCustomStartLifeEnabled(boolean value) {
        this.customStartLifeEnabled = value;
    }

    public int getCustomStartLife() {
        return customStartLife;
    }

    public void setCustomStartLife(int startLife) {
        this.customStartLife = startLife;
    }

    public boolean isCustomStartHandSizeEnabled() {
        return customStartHandSizeEnabled;
    }

    public void setCustomStartHandSizeEnabled(boolean value) {
        this.customStartHandSizeEnabled = value;
    }

    public int getCustomStartHandSize() {
        return customStartHandSize;
    }

    public void setCustomStartHandSize(int startHandSize) {
        this.customStartHandSize = startHandSize;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getDeckType() {
        return deckType;
    }

    public void setDeckType(String deckType) {
        this.deckType = deckType;
    }

    public List<PlayerType> getPlayerTypes() {
        return playerTypes;
    }

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }

    public int getPriorityTime() {
        if (matchTimeLimit == null) {
            return MatchTimeLimit.NONE.getTimeLimit();
        }
        return matchTimeLimit.getTimeLimit();
    }

    public MatchTimeLimit getMatchTimeLimit() {
        return this.matchTimeLimit;
    }

    public void setMatchTimeLimit(MatchTimeLimit matchTimeLimit) {
        this.matchTimeLimit = matchTimeLimit;
    }

    public int getBufferTime() {
        if (matchBufferTime == null) {
            return MatchBufferTime.NONE.getBufferTime();
        }
        return matchBufferTime.getBufferTime();
    }

    public MatchBufferTime getMatchBufferTime() {
        return this.matchBufferTime;
    }

    public void setMatchBufferTime(MatchBufferTime matchBufferTime) {
        this.matchBufferTime = matchBufferTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public boolean isRollbackTurnsAllowed() {
        return rollbackTurnsAllowed;
    }

    public void setRollbackTurnsAllowed(boolean rollbackTurnsAllowed) {
        this.rollbackTurnsAllowed = rollbackTurnsAllowed;
    }

    public boolean isSpectatorsAllowed() {
        return spectatorsAllowed;
    }

    public void setSpectatorsAllowed(boolean spectatorsAllowed) {
        this.spectatorsAllowed = spectatorsAllowed;
    }

    public boolean isPlaneChase() {
        return planeChase;
    }

    public void setPlaneChase(boolean planeChase) {
        this.planeChase = planeChase;
    }

    public int getQuitRatio() {
        return quitRatio;
    }

    public void setQuitRatio(int quitRatio) {
        this.quitRatio = quitRatio;
    }

    public int getMinimumRating() {
        return minimumRating;
    }

    public void setMinimumRating(int minimumRating) {
        this.minimumRating = minimumRating;
    }

    public int getEdhPowerLevel() {
        return edhPowerLevel;
    }

    public void setEdhPowerLevel(int edhPowerLevel) {
        this.edhPowerLevel = edhPowerLevel;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public Set<String> getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(Set<String> bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public ResultProtos.MatchOptionsProto toProto() {
        ResultProtos.MatchOptionsProto.Builder builder = ResultProtos.MatchOptionsProto.newBuilder()
                .setName(this.getName())
                .setLimited(this.isLimited())
                .setRated(this.isRated())
                .setWinsNeeded(this.getWinsNeeded());

        ResultProtos.SkillLevel skillLevel = ResultProtos.SkillLevel.BEGINNER;
        switch (this.getSkillLevel()) {
            case BEGINNER:
                skillLevel = ResultProtos.SkillLevel.BEGINNER;
                break;
            case CASUAL:
                skillLevel = ResultProtos.SkillLevel.CASUAL;
                break;
            case SERIOUS:
                skillLevel = ResultProtos.SkillLevel.SERIOUS;
                break;
        }
        builder.setSkillLevel(skillLevel);

        return builder.build();
    }

    public void setMullgianType(MulliganType mulliganType) {
        this.mulliganType = mulliganType;
    }

    public MulliganType getMulliganType() {
        if (mulliganType == null) {
            return MulliganType.GAME_DEFAULT;
        }
        return mulliganType;
    }

    public Collection<DeckCardInfo> getPerPlayerEmblemCards() {
        return perPlayerEmblemCards;
    }

    public void setPerPlayerEmblemCards(Collection<DeckCardInfo> perPlayerEmblemCards) {
        this.perPlayerEmblemCards = perPlayerEmblemCards;
    }

    public Collection<DeckCardInfo> getGlobalEmblemCards() {
        return globalEmblemCards;
    }

    public void setGlobalEmblemCards(Collection<DeckCardInfo> globalEmblemCards) {
        this.globalEmblemCards = globalEmblemCards;
    }
}
