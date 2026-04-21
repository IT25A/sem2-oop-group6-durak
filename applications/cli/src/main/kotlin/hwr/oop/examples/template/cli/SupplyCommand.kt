package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class SupplyCommand : CliktCommand(name = "supply") {
	private val gameId by requireObject<String>()
	private val playerId by option(
		"--player-id",
		help = "The ID of the player supplying an additional attack card."
	).required()
	private val card by option(
		"--card",
		help = "The card to supply, encoded as a string (e.g. define your own format)."
	).required()
	
	override fun run(): Unit = TODO()
}
