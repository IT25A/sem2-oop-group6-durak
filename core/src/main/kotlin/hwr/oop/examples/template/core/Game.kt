package hwr.oop.examples.template.core

class Game(
    val gameId: Int,
    playerNames: List<String>
) {
    val deck = Deck()
    val trump = deck.peekTrump().suit
    val bout = Bout()
    val players = playerNames.map { name -> Player(name) }
    var attackerIndex = 0
    var defenderIndex = 1
    var attackingPlayer = players[attackerIndex]
    var defendingPlayer = players[defenderIndex]
    var gamePhase = GamePhase.attacking

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
        }
        else {
            defenderIndex = getNextNonEmptyPlayerIndex(attackerIndex)
        }
    }

    fun attack(card: Card){
        if (gamePhase != GamePhase.attacking){
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
    }
    fun defend(card: Card){
        if (gamePhase != GamePhase.defending){
            throw InvalidMoveException("Can not defend. Game $gameId is .currently in $gamePhase phase")
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
            gamePhase == GamePhase.defending && defendingPlayer.hand.isNotEmpty()

        val passingPlayer =
            if (defenderTakesCards) {
                defendingPlayer.hand.addAll(bout.defenseDeck)
                defendingPlayer.hand.addAll(bout.attackDeck)
                defendingPlayer
            } else {
                attackingPlayer
            }

        bout.attackDeck.clear()
        bout.defenseDeck.clear()
        refillHands()
        determineNextTurn(passingPlayer)
        gamePhase = GamePhase.attacking
    }

    init {
        for (player in players) {
            repeat(6){
                player.draw(deck)
            }
        }
    }
}

// --- Previous attack and defense phase functions ---
/*
fun attackPhase(selectedIndex: Int, isFirstTurn: Boolean): Boolean {
    val hand = attackingPlayer.hand

    if (selectedIndex !in hand.indices){
        throw IndexOutOfBoundsException("Selected index $selectedIndex is out of bounds")
    }

    val card = hand[selectedIndex]
    val canAttack = isFirstTurn ||
            bout.attackDeck.any { it.rank == card.rank } ||
            bout.defenseDeck.any { it.rank == card.rank }

    if (!canAttack) {
        throw IllegalArgumentException("${card.rank} has not been played this bout")
    }

    bout.attack(card)
    hand.removeAt(selectedIndex)
    return true
}
fun defensePhase(selectedIndex: Int): Boolean {
    val attackCard = bout.attackDeck.last()
    val hand = defendingPlayer.hand

    if (selectedIndex !in hand.indices){
        throw IndexOutOfBoundsException("Selected index $selectedIndex is out of bounds")
    }

    val card = hand[selectedIndex]
    val canDefend =
        (attackCard.suit != trump && card.suit == trump) ||
        (attackCard.suit == card.suit && card.rank.rankValue > attackCard.rank.rankValue)

    if (!canDefend) {
        throw IllegalArgumentException("$card is not a valid card to defend with")
    }

    bout.defense(card)
    hand.removeAt(selectedIndex)
    return true
}
*/