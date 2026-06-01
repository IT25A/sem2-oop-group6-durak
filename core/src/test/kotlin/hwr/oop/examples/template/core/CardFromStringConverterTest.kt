package hwr.oop.examples.template.core

import hwr.oop.examples.template.core.CardFromStringConverter.asCard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CardFromStringConverterTest {
    @Test
    fun `convertSingle converts 2-character card correctly`() {
        val card = CardFromStringConverter.convertSingle("qh")
        assertEquals(Card(Suit.HEARTS, Rank.QUEEN), card)
    }
    @Test
    fun `convertSingle converts 3-character card (10) correctly`() {
        val card = CardFromStringConverter.convertSingle("10s")
        assertEquals(Card(Suit.SPADES, Rank.TEN), card)
    }
    @Test
    fun `convertSingle is case-insensitive`() {
        val card1 = CardFromStringConverter.convertSingle("Qh")
        val card2 = CardFromStringConverter.convertSingle("qh")
        val card3 = CardFromStringConverter.convertSingle("qH")
        assertEquals(card1, card2)
        assertEquals(card2, card3)
    }
    @Test
    fun `convert converts multiple cards correctly`() {
        val cards = CardFromStringConverter.convert("6D", "KH", "10C")
        assertEquals(
            listOf(
                Card(Suit.DIAMONDS, Rank.SIX),
                Card(Suit.HEARTS, Rank.KING),
                Card(Suit.CLUBS, Rank.TEN)
            ),
            cards
        )
    }
    @Test
    fun `convertList converts list of string cards correctly`() {
        val strings = listOf("JD", "7S")
        val cards = CardFromStringConverter.convert(*strings.toTypedArray())
        assertEquals(
            listOf(
                Card(Suit.DIAMONDS, Rank.JACK),
                Card(Suit.SPADES, Rank.SEVEN)
            ),
            cards
        )
    }
    @Test
    fun `asCard converts all possible ranks`() {
        assertEquals(Rank.SIX,   "6H".asCard().rank)
        assertEquals(Rank.SEVEN, "7H".asCard().rank)
        assertEquals(Rank.EIGHT, "8H".asCard().rank)
        assertEquals(Rank.NINE,  "9H".asCard().rank)
        assertEquals(Rank.TEN,   "10H".asCard().rank)
        assertEquals(Rank.JACK,  "JH".asCard().rank)
        assertEquals(Rank.QUEEN, "QH".asCard().rank)
        assertEquals(Rank.KING,  "KH".asCard().rank)
        assertEquals(Rank.ACE,   "AH".asCard().rank)
    }
    @Test
    fun `asCard throws on empty string`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            "".asCard()
        }
        assertTrue(exception.message!!.contains("must not be empty"))
    }
    @Test
    fun `asCard throws on blank string`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            "   ".asCard()
        }
        assertTrue(exception.message!!.contains("must not be blank"))
    }
    @Test
    fun `asCard throws on invalid length`() {
        val exception1 = assertThrows(IllegalArgumentException::class.java) {
            "1".asCard()
        }
        assertTrue(exception1.message!!.contains("must be of length"))

        val exception2 = assertThrows(IllegalArgumentException::class.java) {
            "10".asCard()
        }
        assertTrue(exception2.message!!.contains("must be of length"))
    }
    @Test
    fun `asCard throws on unknown suit`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            "QS".replace('S', 'X').asCard()
        }
        assertTrue(exception.message!!.contains("Unknown suit"))
    }
    @Test
    fun `asCard throws on unknown rank`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            "1S".asCard()
        }
        assertTrue(exception.message!!.contains("Unknown rank"))
    }
}