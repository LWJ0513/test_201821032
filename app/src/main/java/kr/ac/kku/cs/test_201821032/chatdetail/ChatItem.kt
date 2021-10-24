package kr.ac.kku.cs.test_201821032.chatdetail

data class ChatItem(
  //  val profileImage: String,
    val message: String,
    val senderUid: String
) {
    constructor() : this( "","")
}