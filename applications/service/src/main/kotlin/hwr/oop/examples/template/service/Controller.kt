package hwr.oop.examples.template.service

import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.GamePhase
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.service.ResponseMapper.toGameCard
import hwr.oop.examples.template.service.ResponseMapper.toGameState
import hwr.oop.examples.template.service.api.GameActionApi
import hwr.oop.examples.template.service.api.GameApi
import hwr.oop.examples.template.service.model.AttackRequest
import hwr.oop.examples.template.service.model.DefendRequest
import hwr.oop.examples.template.service.model.GameCreatedResponse
import hwr.oop.examples.template.service.model.GameState
import hwr.oop.examples.template.service.model.PassRequest
import hwr.oop.examples.template.service.model.StartGameRequest
import hwr.oop.examples.template.service.model.SupplyRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
	private val newGameUseCase: NewGameUseCase,
	private val playCardUseCase: PlayCardUseCase,
	private val loadGameByIdQuery: LoadGameByIdQuery,
) : GameApi, GameActionApi {

	override fun getGame(gameId: String?): ResponseEntity<GameState> {
		val game = loadGameByIdQuery.load(GameId(requireNotNull(gameId)))
		return ResponseEntity.ok(game.toGameState())
	}

	override fun startGame(startGameRequest: @Valid StartGameRequest?): ResponseEntity<GameCreatedResponse> {
		val playerNames = requireNotNull(startGameRequest).playerIds
		val game = newGameUseCase.newGame(playerNames)
		return ResponseEntity.status(201).body(GameCreatedResponse(game.gameId.value))
	}

	override fun attack(
		gameId: String?,
		attackRequest: @Valid AttackRequest?,
	): ResponseEntity<GameState> {
		val id = GameId(requireNotNull(gameId))
		val request = requireNotNull(attackRequest)
		val game = loadGameByIdQuery.load(id)
		if (game.attackingPlayer.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		val updated = playCardUseCase.attack(id, request.card.toGameCard())
		return ResponseEntity.ok(updated.toGameState())
	}

	override fun defend(
		gameId: String?,
		defendRequest: @Valid DefendRequest?,
	): ResponseEntity<GameState> {
		val id = GameId(requireNotNull(gameId))
		val request = requireNotNull(defendRequest)
		val game = loadGameByIdQuery.load(id)
		if (game.defendingPlayer.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		val updated = playCardUseCase.defend(id, request.defenseCard.toGameCard())
		return ResponseEntity.ok(updated.toGameState())
	}

	override fun pass(
		gameId: String?,
		passRequest: @Valid PassRequest?,
	): ResponseEntity<GameState> {
		val id = GameId(requireNotNull(gameId))
		val request = requireNotNull(passRequest)
		val game = loadGameByIdQuery.load(id)
		val validPlayer = when (game.getGamePhase()) {
			GamePhase.DEFENDING -> game.defendingPlayer
			GamePhase.ATTACKING -> game.attackingPlayer
			else -> null
		}
		if (validPlayer?.name != request.playerId) {
			return ResponseEntity.status(400).build()
		}
		val updated = playCardUseCase.pass(id)
		return ResponseEntity.ok(updated.toGameState())
	}

	override fun supply(
		gameId: String?,
		supplyRequest: @Valid SupplyRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
}