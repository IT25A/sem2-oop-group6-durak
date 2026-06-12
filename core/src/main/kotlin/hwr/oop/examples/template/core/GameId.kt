package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
//@Serializable
value class GameId(val value: String) {
    companion object {
        fun random(): GameId = GameId(UUID.randomUUID().toString())
    }
}