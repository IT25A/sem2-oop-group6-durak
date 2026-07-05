package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.GamePhase

class PassCommand : CliktCommand(name = "pass") {
	private val cliContext by requireObject<CliContext>()
	private val playerId by argument("PLAYER", help = "Name of the passing player.")

	override fun run() {
		val game = cliContext.persistence.getGame(GameId(requireNotNull(cliContext.gameId)))
		val validPlayer = when (game.getGamePhase()) {
			GamePhase.DEFENDING -> game.defendingPlayer
			GamePhase.ATTACKING -> game.attackingPlayer
			else -> null
		}
		require(validPlayer?.name == playerId) {
			"$playerId cannot pass right now. Expected: ${validPlayer?.name ?: "nobody (game is finished)"}."
		}
		game.pass()
		cliContext.persistence.save(game)
		echo("$playerId passed.")
		echo("Phase: ${game.getGamePhase()}  |  Attacker: ${game.attackingPlayer.name}  |  Defender: ${game.defendingPlayer.name}")
	}
}
