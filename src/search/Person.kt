package search

data class Person(val firstName: String, val lastName: String = "", val email: String = "") {
    override fun toString(): String {
        return "$firstName $lastName $email".trim()
    }
}