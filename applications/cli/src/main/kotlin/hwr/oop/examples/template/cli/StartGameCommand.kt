package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import hwr.oop.examples.template.core.Game

class StartGameCommand : CliktCommand(name = "startGame") {
	private val cliContext by requireObject<CliContext>()
	private val playerNames by argument(
		"PLAYERS",
		help = "Names of the players joining the game (2–6)."
	).multiple(required = true)

	override fun run() {
		require(playerNames.size in 2..6) { "Need between 2 and 6 players, got ${playerNames.size}." }
		val game = Game.createRandomGame(playerNames = playerNames)
		cliContext.persistence.save(game)
		echo("Game started! ID: ${game.gameId.value}")
		echo("Players: ${playerNames.joinToString(", ")}")
		echo("Trump suit: ${game.trump}")
		echo("Attacker: ${game.attackingPlayer.name}  |  Defender: ${game.defendingPlayer.name}")
	}
}
