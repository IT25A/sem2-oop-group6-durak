package hwr.oop.examples.template.core

enum class Rank(val rankValue: Int, val label: String) {
    SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"),
    NINE(9, "9"), TEN(10, "10"), JACK(11, "J"),
    QUEEN(12, "Q"), KING(13, "K"), ACE(14, "A")
}