package hwr.oop.examples.template.service

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
class Controller : GameApi, GameActionApi {
	
	override fun getGame(gameId: String?): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun startGame(startGameRequest: @Valid StartGameRequest?): ResponseEntity<GameCreatedResponse> {
		TODO("Not yet implemented")
	}
	
	override fun attack(
		gameId: String?,
		attackRequest: @Valid AttackRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun defend(
		gameId: String?,
		defendRequest: @Valid DefendRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun pass(
		gameId: String?,
		passRequest: @Valid PassRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
	override fun supply(
		gameId: String?,
		supplyRequest: @Valid SupplyRequest?,
	): ResponseEntity<GameState> {
		TODO("Not yet implemented")
	}
	
}
