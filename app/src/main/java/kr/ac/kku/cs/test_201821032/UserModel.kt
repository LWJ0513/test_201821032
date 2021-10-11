package kr.ac.kku.cs.test_201821032

data class UserModel(
    val user_name: String,
    val user_id: String,
    val email: String,
    val profile_image: String
) {
    constructor() : this("", "", "", "")
}