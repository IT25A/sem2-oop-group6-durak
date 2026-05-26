package hwr.oop.examples.template.core

class EmptyDeckException(message: String) : Exception(message)
class InvalidMoveException(message: String) : Exception(message)
class PlayerNotFoundException(message: String) : Exception(message)
class InvalidPlayerTurn(message: String) : Exception(message)