package com.example.frogprince.contract

import com.example.frogprince.dao.*
import com.example.frogprince.db.model.AnswerChoiceModel
import com.example.frogprince.db.model.MainQuestionModel
import com.example.frogprince.db.model.SubQuestionModel
import com.example.frogprince.db.model.UserModel
import java.util.ArrayList

object ProgramManager {

    var manager : DAOManager? = null
    var mainQuestions : ArrayList<MainQuestionModel>? = null
    var pendingAssessments: HashMap<String, String>? = null

    class FrogPrinceSystem {
        companion object {

            var loggedIn: Boolean = false
            var user: UserModel? = null
            var forcedInitialization = false

        }
    }

    fun initializeAppWithDefaultSetUp() : Boolean{

        var result : Boolean = false
        if (manager != null) {
            //reset DB
            var existingDbDeleted : Boolean = manager!!.deleteAllTablesAndDatabse()

            if(existingDbDeleted) {

                manager!!.userDAO.createDB()
                manager!!.avatarDAO.createDB()
                manager!!.mainQuestionDAO.createDB()
                manager!!.subQuestionDAO.createDB()
                manager!!.answerChoiceDAO.createDB()
                manager!!.userAnswerDAO.createDB()
                manager!!.assessmentDetailDAO.createDB()
                manager!!.imageDAO.createDB()

                resetQuestions()
                result = true

            } // existing db deleted and set up new
        }
        return result
    }

    fun resetQuestions(){
        //** Reset questions **//

        val mainQuestionArray = arrayOf(
            "Did she like how he treated her?",
            "Was his distinguishable feature (physique/personality) positive?",
            "Was his personality good match for her?",
            "Does she fall for this type of person a lot?",
            "If things went well, is he close to her ideal partner model?"
        )

        val subQuestionArray = arrayOf(
            "How did he made her feel the way she liked the most?",
            "What made her the most upset of the way he treated her?",

            "What was the most attractive point (feature or trait) of him?",
            "Describe the most disappointing point (feature or trait) of him.",

            "What was the best point of his personality that harmonized with hers?",
            "What was the worst side of his personality that ruined relationship?",

            "What is the key attractiveness of this type of person?",
            "What was so unique of him that made her give him a chance?",

            "You said Yes. Rate it in the scale.",
            "You said No. Rate it in the scale."
        )

        val answerOptionArray = arrayOf(
            "Protected",
            "Loved",
            "Respected",
            "Liberated",

            "Condescending",
            "Taking for granted",
            "Careless words",
            "Not enough attention",

            "Health-conscious",
            "Muscular Body",
            "Well-dressed",
            "Good-looking",

            "Laziness",
            "Not ambitious",
            "Poor Hygene",
            "Negative",

            "Talk a lot",
            "Different personality",
            "Try new things",
            "Read a lot",

            "Has a temper",
            "Too much alike",
            "Liked to be alone",
            "Too analytical",

            "Pretty face/body",
            "Came from Money",
            "Intelligence",
            "Good humour",

            "Curiosity",
            "Spontaneous decision",
            "Wanted to explore",
            "Make no sense",

            "4 stars (Best)",
            "3 stars (Please date again)",
            "2 stars (Good)",
            "1 star (Positive)",

            "-1 (Negative) ",
            "-2 (Bad)",
            "-3 (Never date again)",
            "-4 (Worst)"
        )

        var indexSub: Int = 0
        var indexAnsOpt: Int = 0

        for (idexMain in mainQuestionArray.indices) {

            val mq1Desc: String = mainQuestionArray[idexMain]
            val mq1: MainQuestionModel = MainQuestionModel("", mq1Desc)
            val mq1Id: Long = manager!!.mainQuestionDAO.insertMainQuestion(mq1)

            if (!mq1Id.equals(-1)) {
                val mq1SubDesc1: String = subQuestionArray[indexSub++]
                val mq1SubDesc2: String = subQuestionArray[indexSub++]
                val mq1Sub1: SubQuestionModel =
                    SubQuestionModel("", mq1Id.toString(), "P", mq1SubDesc1)
                val mq1Sub2: SubQuestionModel =
                    SubQuestionModel("", mq1Id.toString(), "N", mq1SubDesc2)
                val mq1Sub1Id: Long = manager!!.subQuestionDAO.insertSubQuestion(mq1Sub1)
                val mq1Sub2Id: Long = manager!!.subQuestionDAO.insertSubQuestion(mq1Sub2)

                if (!mq1Sub1Id.equals(-1)) {
                    val mq1Sub1Option1: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub1Option2: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub1Option3: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub1Option4: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub1Op1: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub1Id.toString(), "1", mq1Sub1Option1)
                    val mq1Sub1Op2: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub1Id.toString(), "2", mq1Sub1Option2)
                    val mq1Sub1Op3: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub1Id.toString(), "3", mq1Sub1Option3)
                    val mq1Sub1Op4: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub1Id.toString(), "4", mq1Sub1Option4)
                    val mq1Sub1Opt1Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub1Op1)
                    val mq1Sub1Opt2Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub1Op2)
                    var mq1Sub1Opt3Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub1Op3)
                    val mq1Sub1Opt4Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub1Op4)
                }

                if (!mq1Sub2Id.equals(-1)) {
                    val mq1Sub2Option1: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub2Option2: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub2Option3: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub2Option4: String = answerOptionArray[indexAnsOpt++]
                    val mq1Sub2Op1: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub2Id.toString(), "1", mq1Sub2Option1)
                    val mq1Sub2Op2: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub2Id.toString(), "2", mq1Sub2Option2)
                    val mq1Sub2Op3: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub2Id.toString(), "3", mq1Sub2Option3)
                    val mq1Sub2Op4: AnswerChoiceModel =
                        AnswerChoiceModel("", mq1Sub2Id.toString(), "4", mq1Sub2Option4)
                    val mq1Sub2Opt1Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub2Op1)
                    val mq1Sub2Opt2Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub2Op2)
                    val mq1Sub2Opt3Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub2Op3)
                    val mq1Sub2Opt4Id: Long =
                        manager!!.answerChoiceDAO.insertAnswerChoice(mq1Sub2Op4)
                }
            }

        }  //set up iteration ends
    }


}