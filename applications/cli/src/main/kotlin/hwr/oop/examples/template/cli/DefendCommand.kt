package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import hwr.oop.examples.template.core.CardFromStringConverter.asCard
import hwr.oop.examples.template.core.GameId

class DefendCommand : CliktCommand(name = "defend") {
	private val cliContext by requireObject<CliContext>()
	private val playerId by argument("PLAYER", help = "Name of the defending player.")
	private val card by argument("CARD", help = "The card from hand used to cover the attack, e.g. 8H, QD, 10S, AS.")

	override fun run() {
		val game = cliContext.persistence.loadById(GameId(requireNotNull(cliContext.gameId)))
		require(game.defendingPlayer.name == playerId) {
			"It is ${game.defendingPlayer.name}'s turn to attack, not $playerId."
		}
		game.defend(card.asCard())
		cliContext.persistence.save(game)
		printGameState(game)
	}
}
