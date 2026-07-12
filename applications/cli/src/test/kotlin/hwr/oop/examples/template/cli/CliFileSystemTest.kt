package hwr.oop.examples.template.cli

import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.GameRepository
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CliFileSystemTest {
	
	private val fakeFileSystem = FakeFileSystem()
	private val tempDir = "/tmp/cli-fs-test".toPath()
	private lateinit var persistence: FileSystemPersistence
	private lateinit var sut: ExampleBaseCommand


	private fun makeStorage(game: Game) = mutableMapOf(game.gameId.value to game)

	private fun makeContext(storage: MutableMap<String, Game>, gameId: String?): CliContext {
		val persistence = object : GameRepository {
			override fun loadById(id: GameId) =
				storage[id.value] ?: error("Game not found")
			override fun save(game: Game) { storage[game.gameId.value] = game }
		}
		return CliContext(
			newGameUseCase = NewGameUseCase(persistence),
			playCardUseCase = PlayCardUseCase(persistence, persistence),
			loadGameByIdQuery = LoadGameByIdQuery(persistence),
			gameId = gameId,
		)
	}
	@BeforeEach
	fun setUp() {
		fakeFileSystem.createDirectories(tempDir)
		persistence = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem
		)
		sut = ExampleBaseCommand()
	}

	@AfterEach
	fun tearDown() {
		fakeFileSystem.checkNoOpenFiles()
	}
	
	@Test
	fun `Loading a game by a valid ID returns the proper game`() {
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		val storage = makeStorage(game)
		val cliContext = makeContext(storage, game.gameId.value)
		persistence.save(game)
		val loadedGame = cliContext.loadGameByIdQuery.load(game.gameId)
		assertEquals(loadedGame, game)
	}
	@Test
	fun `Loading a game by an invalid ID throws an error`() {
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		val storage = makeStorage(game)
		persistence.save(game)
		val cliContext = makeContext(storage, game.gameId.value)
		val wrongId = GameId("game-id-123")
		org.junit.jupiter.api.assertThrows<IllegalStateException> {
			cliContext.loadGameByIdQuery.load(wrongId)
		}
	}
}
