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
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.MainQuestionModel
import com.example.frogprince.db.model.SubQuestionModel
import com.example.frogprince.db.model.UserModel
import java.lang.Exception
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class MainQuestionDAO(context : Context) : SQLiteOpenHelper(context,
                                            MainQuestionDAO.DATABASE_NAME, null,
                                            MainQuestionDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_MAIN_QUESTION_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.MainQuestion.TABLE_NAME + " (" +
                    DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.MainQuestion.COLUMN_DESC + " TEXT)"

        val SQL_DELETE_MAIN_QUESTION_TABLE = "DROP TABLE IF EXISTS " + DBContract.MainQuestion.TABLE_NAME

        val SQL_LASTEST_CREATED_MAIN_QUESTION_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(MainQuestionDAO.SQL_DELETE_MAIN_QUESTION_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(MainQuestionDAO.SQL_CREATE_MAIN_QUESTION_TABLE)
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
    fun insertMainQuestion(mainQuestion: MainQuestionModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.MainQuestion.COLUMN_DESC, mainQuestion.description)

            newRowId = db.insert(DBContract.MainQuestion.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteMainQuestion(mainQuestionId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(mainQuestionId)

            // Issue SQL statement.
            db.delete(DBContract.MainQuestion.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun findMainQuestions(mainQuestionId: String): ArrayList<MainQuestionModel> {
        val mainQuestions = ArrayList<MainQuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.MainQuestion.TABLE_NAME
                    + " WHERE " + DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID + "='" + mainQuestionId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return mainQuestions
        }

        var mainQuestionId: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_DESC))

                mainQuestions.add(MainQuestionModel(mainQuestionId, description))
                cursor.moveToNext()
            }
        }
        return mainQuestions
    }

    fun listAllMainQuestions(): ArrayList<MainQuestionModel> {
        val mainQuestions = ArrayList<MainQuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.MainQuestion.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return mainQuestions
        }

        var mainQuestionId : String
        var description: String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_DESC))

                var subQuestions : ArrayList<SubQuestionModel>? = null
                if(ProgramManager.manager != null) {
                    subQuestions = ProgramManager.manager!!.subQuestionDAO.findSubQuestionsByMainQuestionId(mainQuestionId)
                }

                var mainQuestion : MainQuestionModel = MainQuestionModel(mainQuestionId, description)
                if(subQuestions != null) {
                    mainQuestion.setSubQuestions(subQuestions)
                }

                mainQuestions.add(mainQuestion)
                cursor.moveToNext()
            }
        }
        return mainQuestions
    }

    fun getMainQuestionById(mainQuestionId : String): MainQuestionModel? {
        var mainQuestion : MainQuestionModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.MainQuestion.TABLE_NAME
                     + " where "+DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID+ " = "+mainQuestionId, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return mainQuestion
        }

        var mainQuestionId : String
        var description: String

        if (cursor!!.moveToFirst()) {
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_MAIN_QUESTION_ID))
                description = cursor.getString(cursor.getColumnIndex(DBContract.MainQuestion.COLUMN_DESC))

                var subQuestions : ArrayList<SubQuestionModel>? = null
                if(ProgramManager.manager != null) {
                    subQuestions = ProgramManager.manager!!.subQuestionDAO.findSubQuestionsByMainQuestionId(mainQuestionId)
                }

                mainQuestion = MainQuestionModel(mainQuestionId, description)
                if(subQuestions != null) {
                    mainQuestion.setSubQuestions(subQuestions)
                }
        }
        return mainQuestion
    }
}