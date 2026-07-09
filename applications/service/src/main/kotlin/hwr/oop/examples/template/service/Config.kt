package hwr.oop.examples.template.service

import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.SqlPersistence
import hwr.oop.examples.template.config.ConfigLoader
import hwr.oop.examples.template.config.PersistenceType
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.ports.out.GameRepository
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import hwr.oop.examples.template.ports.out.SaveGamePort
import okio.Path.Companion.toPath
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {

	private val appConfig = ConfigLoader.load()
	private val repository: GameRepository by lazy {
		when (appConfig.persistence) {
			PersistenceType.SQL -> SqlPersistence(
				appConfig.sql.jdbcUrl,
				appConfig.sql.username,
				appConfig.sql.password,
			) as GameRepository

			PersistenceType.FILE_SYSTEM -> FileSystemPersistence(
				configuration = FileSystemPersistenceConfiguration(
					directory = appConfig.fileSystem.directory.toPath()
				)
			)
		}
	}

	@Bean
	fun persistence(): GameRepository = repository

	@Bean
	fun newGameUseCase(saveGamePort: SaveGamePort): NewGameUseCase =
		NewGameUseCase(saveGamePort = saveGamePort)

	@Bean
	fun playCardUseCase(loadGameByIdPort: LoadGameByIdPort, saveGamePort: SaveGamePort): PlayCardUseCase =
		PlayCardUseCase(loadGameByIdPort = loadGameByIdPort, saveGamePort = saveGamePort)

	@Bean
	fun loadGameByIdQuery(loadGameByIdPort: LoadGameByIdPort): LoadGameByIdQuery =
		LoadGameByIdQuery(loadGameByIdPort = loadGameByIdPort)
}