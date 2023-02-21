package com.example.frogprince.db.model

import android.provider.BaseColumns

// ** Acknowledgement and Declaration of Code Use **
// This code idea is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class AvatarModel (val avatarId : String, val userId : String,
                   val nickName : String, val memoryAid : String,
                   val phoneNum : String, val imageLink : String, var quizStage: String){
}

