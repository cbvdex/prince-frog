package com.example.frogprince.db.model

// ** Acknowledgement and Declaration of Code Use **
// This code idea is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class SubQuestionModel (val subQuestionId : String, val mainQuestionId : String,
                   val positiveOrNegative : String, val description : String){

    var answerChoices : ArrayList<AnswerChoiceModel>? = null

    fun setAnswerChoices(choices : List<AnswerChoiceModel>){
        answerChoices = ArrayList<AnswerChoiceModel>(choices)
    }

}

