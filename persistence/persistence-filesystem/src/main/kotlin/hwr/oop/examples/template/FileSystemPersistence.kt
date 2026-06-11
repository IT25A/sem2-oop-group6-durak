package hwr.oop.examples.template

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.ports.out.Persistence
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import kotlinx.serialization.json.Json

private val json = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
}

class FileSystemPersistence(
	configuration: FileSystemPersistenceConfiguration,
	private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : Persistence {

	private val directory = configuration.directory
	override fun getGame(id: UUID): Game {
		TODO("Not yet implemented")
	}

	override fun save(game: Game) {
		val path = directory / "${game.gameId}.json"
		fileSystem.write(path){
			writeUtf8(json.encodeToString(game))
		}
	}
}

//Okio: File Management
//kotlinx-serialization-json