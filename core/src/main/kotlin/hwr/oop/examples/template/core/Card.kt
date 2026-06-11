package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

/**
 * @param suit [Suit]
 * @param rank [Rank]
 */
@Serializable
data class Card(
    val suit: Suit,
    val rank: Rank
)