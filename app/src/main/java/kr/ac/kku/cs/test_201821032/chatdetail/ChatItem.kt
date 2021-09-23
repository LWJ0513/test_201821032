package kr.ac.kku.cs.test_201821032.chatdetail

data class ChatItem(
    val senderName: String,
    val message: String
) {
    constructor() : this("", "")
}