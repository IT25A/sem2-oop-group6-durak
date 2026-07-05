package hwr.oop.examples.template.service

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.GamePhase
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.core.Suit
import hwr.oop.examples.template.core.Card as GameCard
import hwr.oop.examples.template.ports.out.Persistence
import hwr.oop.examples.template.service.api.GameActionApi
import hwr.oop.examples.template.service.api.GameApi
import hwr.oop.examples.template.service.model.AttackRequest
import hwr.oop.examples.template.service.model.AttackStack
import hwr.oop.examples.template.service.model.Card
import hwr.oop.examples.template.service.model.DefendRequest
import hwr.oop.examples.template.service.model.GameCreatedResponse
import hwr.oop.examples.template.service.model.GameState
import hwr.oop.examples.template.service.model.GameStatus
import hwr.oop.examples.template.service.model.PassRequest
import hwr.oop.examples.template.service.model.PlayerHand
import hwr.oop.examples.template.service.model.StartGameRequest
import hwr.oop.examples.template.service.model.SupplyRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(private val persistence: Persistence) : GameApi, GameActionApi {

	override fun getGame(gameId: String?): ResponseEntity<GameState> {
		val game = persistence.getGame(GameId(requireNotNull(gameId)))
		return ResponseEntity.ok(game.toGameState())
	}

	override fun startGame(startGameRequest: @Valid StartGameRequest?): ResponseEntity<GameCreatedResponse> {
		val playerNames = requireNotNull(startGameRequest).playerIds
		val game = Game.createRandomGame(playerNames = playerNames)
		persistence.save(game)
		return ResponseEntity.status(201).body(GameCreatedResponse(game.gameId.value))
	}

	override fun attack(
		gameId: String?,
		attackRequest: @Valid AttackRequest?,
	): ResponseEntity<GameState> {
		val game = persistence.getGame(GameId(requireNotNull(gameId)))
		val request = requireNotNull(attackRequest)
		if (game.attackingPlayer.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		val attackCard = request.card.toGameCard()
		game.attack(attackCard)
		persistence.save(game)
		return ResponseEntity.ok(game.toGameState())
	}

	override fun defend(
		gameId: String?,
		defendRequest: @Valid DefendRequest?,
	): ResponseEntity<GameState> {
		val game = persistence.getGame(GameId(requireNotNull(gameId)))
		val request = requireNotNull(defendRequest)
		if(game.defendingPlayer.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		val defendCard = request.defenseCard.toGameCard()
		game.defend(defendCard)
		persistence.save(game)
		return ResponseEntity.ok(game.toGameState())
	}

	override fun pass(
		gameId: String?,
		passRequest: @Valid PassRequest?,
	): ResponseEntity<GameState> {
		val game = persistence.getGame(GameId(requireNotNull(gameId)))
		val request = requireNotNull(passRequest)
		val validPlayer = when (game.getGamePhase()) {
			GamePhase.DEFENDING -> game.defendingPlayer
			GamePhase.ATTACKING -> game.attackingPlayer
			else -> null
		}
		if (validPlayer?.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		game.pass()
		persistence.save(game)
		return ResponseEntity.ok(game.toGameState())
	}

	override fun supply(
		gameId: String?,
		supplyRequest: @Valid SupplyRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}

	private fun Game.toGameState(): GameState {
		val attackCards = bout.getAttackDeck()
		val defenseCards = bout.getDefenseDeck()
		val table = attackCards.mapIndexed { i, attackCard ->
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
			table,
			emptyList(),
		)
	}
	private fun Card.toGameCard(): GameCard = GameCard(Suit.valueOf(suit), Rank.valueOf(rank))
}
