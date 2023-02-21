package com.example.frogprince.db.model

// ** Acknowledgement and Declaration of Code Use **
// This code idea is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class MainQuestionModel (val mainQuestionId : String, val description : String){

    var subQuestions : ArrayList<SubQuestionModel>? = null

    fun setSubQuestions(subQs : List<SubQuestionModel>){
        subQuestions = ArrayList<SubQuestionModel>(subQs)
    }

}
