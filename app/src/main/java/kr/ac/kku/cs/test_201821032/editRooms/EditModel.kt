package kr.ac.kku.cs.test_201821032.editRooms

data class EditModel(
    val roomNumber:String,
    val hobby: String,
    val onOff: String
) {
    constructor(): this( "","","")
}