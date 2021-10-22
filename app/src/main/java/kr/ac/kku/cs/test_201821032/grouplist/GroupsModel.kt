package kr.ac.kku.cs.test_201821032.grouplist

data class GroupsModel(
    val roomManager: String,
    val title: String,
    val createAt: Long,
    val description: String,
    val hashTag: String,
    val imageUrl: String,
    val locationName: String,    //빌딩이름
    val locationAddress: String,
    val latitude: Float,    //위도
    val longitude: Float ,  //경도
    val roomNumber: String,
    val hobby: String
) {
    constructor() : this("", "", 0, "", "", "", "", "", 0F, 0F,"","")
}