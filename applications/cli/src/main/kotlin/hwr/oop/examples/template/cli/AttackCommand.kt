package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import hwr.oop.examples.template.core.CardFromStringConverter.asCard
import hwr.oop.examples.template.core.GameId

class AttackCommand : CliktCommand(name = "attack") {
	private val cliContext by requireObject<CliContext>()
	private val playerId by argument("PLAYER", help = "Name of the attacking player.")
	private val card by argument("CARD", help = "Card to play, e.g. 8H, QD, 10S, AS.")

	override fun run() {
		val game = cliContext.persistence.getGame(GameId(requireNotNull(cliContext.gameId)))
		require(game.attackingPlayer.name == playerId) {
			"It is ${game.attackingPlayer.name}'s turn to attack, not $playerId."
		}
		game.attack(card.asCard())
		cliContext.persistence.save(game)
		echo("$playerId played ${card.uppercase()}.")
		echo("Phase: ${game.getGamePhase()}  |  Defender: ${game.defendingPlayer.name}")
	}
}
