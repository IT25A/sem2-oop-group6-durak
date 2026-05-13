package hwr.oop.examples.template.core

class Game(playerNames: List<String> = listOf("Alice", "Bob")) {
    val deck = Deck()
    val trump = deck.peekTrump().suit
    val bout = Bout()
    val players = playerNames.map { name -> Player(name) }
    var attackingPlayer = players[0]
    var defendingPlayer = players[1]

    fun attackPhase(isFirstTurn: Boolean): Boolean {
        var validInput = false
        val hand = attackingPlayer.hand

        while (!validInput) {
            attackingPlayer.printHand()
            val input = readln().toInt()-1
            if (input in hand.indices) {
                val card = hand[input]
                val canAttack = isFirstTurn ||
                        bout.attackDeck.any { it.rank == card.rank } ||
                        bout.defenseDeck.any { it.rank == card.rank }

                if (canAttack) {
                    bout.attack(card)
                    hand.removeAt(input)
                    validInput = true
                } else {
                    println("${card.rank} has not been played this bout")
                }
            } else {
                println("Invalid Number! (outside of range)")
            }
        }
        bout.printBout()
        return true
    }
    fun defensePhase(): Boolean {
        val attackCard = bout.attackDeck.last()
        val hand = defendingPlayer.hand
        var validInput = false

        while (!validInput) {
            defendingPlayer.printHand()
            val input = readln().toInt() - 1
            if (input in hand.indices) {
                val defendCard = hand[input]
                val canDefend =
                    (attackCard.suit != trump && defendCard.suit == trump) ||
                    (attackCard.suit == defendCard.suit && defendCard.rank.rankValue > attackCard.rank.rankValue)

                if (canDefend) {
                    bout.defense(defendCard)
                    hand.removeAt(input)
                    validInput = true
                } else {
                    println("$defendCard is not a valid card to defend with")
                }
            } else {
                println("Invalid Number! (outside of range)")
            }
        }
        bout.printBout()
        return true
    }
    init {
        for (player in players) {
            repeat(6){
                player.draw(deck)
            }
        }
    }
}