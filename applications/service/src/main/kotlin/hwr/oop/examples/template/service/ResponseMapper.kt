package hwr.oop.examples.template.service

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GamePhase
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.core.Suit
import hwr.oop.examples.template.core.Card as GameCard
import hwr.oop.examples.template.service.model.AttackStack
import hwr.oop.examples.template.service.model.Card
import hwr.oop.examples.template.service.model.GameState
import hwr.oop.examples.template.service.model.GameStatus
import hwr.oop.examples.template.service.model.PlayerHand

object ResponseMapper {
     fun Game.toGameState(): GameState {
        val attackCards = bout.getAttackDeck()
        val defenseCards = bout.getDefenseDeck()
        val bout = attackCards.mapIndexed { i, attackCard ->
            val defenseCard = defenseCards.getOrNull(i)
            AttackStack(Card(attackCard.suit.name, attackCard.rank.name)).apply {
                if (defenseCard != null) setDefenseCard(Card(defenseCard.suit.name, defenseCard.rank.name))
            }
        }
        return GameState(
            gameId.value,
            if (getGamePhase() == GamePhase.FINISHED) GameStatus.FINISHED else GameStatus.IN_PROGRESS,
            deck.getCards().map { Card(it.suit.name, it.rank.name) },
            trump.name,
            attackingPlayer.name,
            defendingPlayer.name,
            players.map { player ->
                PlayerHand(player.name, player.getHand().map { Card(it.suit.name, it.rank.name) })
            },
            bout,
            emptyList(),
        )
    }

    fun Card.toGameCard(): GameCard = GameCard(Suit.valueOf(suit), Rank.valueOf(rank))
}
