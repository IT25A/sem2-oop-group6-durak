package hwr.oop.examples.template.core

class Game(
    val gameId: Int,
    playerNames: List<String>
) {
    val deck = Deck()
    val trump = deck.peekTrump().suit
    val bout = Bout()
    val players = playerNames.map { name -> Player(name) }
    private var attackerIndex = 0
    private var defenderIndex = 1
    private var attackingPlayer = players[attackerIndex]
    private var defendingPlayer = players[defenderIndex]
    private var gamePhase = GamePhase.ATTACKING
    private var isGameOver = false
    val playerWinOrder = mutableListOf<Player>()

    private fun getNextNonEmptyPlayerIndex(fromIndex: Int): Int {
        var index = fromIndex
        do {
            index = (index + 1) % players.size
        } while (players[index].hand.isEmpty())
        return index
    }
    fun determineNextTurn(passingPlayer: Player) {
        if (passingPlayer != attackingPlayer && passingPlayer != defendingPlayer) {
            throw IllegalArgumentException("Passing player is invalid")
        }
        if (attackingPlayer == passingPlayer) {
            attackerIndex = getNextNonEmptyPlayerIndex(attackerIndex)
            defenderIndex = getNextNonEmptyPlayerIndex(attackerIndex)
            attackingPlayer = players[attackerIndex]
            defendingPlayer = players[defenderIndex]
        }
        else {
            defenderIndex = getNextNonEmptyPlayerIndex(attackerIndex)
            defendingPlayer = players[defenderIndex]
        }
    }

    fun attack(card: Card){
        if (gamePhase != GamePhase.ATTACKING){
            throw InvalidMoveException("Can not attack. Game $gameId is currently in $gamePhase ")
        }
        if (!attackingPlayer.hand.contains(card)){
            throw IllegalArgumentException("Card is not part of ${attackingPlayer.name}'s hand")
        }
        val canAttack = bout.attackDeck.isEmpty() ||
                bout.attackDeck.any { it.rank == card.rank } ||
                bout.defenseDeck.any { it.rank == card.rank }
        if (!canAttack){
            throw IllegalArgumentException("You are not allowed to play this card")
        }
        bout.attackDeck.add(card)
        attackingPlayer.hand.remove(card)
        gamePhase = GamePhase.DEFENDING
        if (attackingPlayer.hand.isEmpty() && deck.getCards().isEmpty()){
            playerWinOrder.add(attackingPlayer)
            determineGameOver()
        }
    }
    fun defend(card: Card){
        if (gamePhase != GamePhase.DEFENDING){
            throw InvalidMoveException("Can not defend. Game $gameId is currently in $gamePhase phase")
        }
        if (!defendingPlayer.hand.contains(card)){
            throw IllegalArgumentException("Card is not part of ${attackingPlayer.name}'s hand")
        }
        val attackCard = bout.attackDeck.last()
        val canDefend =
                (attackCard.suit != trump && card.suit == trump) ||
                (attackCard.suit == card.suit && card.rank.rankValue > attackCard.rank.rankValue)
        if (!canDefend) {
            throw IllegalArgumentException("$card is not a valid card to defend with")
        }
        bout.defenseDeck.add(card)
        defendingPlayer.hand.remove(card)
        gamePhase = GamePhase.ATTACKING
        if (defendingPlayer.hand.isEmpty() && deck.getCards().isEmpty()){
            playerWinOrder.add(defendingPlayer)
            determineGameOver()
            pass()
        }
    }

    fun refillHands() {
        val drawOrder = (attackerIndex until players.size) + (0 until attackerIndex)
        for (index in drawOrder) {
            val player = players[index]
            while (player.hand.size < 6 && deck.getCards().isNotEmpty()) {
                player.draw(deck)
            }
        }
    }
    fun pass() {
        val defenderTakesCards =
            gamePhase == GamePhase.DEFENDING && !isBoutDefended()

        val passingPlayer =
            if (defenderTakesCards) {
                finishBout(false)
                defendingPlayer
            } else {
                attackingPlayer
            }

        finishBout(true)
        refillHands()
        determineNextTurn(passingPlayer)
        gamePhase = GamePhase.ATTACKING
    }
    fun discardBoutCards() {
        bout.attackDeck.clear()
        bout.defenseDeck.clear()
    }
    fun finishBout(defenderSucceeded: Boolean) {
        if (defenderSucceeded) {
            discardBoutCards()
        } else {
            defendingPlayer.hand.addAll(bout.attackDeck)
            defendingPlayer.hand.addAll(bout.defenseDeck)
            discardBoutCards()
        }
    }
    fun isBoutDefended(): Boolean {
        return bout.attackDeck.size == bout.defenseDeck.size && bout.attackDeck.isNotEmpty()
    }
    fun determineGameOver(){
        if (players.size - playerWinOrder.size <= 1) {
            isGameOver = true
        }
    }

    init {
        for (player in players) {
            repeat(6){
                player.draw(deck)
            }
        }
    }
}