package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val gameId: GameId = GameId.random(),
    val deck: Deck,
    val trump: Suit,
    val bout: Bout,
    val players: List<Player>,
    private var attackerIndex: Int = 0,
    private var defenderIndex: Int = 1,
    private var supplyRotation: MutableList<Player> = mutableListOf(),
    private var gamePhase: GamePhase = GamePhase.ATTACKING,
    private val playerWinOrder: MutableList<Player> = mutableListOf()
) {
    val attackingPlayer get() = players[attackerIndex]
    val defendingPlayer get() = players[defenderIndex]

    fun getGamePhase(): GamePhase = gamePhase
    fun setGamePhase(phase: GamePhase) { gamePhase = phase }
    fun getPlayerWinOrder() = playerWinOrder

    companion object {
        fun createRandomGame(
            gameId: GameId = GameId.random(),
            playerNames: List<String>
        ): Game {
            val deck = Deck.createShuffled()
            return createGameFromDeck(
                gameId = gameId,
                playerNames = playerNames,
                deck = deck
            )
        }

        fun createGameFromDeck(
            gameId: GameId = GameId.random(),
            playerNames: List<String>,
            deck: Deck
        ): Game {
            val players = playerNames.map { Player.create(it) }
            val bout = Bout()
            val trump = deck.peekTrump().suit

            for (player in players) {
                repeat(6) {
                    player.draw(deck)
                }
            }

            return Game(
                gameId = gameId,
                deck = deck,
                trump = trump,
                bout = bout,
                players = players
            )
        }
    }

    fun getNextNonEmptyPlayerIndex(fromIndex: Int): Int {
        var index = fromIndex
        do {
            index = (index + 1) % players.size
        } while (players[index].getHand().isEmpty())
        return index
    }
    fun determineNextTurn(passingPlayer: Player) {
        if (passingPlayer != attackingPlayer && passingPlayer != defendingPlayer) {
            throw InvalidPlayerTurnException("Passing player is invalid")
        }
        if (attackingPlayer == passingPlayer) {
            attackerIndex = getNextNonEmptyPlayerIndex(attackerIndex)
            defenderIndex = getNextNonEmptyPlayerIndex(attackerIndex)
        } else {
            attackerIndex = getNextNonEmptyPlayerIndex(defenderIndex)
            defenderIndex = getNextNonEmptyPlayerIndex(attackerIndex)
        }
    }

    fun attack(card: Card) {
        if (gamePhase != GamePhase.ATTACKING) {
            throw InvalidMoveException("Can not attack. Game $gameId is currently in $gamePhase ")
        }
        if (!attackingPlayer.getHand().contains(card)) {
            throw UnavailableCardException("Card is not part of ${attackingPlayer.name}'s hand")
        }
        val canAttack =
            bout.getAttackDeck().isEmpty() ||
            bout.getAttackDeck().any { it.rank == card.rank } ||
            bout.getDefenseDeck().any { it.rank == card.rank }
        if (!canAttack) {
            throw InvalidCardException("You are not allowed to play this card")
        }
        bout.addAttackCard(card)
        attackingPlayer.removeFromHand(card)
        gamePhase = GamePhase.DEFENDING
        handlePlayerFinished(attackingPlayer)
    }
    fun defend(card: Card) {
        if (gamePhase != GamePhase.DEFENDING) {
            throw InvalidMoveException("Can not defend. Game $gameId is currently in $gamePhase phase")
        }
        if (!defendingPlayer.getHand().contains(card)) {
            throw UnavailableCardException("Card is not part of ${defendingPlayer.name}'s hand")
        }
        val attackCard = bout.getAttackDeck().last()
        val canDefend =
            (attackCard.suit != trump && card.suit == trump) ||
            (attackCard.suit == card.suit && card.rank.rankValue > attackCard.rank.rankValue)
        if (!canDefend) {
            throw InvalidCardException("$card is not a valid card to defend with")
        }
        bout.addDefenseCard(card)
        defendingPlayer.removeFromHand(card)
        gamePhase = GamePhase.ATTACKING
        handlePlayerFinished(defendingPlayer, true)
    }
//    fun supplyAttack(supplierPlayer: Player, card: Card) {
//        
//    }
//    fun startSupplyRotation(defenderLoses: Boolean) {
//        supplyRotation = players
//            .filterIndexed { index, _ -> index != attackerIndex && index != defenderIndex }
//            .toMutableList()
//        if (supplyRotation.isNotEmpty()) {
//            gamePhase = GamePhase.SUPPLYING
//        } else if (defenderLoses) {
//            finishBout(false)
//        } else {
//            finishBout(true)
//        }
//    }
//    fun supplyPass(){
//    
//    }

    fun refillHands() {
        val drawOrder = (attackerIndex until players.size) + (0..attackerIndex)
        for (index in drawOrder) {
            val player = players[index]
            while (player.getHand().size < 6 && deck.getCards().isNotEmpty()) {
                player.draw(deck)
            }
        }
    }
    fun pass() {
        val defenderTakesCards =
            gamePhase == GamePhase.DEFENDING && !bout.isBoutDefended()

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
    fun finishBout(defenderSucceeded: Boolean) {
        if (defenderSucceeded) {
            bout.clearBout()
        } else {
            defendingPlayer.addAllToHand(bout.getAttackDeck())
            defendingPlayer.addAllToHand(bout.getDefenseDeck())
            bout.clearBout()
        }
    }

    fun handlePlayerFinished(player: Player, callPassIfDefender: Boolean = false) {
        if (player.getHand().isEmpty() && deck.getCards().isEmpty()) {
            playerWinOrder.add(player)
            determineGameOver()
            if (callPassIfDefender) {
                pass()
            }
        }
    }
    fun determineGameOver() {
        if (players.size - playerWinOrder.size <= 1) {
            gamePhase = GamePhase.FINISHED
        }
    }
}