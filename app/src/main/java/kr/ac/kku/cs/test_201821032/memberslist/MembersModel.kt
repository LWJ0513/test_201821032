package kr.ac.kku.cs.test_201821032.memberslist

data class MembersModel(
    val roomManager: String,
    val title: String,
    val createAt: Long,
    val description: String,
    val imageUrl: String
) {
    constructor(): this("", "", 0, "", "")
}