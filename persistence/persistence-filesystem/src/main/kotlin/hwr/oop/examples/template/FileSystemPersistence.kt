package hwr.oop.examples.template

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.Persistence
import okio.FileNotFoundException
import okio.FileSystem
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
	override fun getGame(id: GameId): Game {
		val path = directory / "${id.value}.json"
		val readString = try {
			fileSystem.read(path) {
				readUtf8()
			}
		} catch (e: FileNotFoundException) {
			throw FileNotFoundException("File not found: $path")
		}
		return json.decodeFromString<Game>(readString)
	}


	override fun save(game: Game) {
		val path = directory / "${game.gameId.value}.json"
		fileSystem.createDirectories(directory)
		fileSystem.write(path){
			writeUtf8(json.encodeToString(game))
		}
	}
}

//Okio: File Management
//kotlinx-serialization-json