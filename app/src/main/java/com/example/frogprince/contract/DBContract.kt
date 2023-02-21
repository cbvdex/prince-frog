package com.example.frogprince.contract

import android.provider.BaseColumns

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLite (SQL Demo), and I have
// modified it and added some features and functions.
object DBContract {

    /* Inner class for User Table */
    class User : BaseColumns {
        companion object {
            val TABLE_NAME = "user"
            val COLUMN_USER_ID = "userid"
            val COLUMN_USER_NAME = "username"
            val COLUMN_EMAIL = "email"
            val COLUMN_PASSCODE = "passcode"
        }
    }

    /* Inner class for Avatar Table */
    class Avatar : BaseColumns {
        companion object {
            val TABLE_NAME = "avatar"
            val COLUMN_AVATAR_ID = "avatarId"
            val COLUMN_USER_ID = "userId"
            val COLUMN_NICKNAME = "nickName"
            val COLUMN_MEMORY_AID = "memoryAid"
            val COLUMN_PHONE_NUMBER = "phoneNum"
            val COLUMN_IMAGE_LINK = "imageLink"
            val COLUMN_QUIZ_STAGE = "quizStage"
        }
    }

    /* Inner class for MainQuestion Table */
    class MainQuestion : BaseColumns {
        companion object {
            val TABLE_NAME = "mainQuestion"
            val COLUMN_MAIN_QUESTION_ID = "mainQuestionId"
            val COLUMN_DESC = "description"
        }
    }

    /* Inner class for MainQuestion Table */
    class SubQuestion : BaseColumns {
        companion object {
            val TABLE_NAME = "subQuestion"
            val COLUMN_SUB_QUESTION_ID = "subQuestionId"
            val COLUMN_MAIN_QUESTION_ID = "mainQuestionId"
            val COLUMN_POSITIVE_NEGATIVE = "positiveOrNegative"
            val COLUMN_DESC = "description"
        }
    }

    /* Inner class for MainQuestion Table */
    class AnswerChoice : BaseColumns {
        companion object {
            val TABLE_NAME = "answerChoice"
            val COLUMN_CHOICE_ID = "choiceId"
            val COLUMN_SUB_QUESTION_ID = "subQuestionId"
            val COLUMN_CHOICE_SEQUENCE_ID = "choiceSequenceId"
            val COLUMN_DESC = "description"
        }
    }

    /* Inner class for MainQuestion Table */
    class UserAnswer : BaseColumns {
        companion object {
            val TABLE_NAME = "userAnswer"
            val COLUMN_USER_ANSWER_ID = "userAnswerId"
            val COLUMN_USER_ID = "userId"
            val COLUMN_AVATAR_ID = "avatarId"
            val COLUMN_MAIN_QUESTION_ID = "mainQuestionId"
            val COLUMN_SUB_QUESTION_ID = "subQuestionId"
            val COLUMN_POSITIVE_NEGATIVE = "positiveOrNegative"
            val COLUMN_RATING = "rating"
            val COLUMN_ANSWER = "answer"
        }
    }

    /* Inner class for MainQuestion Table */
    class AssessmentDetail : BaseColumns {
        companion object {
            val TABLE_NAME = "assessmentDetail"
            val COLUMN_ASSESS_DETAIL_ID = "assessmentDetailId"
            val COLUMN_USER_ID = "userId"
            val COLUMN_MAIN_QUESTION_ID = "mainQuestionId"
            val COLUMN_SUBJECT = "subject"
            val COLUMN_DESC = "description"
        }
    }

    /* Inner class for Image Table */
    class Image : BaseColumns {
        companion object {
            val TABLE_NAME = "imageTable"
            val COLUMN_IMAGE_ID = "image_id"
            val COLUMN_IMAGE = "img"
        }
    }
}