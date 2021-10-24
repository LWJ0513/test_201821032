package kr.ac.kku.cs.test_201821032.grouplist

data class QnAChatItem(
  //  val profileImage: String,
    val message: String,
    val senderUid: String,
    val roomManager: String
) {
    constructor() : this( "","","")
}