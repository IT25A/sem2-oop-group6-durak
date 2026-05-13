package hwr.oop.examples.template.core

class Bout {
    val attackDeck: MutableList<Card> = mutableListOf()
    val defenseDeck: MutableList<Card> = mutableListOf()

    fun attack(card: Card) {
        attackDeck.add(card)
    }
    fun defense(card: Card) {
        defenseDeck.add(card)
    }
    fun printBout() {
        println("---Bout---")
        val size = maxOf(attackDeck.size, defenseDeck.size)

        for (i in 1..size) {
            print("[$i]")
        }
        println()
        for (i in 0 until size) {
            val card = attackDeck.getOrNull(i)?.toString() ?: "   "
            print("$card ")
        }
        println()
        for (i in 0 until size) {
            val card = defenseDeck.getOrNull(i)?.toString() ?: "   "
            print("$card ")
        }
        println("\n------")
    }
}