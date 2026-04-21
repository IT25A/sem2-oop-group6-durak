package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class AttackCommand : CliktCommand(name = "attack") {
	private val gameId by requireObject<String>()
	private val playerId by option("--player-id", help = "The ID of the attacking player.").required()
	private val card by option(
		"--card",
		help = "The card to play, encoded as a string (e.g. define your own format)."
	).required()
	
	override fun run(): Unit = TODO()
}
