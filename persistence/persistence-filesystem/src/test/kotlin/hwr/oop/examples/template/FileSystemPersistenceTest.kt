package hwr.oop.examples.template

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FileSystemPersistenceTest {
	
	private val fakeFileSystem = FakeFileSystem()
	private val tempDir = "/tmp/template-test".toPath()
	private val sut: FileSystemPersistence
	
	init {
		fakeFileSystem.createDirectories(tempDir)
		sut = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem
		)
	}
	
	@AfterEach
	fun tearDown() {
		fakeFileSystem.checkNoOpenFiles()
	}
	
	@Test
	fun `accessing non-existent file throws exception` () {
		val game = Game.createRandomGame(
			playerNames = listOf("a", "b"),
			gameId = GameId("a")
		)
		sut.save(game)
		assertThrows<FileNotFoundException> {sut.getGame(GameId("b"))  }
	}

	@Test
	fun `verify saving and loading a Game returns the same game` (){
		val game = Game.createRandomGame(
			playerNames = listOf("alpha", "beta", "gamma"),
			gameId = GameId("a")
		)
		sut.save(game)
		val loadedGame = sut.getGame(game.gameId)
		assertThat(loadedGame == game)
	}
}
