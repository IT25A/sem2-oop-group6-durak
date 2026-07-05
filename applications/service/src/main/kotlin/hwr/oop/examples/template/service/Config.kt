package hwr.oop.examples.template.service

import hwr.oop.examples.template.FileSystemPersistence
import hwr.oop.examples.template.FileSystemPersistenceConfiguration
import hwr.oop.examples.template.SqlPersistence
import hwr.oop.examples.template.config.ConfigLoader
import hwr.oop.examples.template.config.PersistenceType
import hwr.oop.examples.template.ports.out.Persistence
import okio.Path.Companion.toPath
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {

	private val appConfig = ConfigLoader.load()
	private val persistence: Persistence by lazy {
		when (appConfig.persistence) {
			PersistenceType.SQL -> SqlPersistence(
				appConfig.sql.jdbcUrl,
				appConfig.sql.username,
				appConfig.sql.password,
			)

			PersistenceType.FILE_SYSTEM -> FileSystemPersistence(
				configuration = FileSystemPersistenceConfiguration(
					directory = appConfig.fileSystem.directory.toPath()
				)
			)
		} as Persistence
	}

	@Bean
	@ConditionalOnMissingBean
	fun persistence(): Persistence = persistence
}