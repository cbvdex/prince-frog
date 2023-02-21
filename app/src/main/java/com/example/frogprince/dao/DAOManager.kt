package com.example.frogprince.dao

import android.content.Context
import java.lang.Exception

class DAOManager(context : Context){

    val context : Context = context
    val DATABASE_NAME = "FrogPrince.db"

    var userDAO : UserDAO = UserDAO(context)
    var avatarDAO : AvatarDAO = AvatarDAO(context)
    var mainQuestionDAO : MainQuestionDAO = MainQuestionDAO(context)
    var subQuestionDAO : SubQuestionDAO = SubQuestionDAO(context)
    var answerChoiceDAO : AnswerChoiceDAO = AnswerChoiceDAO(context)
    var userAnswerDAO : UserAnswerDAO = UserAnswerDAO(context)
    var assessmentDetailDAO : AssessmentDetailDAO = AssessmentDetailDAO(context)

    public fun deleteAllTablesAndDatabse() : Boolean{
        var result : Boolean = false
        try {
            context.deleteDatabase(DATABASE_NAME)
            result = true
        }catch(e : Exception){
            e.printStackTrace()
        }
        return result
    }

}