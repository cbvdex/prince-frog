package com.example.frogprince.db.model

// ** Acknowledgement and Declaration of Code Use **
// This code idea is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class UserAnswerModel (val userAnswerId : String, val userId : String, val avatarId : String,
                        val mainQuestionId : String, val subQuestionId : String, val positiveOrNegative : String,
                        val rating : String, val answer : String){
}
