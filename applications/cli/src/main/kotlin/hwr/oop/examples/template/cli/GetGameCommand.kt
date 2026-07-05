package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.GamePhase

class GetGameCommand : CliktCommand(name = "getGame") {
	private val cliContext by requireObject<CliContext>()

	override fun run() {
		val game = cliContext.persistence.getGame(GameId(requireNotNull(cliContext.gameId)))
		echo("Game ID:  ${game.gameId.value}")
		echo("Phase:    ${game.getGamePhase()}")
		echo("Trump:    ${game.trump}")
		echo("Attacker: ${game.attackingPlayer.name}  |  Defender: ${game.defendingPlayer.name}")
		echo("")
		game.players.forEach { player ->
			echo("${player.name}: ${player.getHand().joinToString(", ") { "${it.rank} of ${it.suit}" }}")
		}
		val attackCards = game.bout.getAttackDeck()
		val defenseCards = game.bout.getDefenseDeck()
		if (attackCards.isNotEmpty()) {
			echo("")
			echo("Bout:")
			attackCards.forEachIndexed { i, attack ->
				val defense = defenseCards.getOrNull(i)
				if (defense != null) echo("  ${attack.rank} of ${attack.suit}  →  ${defense.rank} of ${defense.suit}")
				else echo("  ${attack.rank} of ${attack.suit}  →  (undefended)")
			}
		}
		if (game.getGamePhase() == GamePhase.FINISHED) echo("\nGame over!")
	}
}
