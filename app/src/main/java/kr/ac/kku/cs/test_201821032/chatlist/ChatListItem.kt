package kr.ac.kku.cs.test_201821032.chatlist

data class ChatListItem(
    val entryId: String,
    val managerId: String,
    val roomName: String,
    val key: Long
) {
    constructor() : this("", "", "", 0)
}
