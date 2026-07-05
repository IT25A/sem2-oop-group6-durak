package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument

class OnGameIdCommand : CliktCommand(name = "onGameID") {
	private val cliContext by requireObject<CliContext>()
	private val gameId by argument("GAMEID", help = "The unique identifier of the game.")

	override fun run() {
		cliContext.gameId = gameId
	}
}
