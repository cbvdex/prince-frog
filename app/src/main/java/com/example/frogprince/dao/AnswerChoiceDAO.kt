package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.frogprince.contract.DBContract
import com.example.frogprince.db.model.AnswerChoiceModel
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.SubQuestionModel
import java.lang.Exception
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class AnswerChoiceDAO(context : Context) : SQLiteOpenHelper(context,
                                            AnswerChoiceDAO.DATABASE_NAME, null,
                                            AnswerChoiceDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_ANSWER_CHOICE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.AnswerChoice.TABLE_NAME + " (" +
                    DBContract.AnswerChoice.COLUMN_CHOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID + " TEXT," +
                    DBContract.AnswerChoice.COLUMN_CHOICE_SEQUENCE_ID + " TEXT," +
                    DBContract.AnswerChoice.COLUMN_DESC + " TEXT)"

        val SQL_DELETE_ANSWER_CHOICE_TABLE = "DROP TABLE IF EXISTS " + DBContract.AnswerChoice.TABLE_NAME

        val SQL_LASTEST_CREATED_ANSWER_CHOICE_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(AnswerChoiceDAO.SQL_DELETE_ANSWER_CHOICE_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(AnswerChoiceDAO.SQL_CREATE_ANSWER_CHOICE_TABLE)
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
    fun insertAnswerChoice(answerChoice: AnswerChoiceModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID, answerChoice.subQuestionId)
            entity.put(DBContract.AnswerChoice.COLUMN_CHOICE_SEQUENCE_ID, answerChoice.choiceSequenceId)
            entity.put(DBContract.AnswerChoice.COLUMN_DESC, answerChoice.description)
            newRowId = db.insert(DBContract.AnswerChoice.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAnswerChoice(choiceId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.AnswerChoice.COLUMN_CHOICE_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(choiceId)

            // Issue SQL statement.
            db.delete(DBContract.AnswerChoice.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun findAnswerChoices(choiceId: String): ArrayList<AnswerChoiceModel> {
        val answerChoices = ArrayList<AnswerChoiceModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AnswerChoice.TABLE_NAME
                    + " WHERE " + DBContract.AnswerChoice.COLUMN_CHOICE_ID + "='" + choiceId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return answerChoices
        }

        var choiceId: String
        var subQuestionId : String
        var choiceSequenceId: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                choiceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID))
                choiceSequenceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_SEQUENCE_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_DESC))

                answerChoices.add(AnswerChoiceModel(choiceId, subQuestionId, choiceSequenceId, description))
                cursor.moveToNext()
            }
        }
        return answerChoices
    }

    fun listAllAnswerChoices(): ArrayList<AnswerChoiceModel> {
        val answerChoices = ArrayList<AnswerChoiceModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AnswerChoice.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return answerChoices
        }

        var choiceId: String
        var subQuestionId : String
        var choiceSequenceId: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                choiceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID))
                choiceSequenceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_SEQUENCE_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_DESC))

                answerChoices.add(AnswerChoiceModel(choiceId, subQuestionId, choiceSequenceId, description))
                cursor.moveToNext()
            }
        }
        return answerChoices
    }

    fun findAnswerChoicesBySubQuestionId(subQuestionId: String): ArrayList<AnswerChoiceModel> {
        val answerChoices = ArrayList<AnswerChoiceModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AnswerChoice.TABLE_NAME
                    + " WHERE " + DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID + "='" + subQuestionId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return answerChoices
        }

        var choiceId: String
        var subQuestionId : String
        var choiceSequenceId: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                choiceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_ID))
                subQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_SUB_QUESTION_ID))
                choiceSequenceId = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_CHOICE_SEQUENCE_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AnswerChoice.COLUMN_DESC))

                answerChoices.add(AnswerChoiceModel(choiceId, subQuestionId, choiceSequenceId, description))
                cursor.moveToNext()
            }
        }
        return answerChoices
    }

}