package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class DefendCommand : CliktCommand(name = "defend") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the defending player.").required()
	private val attackCard by option(
		"--attack-card",
		help = "The attacking card on the table to cover, encoded as a string."
	).required()
	private val defenseCard by option(
		"--defense-card",
		help = "The card from hand used to cover the attack, encoded as a string."
	).required()
	
	override fun run(): Unit = TODO()
}
