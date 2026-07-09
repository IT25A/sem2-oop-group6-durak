package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameId

class GetGameCommand : CliktCommand(name = "getGame") {
	private val cliContext by requireObject<CliContext>()

	override fun run() {
		val game = cliContext.persistence.loadById(GameId(requireNotNull(cliContext.gameId)))
		printGameState(game)
	}
}
