package hwr.oop.examples.template.service

import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.core.Card
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.core.Suit
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.ports.out.GameRepository
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import hwr.oop.examples.template.ports.out.SaveGamePort
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(
	webEnvironment = MOCK,
	properties = ["spring.main.allow-bean-definition-overriding=true"]
)
class ServiceFileSystemTest {

	@TestConfiguration
	class Config {
		private val fakeFileSystem = FakeFileSystem()
		private val tempDir = "/tmp/service-fs-test".toPath()
		private val gameRepository: FileSystemPersistence = FileSystemPersistence(
			FileSystemPersistenceConfiguration(tempDir),
			fakeFileSystem.also { it.createDirectories(tempDir) }
		)

		@Bean
		@Primary
		fun persistence(): GameRepository = gameRepository

		@Bean
		fun newGameUseCase(saveGamePort: SaveGamePort) = NewGameUseCase(
			saveGamePort = saveGamePort
		)

		@Bean
		fun playCardUseCase(saveGamePort: SaveGamePort, loadGameByIdPort: LoadGameByIdPort) = PlayCardUseCase(
			loadGameByIdPort = loadGameByIdPort,
			saveGamePort = saveGamePort,
		)

		@Bean
		fun loadGameByIdQuery(loadGameByIdPort: LoadGameByIdPort) = LoadGameByIdQuery(
			loadGameByIdPort = loadGameByIdPort,
		)
	}

	@Autowired
	private lateinit var webApplicationContext: WebApplicationContext

	@Autowired
	private lateinit var gameRepository: GameRepository

	private lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
	}

	@Test
	fun `POST games creates a new game and returns 201 with gameId`() {
		// given
		val body = """{"playerIds": ["Alice", "Bob"]}"""

		// when / then
		mockMvc.perform(
			post("/games")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.gameId").isNotEmpty)
	}

	@Test
	fun `GET games returns current game state`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		gameRepository.save(game)

		// when / then
		mockMvc.perform(get("/games/${game.gameId.value}"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.gameId").value(game.gameId.value))
			.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
			.andExpect(jsonPath("$.trumpSuit").isNotEmpty)
			.andExpect(jsonPath("$.playerHands").isArray)
	}

	@Test
	fun `GET games returns 500 for unknown gameId`() {
		// given
		val unknownId = GameId.random().value

		// when / then
		mockMvc.perform(get("/games/$unknownId"))
			.andExpect(status().isInternalServerError)
	}

	@Test
	fun `POST games attacks adds card to table and returns updated state`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		val attacker = game.attackingPlayer
		val attackCard = attacker.getHand().first()
		gameRepository.save(game)

		val body = """{"playerId": "${attacker.name}", "card": {"suit": "${attackCard.suit.name}", "rank": "${attackCard.rank.name}"}}"""

		// when / then
		mockMvc.perform(
			post("/games/${game.gameId.value}/attacks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.table[0].attackCard.suit").value(attackCard.suit.name))
			.andExpect(jsonPath("$.table[0].attackCard.rank").value(attackCard.rank.name))
	}

	@Test
	fun `POST games attacks returns 400 when wrong player attacks`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		gameRepository.save(game)

		val body = """{"playerId": "${game.defendingPlayer.name}", "card": {"suit": "CLUBS", "rank": "SIX"}}"""

		// when / then
		mockMvc.perform(
			post("/games/${game.gameId.value}/attacks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isBadRequest)
	}

	@Test
	fun `POST games defenses returns 400 when wrong player defends`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		game.bout.addAttackCard(Card(Suit.CLUBS, Rank.SIX))
		gameRepository.save(game)

		val body = """
			{
				"playerId": "${game.attackingPlayer.name}",
				"attackCard": {"suit": "CLUBS", "rank": "SIX"},
				"defenseCard": {"suit": "CLUBS", "rank": "SEVEN"}
			}
		""".trimIndent()

		// when / then
		mockMvc.perform(
			post("/games/${game.gameId.value}/defenses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isBadRequest)
	}

	@Test
	fun `POST games passes returns 400 when defending player tries to pass`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		gameRepository.save(game)

		val body = """{"playerId": "${game.defendingPlayer.name}"}"""

		// when / then
		mockMvc.perform(
			post("/games/${game.gameId.value}/passes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isBadRequest)
	}

	@Test
	fun `POST games passes returns updated state when attacker passes`() {
		// given
		val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
		gameRepository.save(game)

		val body = """{"playerId": "${game.attackingPlayer.name}"}"""

		// when / then
		mockMvc.perform(
			post("/games/${game.gameId.value}/passes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.gameId").value(game.gameId.value))
	}
}