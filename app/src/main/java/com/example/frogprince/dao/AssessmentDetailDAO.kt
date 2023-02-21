package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.frogprince.contract.DBContract
import com.example.frogprince.db.model.AssessmentDetailModel
import com.example.frogprince.db.model.AvatarModel
import java.lang.Exception
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class AssessmentDetailDAO(context : Context) : SQLiteOpenHelper(context,
                                            AssessmentDetailDAO.DATABASE_NAME, null,
                                            AssessmentDetailDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_ASSESS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.AssessmentDetail.TABLE_NAME + " (" +
                    DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.AssessmentDetail.COLUMN_USER_ID + " TEXT," +
                    DBContract.AssessmentDetail.COLUMN_MAIN_QUESTION_ID + " TEXT," +
                    DBContract.AssessmentDetail.COLUMN_SUBJECT + " TEXT," +
                    DBContract.AssessmentDetail.COLUMN_DESC + " TEXT)"

        val SQL_DELETE_ASSESS_TABLE = "DROP TABLE IF EXISTS " + DBContract.AssessmentDetail.TABLE_NAME

        val SQL_LASTEST_CREATED_ASSESS_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(AssessmentDetailDAO.SQL_DELETE_ASSESS_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(AssessmentDetailDAO.SQL_CREATE_ASSESS_TABLE)
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
    fun insertAssessment(assess: AssessmentDetailModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.AssessmentDetail.COLUMN_USER_ID, assess.userId)
            entity.put(DBContract.AssessmentDetail.COLUMN_MAIN_QUESTION_ID, assess.mainQuestionId)
            entity.put(DBContract.AssessmentDetail.COLUMN_SUBJECT, assess.subject)
            entity.put(DBContract.AssessmentDetail.COLUMN_DESC, assess.description)
            newRowId = db.insert(DBContract.AssessmentDetail.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAssessmentDetail(assessId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(assessId)

            // Issue SQL statement.
            db.delete(DBContract.AssessmentDetail.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAssessmentDetailByUser(userId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.AssessmentDetail.COLUMN_USER_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(userId)

            // Issue SQL statement.
            db.delete(DBContract.AssessmentDetail.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun findAssessmentDetails(assessId: String): ArrayList<AssessmentDetailModel> {
        val assessDetails = ArrayList<AssessmentDetailModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AssessmentDetail.TABLE_NAME
                    + " WHERE " + DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID + "='" + assessId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return assessDetails
        }

        var assessmentDetailId : String
        var userId: String
        var mainQuestionId : String
        var subject: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                assessmentDetailId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_USER_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_MAIN_QUESTION_ID))
                subject = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_SUBJECT))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_DESC))

                assessDetails.add(AssessmentDetailModel(assessmentDetailId, userId, mainQuestionId, subject, description))
                cursor.moveToNext()
            }
        }
        return assessDetails
    }

    fun findAssessmentDetailsForUser(userId: String): ArrayList<AssessmentDetailModel> {
        val assessDetails = ArrayList<AssessmentDetailModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AssessmentDetail.TABLE_NAME
                    + " WHERE " + DBContract.AssessmentDetail.COLUMN_USER_ID + "='" + userId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return assessDetails
        }

        var assessmentDetailId : String
        var userId: String
        var mainQuestionId : String
        var subject: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                assessmentDetailId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_USER_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_MAIN_QUESTION_ID))
                subject = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_SUBJECT))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_DESC))

                assessDetails.add(AssessmentDetailModel(assessmentDetailId, userId, mainQuestionId, subject, description))
                cursor.moveToNext()
            }
        }
        return assessDetails
    }

    fun listAllAssessmentDetails(): ArrayList<AssessmentDetailModel> {
        val assessDetails = ArrayList<AssessmentDetailModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.AssessmentDetail.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return assessDetails
        }

        var assessmentDetailId : String
        var userId: String
        var mainQuestionId : String
        var subject: String
        var description : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                assessmentDetailId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_ASSESS_DETAIL_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_USER_ID))
                mainQuestionId = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_MAIN_QUESTION_ID))
                subject = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_SUBJECT))
                description = cursor.getString(cursor.getColumnIndex(DBContract.AssessmentDetail.COLUMN_DESC))

                assessDetails.add(AssessmentDetailModel(assessmentDetailId, userId, mainQuestionId, subject, description))
                cursor.moveToNext()
            }
        }
        return assessDetails
    }


}