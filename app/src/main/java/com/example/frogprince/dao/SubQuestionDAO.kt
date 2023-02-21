package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.frogprince.contract.DBContract
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.db.model.AnswerChoiceModel
import com.example.frogprince.db.model.MainQuestionModel
import com.example.frogprince.db.model.SubQuestionModel
import java.lang.Exception
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class SubQuestionDAO(context : Context) : SQLiteOpenHelper(context,
                                            SubQuestionDAO.DATABASE_NAME, null,
                                            SubQuestionDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_SUB_QUESTION_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.SubQuestion.TABLE_NAME + " (" +
                    DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID + " TEXT," +
                    DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE + " TEXT," +
                    DBContract.SubQuestion.COLUMN_DESC + " TEXT)"

        val SQL_DELETE_SUB_QUESTION_TABLE = "DROP TABLE IF EXISTS " + DBContract.SubQuestion.TABLE_NAME

        val SQL_LASTEST_CREATED_SUB_QUESTION_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(SubQuestionDAO.SQL_DELETE_SUB_QUESTION_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(SubQuestionDAO.SQL_CREATE_SUB_QUESTION_TABLE)
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
    fun insertSubQuestion(subQuestion: SubQuestionModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID, subQuestion.mainQuestionId)
            entity.put(DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE, subQuestion.positiveOrNegative)
            entity.put(DBContract.SubQuestion.COLUMN_DESC, subQuestion.description)

            newRowId = db.insert(DBContract.SubQuestion.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteSubQuestion(subQuestionId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(subQuestionId)

            // Issue SQL statement.
            db.delete(DBContract.SubQuestion.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun findSubQuestions(subQuestionId: String): ArrayList<SubQuestionModel> {
        val subQuestions = ArrayList<SubQuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.SubQuestion.TABLE_NAME
                    + " WHERE " + DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID + "='" + subQuestionId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return subQuestions
        }

        var subQuestionId: String
        var mainQuestionId : String
        var positiveOrNegative  : String
        var description  : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE))
                description = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_DESC))

                subQuestions.add(SubQuestionModel(subQuestionId, mainQuestionId, positiveOrNegative, description))
                cursor.moveToNext()
            }
        }
        return subQuestions
    }

    fun findSubQuestion(subQuestionId: String): SubQuestionModel? {
        var subQuestion : SubQuestionModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.SubQuestion.TABLE_NAME
                    + " WHERE " + DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID + "='" + subQuestionId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return subQuestion
        }

        var subQuestionId: String
        var mainQuestionId : String
        var positiveOrNegative  : String
        var description  : String

        if (cursor!!.moveToFirst()) {
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE))
                description = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_DESC))

                subQuestion = SubQuestionModel(subQuestionId, mainQuestionId, positiveOrNegative, description)
        }
        return subQuestion
    }

    fun listAllSubQuestions(): ArrayList<SubQuestionModel> {
        val subQuestions = ArrayList<SubQuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.SubQuestion.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return subQuestions
        }

        var subQuestionId: String
        var mainQuestionId : String
        var positiveOrNegative  : String
        var description  : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE))
                description = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_DESC))

                subQuestions.add(SubQuestionModel(subQuestionId, mainQuestionId, positiveOrNegative, description))
                cursor.moveToNext()
            }
        }
        return subQuestions
    }

    fun findSubQuestionsByMainQuestionId(mainQuestionId: String): ArrayList<SubQuestionModel> {
        val subQuestions = ArrayList<SubQuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.SubQuestion.TABLE_NAME
                    + " WHERE " + DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID + "='" + mainQuestionId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return subQuestions
        }

        var subQuestionId: String
        var mainQuestionId : String
        var positiveOrNegative  : String
        var description  : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_SUB_QUESTION_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_MAIN_QUESTION_ID))
                positiveOrNegative = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_POSITIVE_NEGATIVE))
                description = cursor.getString(cursor.getColumnIndex(DBContract.SubQuestion.COLUMN_DESC))

                var answerChoices : ArrayList<AnswerChoiceModel>? = null
                if(ProgramManager.manager != null) {
                    answerChoices = ProgramManager.manager!!.answerChoiceDAO.findAnswerChoicesBySubQuestionId(subQuestionId)
                }
                var subQuestion : SubQuestionModel = SubQuestionModel(subQuestionId, mainQuestionId, positiveOrNegative, description)
                if (answerChoices != null) {
                    subQuestion.setAnswerChoices(answerChoices)
                }

                subQuestions.add(subQuestion)
                cursor.moveToNext()
            }
        }
        return subQuestions
    }

}