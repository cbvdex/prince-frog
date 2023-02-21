package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.frogprince.contract.DBContract
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserAnswerModel
import java.lang.Exception
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class UserAnswerDAO(context : Context) : SQLiteOpenHelper(context,
                                        UserAnswerDAO.DATABASE_NAME, null,
                                        UserAnswerDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_USER_ANSWER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.UserAnswer.TABLE_NAME + " (" +
                    DBContract.UserAnswer.COLUMN_USER_ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.UserAnswer.COLUMN_USER_ID + " TEXT," +
                    DBContract.UserAnswer.COLUMN_AVATAR_ID + " TEXT," +
                    DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID + " TEXT," +
                    DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID + " TEXT," +
                    DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE + " TEXT," +
                    DBContract.UserAnswer.COLUMN_RATING + " TEXT," +
                    DBContract.UserAnswer.COLUMN_ANSWER + " TEXT)"

        val SQL_DELETE_USER_ANSWER_TABLE = "DROP TABLE IF EXISTS " + DBContract.UserAnswer.TABLE_NAME

        val SQL_LASTEST_CREATED_USER_ANSWER_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(UserAnswerDAO.SQL_DELETE_USER_ANSWER_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(UserAnswerDAO.SQL_CREATE_USER_ANSWER_TABLE)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createDB(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        deleteDB(db)
        createDB(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        deleteDB(db)
        createDB(db)
    }

    fun eraseDB(db: SQLiteDatabase): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase
            deleteDB(db)
            createDB(db)
        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun createDB(){
        try {
            val db = writableDatabase
            createDB(db)
        }catch(e : Exception){
            e.printStackTrace()
        }
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUserAnswer(userAnswer: UserAnswerModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.UserAnswer.COLUMN_USER_ID, userAnswer.userId)
            entity.put(DBContract.UserAnswer.COLUMN_AVATAR_ID, userAnswer.avatarId)
            entity.put(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID, userAnswer.mainQuestionId)
            entity.put(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID, userAnswer.subQuestionId)
            entity.put(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE, userAnswer.positiveOrNegative)
            entity.put(DBContract.UserAnswer.COLUMN_RATING, userAnswer.rating)
            entity.put(DBContract.UserAnswer.COLUMN_ANSWER, userAnswer.answer)

            newRowId = db.insert(DBContract.UserAnswer.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUserAnswer(userAnswerId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.UserAnswer.COLUMN_USER_ANSWER_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(userAnswerId)

            // Issue SQL statement.
            db.delete(DBContract.UserAnswer.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUserAnswerForAvatarId(avatarId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.UserAnswer.COLUMN_AVATAR_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(avatarId)

            // Issue SQL statement.
            db.delete(DBContract.UserAnswer.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun findUserAnswers(userAnswerId: String): ArrayList<UserAnswerModel> {
        val userAnswers = ArrayList<UserAnswerModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserAnswer.TABLE_NAME
                    + " WHERE " + DBContract.UserAnswer.COLUMN_USER_ANSWER_ID + "='" + userAnswerId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return userAnswers
        }

        var userAnswerId: String
        var userId : String
        var avatarId: String
        var mainQuestionId : String
        var subQuestionId: String
        var positiveOrNegative : String
        var rating: String
        var answer : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userAnswerId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ANSWER_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ID))
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_AVATAR_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE))
                rating = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_RATING))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_ANSWER))

                userAnswers.add(UserAnswerModel(userAnswerId, userId, avatarId, mainQuestionId,
                                                subQuestionId, positiveOrNegative, rating, answer))
                cursor.moveToNext()
            }
        }
        return userAnswers
    }

    fun findUserAnswersByUserByMainquestion(userId: String, mainquestionId: String): ArrayList<UserAnswerModel> {
        val userAnswers = ArrayList<UserAnswerModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserAnswer.TABLE_NAME
                    + " WHERE " + DBContract.UserAnswer.COLUMN_USER_ID + "='" + userId + "'"
                    + " AND " + DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID + "='" + mainquestionId + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return userAnswers
        }

        var userAnswerId: String
        var userId : String
        var avatarId: String
        var mainQuestionId : String
        var subQuestionId: String
        var positiveOrNegative : String
        var rating: String
        var answer : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userAnswerId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ANSWER_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ID))
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_AVATAR_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE))
                rating = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_RATING))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_ANSWER))

                userAnswers.add(UserAnswerModel(userAnswerId, userId, avatarId, mainQuestionId,
                    subQuestionId, positiveOrNegative, rating, answer))
                cursor.moveToNext()
            }
        }
        return userAnswers
    }

    fun findUserAnswersByUserBySubquestion(userId: String, subquestionId: String): ArrayList<UserAnswerModel> {
        val userAnswers = ArrayList<UserAnswerModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserAnswer.TABLE_NAME
                    + " WHERE " + DBContract.UserAnswer.COLUMN_USER_ID + "='" + userId + "'"
                    + " AND " + DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID + "='" + subquestionId + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return userAnswers
        }

        var userAnswerId: String
        var userId : String
        var avatarId: String
        var mainQuestionId : String
        var subQuestionId: String
        var positiveOrNegative : String
        var rating: String
        var answer : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userAnswerId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ANSWER_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ID))
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_AVATAR_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE))
                rating = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_RATING))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_ANSWER))

                userAnswers.add(UserAnswerModel(userAnswerId, userId, avatarId, mainQuestionId,
                    subQuestionId, positiveOrNegative, rating, answer))
                cursor.moveToNext()
            }
        }
        return userAnswers
    }

    fun findUserAnswersByUserByAvatar(userId: String, avatarId : String): ArrayList<UserAnswerModel> {
        val userAnswers = ArrayList<UserAnswerModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserAnswer.TABLE_NAME
                    + " WHERE " + DBContract.UserAnswer.COLUMN_USER_ID + "='" + userId + "' AND "
                    + DBContract.UserAnswer.COLUMN_AVATAR_ID + "='" + avatarId + "'"
                    , null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return userAnswers
        }

        var userAnswerId: String
        var userId : String
        var avatarId: String
        var mainQuestionId : String
        var subQuestionId: String
        var positiveOrNegative : String
        var rating: String
        var answer : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userAnswerId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ANSWER_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ID))
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_AVATAR_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE))
                rating = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_RATING))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_ANSWER))

                userAnswers.add(UserAnswerModel(userAnswerId, userId, avatarId, mainQuestionId,
                    subQuestionId, positiveOrNegative, rating, answer))
                cursor.moveToNext()
            }
        }
        return userAnswers
    }

    fun listAllUserAnswers(): ArrayList<UserAnswerModel> {
        val userAnswers = ArrayList<UserAnswerModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserAnswer.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return userAnswers
        }

        var userAnswerId: String
        var userId : String
        var avatarId: String
        var mainQuestionId : String
        var subQuestionId: String
        var positiveOrNegative : String
        var rating: String
        var answer : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userAnswerId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ANSWER_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_USER_ID))
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_AVATAR_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_MAIN_QUESTION_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_SUB_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_POSITIVE_NEGATIVE))
                rating = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_RATING))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserAnswer.COLUMN_ANSWER))

                userAnswers.add(UserAnswerModel(userAnswerId, userId, avatarId, mainQuestionId,
                    subQuestionId, positiveOrNegative, rating, answer))
                cursor.moveToNext()
            }
        }
        return userAnswers
    }



}