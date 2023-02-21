package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.frogprince.contract.DBContract
import com.example.frogprince.dao.AvatarDAO.Companion.DATABASE_NAME
import com.example.frogprince.dao.AvatarDAO.Companion.DATABASE_VERSION
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserModel
import java.lang.Exception
import java.math.BigInteger
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class AvatarDAO(context : Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
){
    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_AVATAR_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.Avatar.TABLE_NAME + " (" +
                    DBContract.Avatar.COLUMN_AVATAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.Avatar.COLUMN_USER_ID + " TEXT," +
                    DBContract.Avatar.COLUMN_NICKNAME + " TEXT," +
                    DBContract.Avatar.COLUMN_MEMORY_AID + " TEXT," +
                    DBContract.Avatar.COLUMN_PHONE_NUMBER + " TEXT," +
                    DBContract.Avatar.COLUMN_IMAGE_LINK + " TEXT," +
                    DBContract.Avatar.COLUMN_QUIZ_STAGE + " TEXT)"

        val SQL_DELETE_AVATAR_TABLE = "DROP TABLE IF EXISTS " + DBContract.Avatar.TABLE_NAME

    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(SQL_DELETE_AVATAR_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(SQL_CREATE_AVATAR_TABLE)
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
    fun insertAvatar(avatar: AvatarModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.Avatar.COLUMN_USER_ID, avatar.userId)
            entity.put(DBContract.Avatar.COLUMN_NICKNAME, avatar.nickName)
            entity.put(DBContract.Avatar.COLUMN_MEMORY_AID, avatar.memoryAid)
            entity.put(DBContract.Avatar.COLUMN_PHONE_NUMBER, avatar.phoneNum)
            entity.put(DBContract.Avatar.COLUMN_IMAGE_LINK, avatar.imageLink)
            entity.put(DBContract.Avatar.COLUMN_QUIZ_STAGE, avatar.quizStage)
            newRowId = db.insert(DBContract.Avatar.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

//    @Throws(SQLiteConstraintException::class)
//    fun updateAvatar(avatar: AvatarModel): Int{
//        var result : Int = -1
//        try{
//            val db = writableDatabase
//            var cursor : Cursor? = null
//
//            // Create a new map of ContentValues (column names are the keys).
//            val entity = ContentValues()
//            entity.put(DBContract.Avatar.COLUMN_AVATAR_ID, avatar.avatarId)
//            entity.put(DBContract.Avatar.COLUMN_USER_ID, avatar.userId)
//            entity.put(DBContract.Avatar.COLUMN_NICKNAME, avatar.nickName)
//            entity.put(DBContract.Avatar.COLUMN_MEMORY_AID, avatar.memoryAid)
//            entity.put(DBContract.Avatar.COLUMN_PHONE_NUMBER, avatar.phoneNum)
//            entity.put(DBContract.Avatar.COLUMN_IMAGE_LINK, avatar.imageLink)
//            entity.put(DBContract.Avatar.COLUMN_QUIZ_STAGE, avatar.quizStage)
//            result = db.update(DBContract.Avatar.TABLE_NAME, entity, null, null)
//            System.out.println("update result = "+result)
//
//        } catch (e: SQLiteException) {
//            e.printStackTrace()
//        }
//
//        return result
//    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAvatar(avatarId: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.Avatar.COLUMN_AVATAR_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(avatarId)

            // Issue SQL statement.
            db.delete(DBContract.Avatar.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    fun getAvatarByAvatarId(avatarId : String): AvatarModel? {
        var avatar : AvatarModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.Avatar.TABLE_NAME + " where avatarId = "+avatarId, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return avatar
        }

        var avatarId : String
        var userId: String
        var nickName : String
        var memoryAid: String
        var phoneNum : String
        var imageLink : String
        var quizStage : String

        if (cursor!!.moveToFirst()) {
            avatarId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_AVATAR_ID))
            userId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_USER_ID))
            nickName = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_NICKNAME))
            memoryAid = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_MEMORY_AID))
            phoneNum = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_PHONE_NUMBER))
            imageLink = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_IMAGE_LINK))
            quizStage = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_QUIZ_STAGE))
            avatar = AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage)
        }
        return avatar
    }

    fun findAvatars(avatarId: String): ArrayList<AvatarModel> {
        val avatars = ArrayList<AvatarModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.Avatar.TABLE_NAME
                    + " WHERE " + DBContract.Avatar.COLUMN_AVATAR_ID + "='" + avatarId
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return avatars
        }

        var userId: String
        var nickName : String
        var memoryAid: String
        var phoneNum : String
        var imageLink : String
        var quizStage : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                //avatarId is already set.
                userId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_USER_ID))
                nickName = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_NICKNAME))
                memoryAid = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_MEMORY_AID))
                phoneNum = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_PHONE_NUMBER))
                imageLink = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_IMAGE_LINK))
                quizStage = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_QUIZ_STAGE))
                avatars.add(AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
                cursor.moveToNext()
            }
        }
        return avatars
    }

    fun findAvatarsForPendingAssessments(): ArrayList<AvatarModel> {
        var avatars = ArrayList<AvatarModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.Avatar.TABLE_NAME +
                    " where "+DBContract.Avatar.COLUMN_QUIZ_STAGE+" < 5", null)
        } catch (e: SQLiteException) {
            createDB(db)
            return avatars
        }

        var avatarId : String
        var userId: String
        var nickName : String
        var memoryAid: String
        var phoneNum : String
        var imageLink : String
        var quizStage : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_AVATAR_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_USER_ID))
                nickName = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_NICKNAME))
                memoryAid = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_MEMORY_AID))
                phoneNum = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_PHONE_NUMBER))
                imageLink = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_IMAGE_LINK))
                quizStage = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_QUIZ_STAGE))

                avatars.add(AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
                cursor.moveToNext()
            }
        }
        return avatars
    }

    fun listAllAvatars(): ArrayList<AvatarModel> {
        var avatars = ArrayList<AvatarModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.Avatar.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return avatars
        }

        var avatarId : String
        var userId: String
        var nickName : String
        var memoryAid: String
        var phoneNum : String
        var imageLink : String
        var quizStage : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                avatarId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_AVATAR_ID))
                userId = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_USER_ID))
                nickName = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_NICKNAME))
                memoryAid = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_MEMORY_AID))
                phoneNum = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_PHONE_NUMBER))
                imageLink = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_IMAGE_LINK))
                quizStage = cursor.getString(cursor.getColumnIndex(DBContract.Avatar.COLUMN_QUIZ_STAGE))

                avatars.add(AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
                cursor.moveToNext()
            }
        }
        return avatars
    }

    @Throws(SQLiteConstraintException::class)
    fun modifyAvatar(avatar: AvatarModel): Int{
        var result : Int = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Define 'where' part of query.
            val selection = DBContract.Avatar.COLUMN_AVATAR_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(avatar.avatarId)

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            //entity.put(DBContract.Avatar.COLUMN_AVATAR_ID, avatar.avatarId)
            entity.put(DBContract.Avatar.COLUMN_USER_ID, avatar.userId)
            entity.put(DBContract.Avatar.COLUMN_NICKNAME, avatar.nickName)
            entity.put(DBContract.Avatar.COLUMN_MEMORY_AID, avatar.memoryAid)
            entity.put(DBContract.Avatar.COLUMN_PHONE_NUMBER, avatar.phoneNum)
            entity.put(DBContract.Avatar.COLUMN_IMAGE_LINK, avatar.imageLink)
            entity.put(DBContract.Avatar.COLUMN_QUIZ_STAGE, avatar.quizStage)
            result = db.update(DBContract.Avatar.TABLE_NAME, entity, selection, selectionArgs)
            System.out.println("update result = "+result)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return result
    }

}
