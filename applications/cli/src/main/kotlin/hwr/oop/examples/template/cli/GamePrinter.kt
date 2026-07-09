package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GamePhase

fun CliktCommand.printGameState(game: Game) {
    echo("Game ID:  ${game.gameId.value}")
    echo("Phase:    ${game.getGamePhase()}")
    echo("Trump:    ${game.trump}")
    echo("Deck:     ${if (game.deck.getCards().isEmpty()) "(empty)" else game.deck.getCards().joinToString(", ") { it.toShortString() }}")
    echo("Attacker: ${game.attackingPlayer.name}  |  Defender: ${game.defendingPlayer.name}")
    echo("")
    game.players.forEach { player ->
        echo("${player.name}: ${player.getHand().joinToString(", ") { it.toShortString() }}")
    }
    echo("")
    val attackCards = game.bout.getAttackDeck()
    val defenseCards = game.bout.getDefenseDeck()
    echo("Bout:")
    if (attackCards.isNotEmpty()) {
        attackCards.forEachIndexed { i, attack ->
            val defense = defenseCards.getOrNull(i)
            if (defense != null) echo("${attack.toShortString()}  →  ${defense.toShortString()}")
            else echo("${attack.toShortString()}  →  (undefended)")
        }
    } else {
        echo("(empty)")
    }
    if (game.getGamePhase() == GamePhase.FINISHED) echo("\nGame over!")
}
