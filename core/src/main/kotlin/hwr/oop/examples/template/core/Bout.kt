package hwr.oop.examples.template.core

/**
 * Stores the cards played by the Attacker/Defender
 */
class Bout {
    val attackDeck: MutableList<Card> = mutableListOf()
    val defenseDeck: MutableList<Card> = mutableListOf()

    /**
     * Adds the provided Card to the attackers' played Cards
     */
    fun attack(card: Card) {
        attackDeck.add(card)
    }

    /**
     * Adds the provided Card to the defenders' played Cards
     */
    fun defense(card: Card) {
        defenseDeck.add(card)
    }

    /**
     * Utility function that prints all cards played this bout
     */
//    fun printBout() {
//        println("---Bout---")
//        val size = maxOf(attackDeck.size, defenseDeck.size)
//
//        for (i in 1.size) {
//            print("[$i]")
//        }
//        println()
//        for (i in 0 until size) {
//            val card = attackDeck.getOrNull(i)?.toString() ?: "   "
//            print("$card ")
//        }
//        println()
//        for (i in 0 until size) {
//            val card = defenseDeck.getOrNull(i)?.toString() ?: "   "
//            print("$card ")
//        }
//        println("\n------")
//    }
}