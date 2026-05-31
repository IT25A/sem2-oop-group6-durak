package hwr.oop.examples.template.core

object CardFromStringConverter {
    fun convertSingle(cardString: String): Card = convert(cardString).first()
    fun convert(vararg strings: String) = convert(strings.toList())
    private fun convert(list: List<String>): List<Card> = list.map { it.asCard() }

    fun String.asCard(): Card {
        require(isNotEmpty()) { "Card string must not be empty" }
        require(isNotBlank()) { "Card string must not be blank" }
        val uppercase = this.uppercase()
        val rankStr: String
        val suitChar: Char
        when {
            uppercase.startsWith("10") && uppercase.length == 3 -> {
                rankStr = "10"
                suitChar = uppercase[2]
            }
            uppercase.length == 2 && uppercase != "10" -> {
                rankStr = uppercase[0].toString()
                suitChar = uppercase[1]
            }
            else -> throw IllegalArgumentException("Card string must be of length 2 (e.g. 'QH') or 3 (e.g. '10H')")
        }
        return Card(suits(suitChar), ranks(rankStr))
    }

    private fun suits(char: Char): Suit = when (char) {
        'D' -> Suit.DIAMONDS
        'H' -> Suit.HEARTS
        'C' -> Suit.CLUBS
        'S' -> Suit.SPADES
        else -> throw IllegalArgumentException("Unknown suit: $char")
    }

    private fun ranks(str: String): Rank = when (str) {
        "6"  -> Rank.SIX
        "7"  -> Rank.SEVEN
        "8"  -> Rank.EIGHT
        "9"  -> Rank.NINE
        "10" -> Rank.TEN
        "J"  -> Rank.JACK
        "Q"  -> Rank.QUEEN
        "K"  -> Rank.KING
        "A"  -> Rank.ACE
        else -> throw IllegalArgumentException("Unknown rank: $str")
    }
}