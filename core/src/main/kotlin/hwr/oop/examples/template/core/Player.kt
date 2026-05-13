package hwr.oop.examples.template.core

class Player(val name: String = "", val hand: MutableList<Card> = mutableListOf()) {
    fun draw(deck: Deck) {
        val drawnCard = deck.draw()
        if (drawnCard != null) {
            hand.add(drawnCard)
        } else {
            println("Drawn card not found")
        }
    }
    fun printHand() {
        println("---${name}---")
        var i = 1
        for (card in hand) {
            println("[$i] $card")
            i++
        }
        println("------")
    }
}