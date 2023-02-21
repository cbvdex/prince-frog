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
import com.example.frogprince.db.model.UserModel
import java.lang.Exception
import java.math.BigInteger
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class UserDAO(context : Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
){
    init{
        //test only
        //context.deleteDatabase(DATABASE_NAME);
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBContract.User.TABLE_NAME + " (" +
                    DBContract.User.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.User.COLUMN_USER_NAME + " TEXT," +
                    DBContract.User.COLUMN_EMAIL + " TEXT," +
                    DBContract.User.COLUMN_PASSCODE + " TEXT)"

        val SQL_DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + DBContract.User.TABLE_NAME

        val SQL_LASTEST_CREATED_USER_ID = "SELECT LAST_INSERT_ROWID() LAST_INSERT_ROWID"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(SQL_DELETE_USER_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(SQL_CREATE_USER_TABLE)
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

    fun eraseDB(): Boolean {
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
    fun insertUser(user: UserModel): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null
            var userId : String? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.User.COLUMN_USER_NAME, user.userName)
            entity.put(DBContract.User.COLUMN_EMAIL, user.email)
            entity.put(DBContract.User.COLUMN_PASSCODE, user.passCode)
            newRowId = db.insert(DBContract.User.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        var result : Boolean = true
        try{
            val db = writableDatabase // Gets the data repository in write mode

            // Define 'where' part of query.
            val selection = DBContract.User.COLUMN_USER_ID + " = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(userid)

            // Issue SQL statement.
            db.delete(DBContract.User.TABLE_NAME, selection, selectionArgs)

        }catch(e : Exception){
            e.printStackTrace()
            result = false
        }
        // return whether the result was successful
        return result
    }

    @Throws(SQLiteConstraintException::class)
    fun updateUser(user : UserModel): Int{
        var result : Int = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.User.COLUMN_USER_ID, user.userId)
            entity.put(DBContract.User.COLUMN_USER_NAME, user.userName)
            entity.put(DBContract.User.COLUMN_EMAIL, user.email)
            entity.put(DBContract.User.COLUMN_PASSCODE, user.passCode)

            result = db.update(DBContract.User.TABLE_NAME, entity, null, null)
            System.out.println("update result = "+result)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return result
    }

    fun findUsers(userid: String): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.User.TABLE_NAME
                    + " WHERE " + DBContract.User.COLUMN_USER_ID + "='" + userid
                    + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            createDB(db)
            return users
        }

        var name: String
        var email: String
        var passcode : String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                name = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_NAME))
                email = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_EMAIL))
                passcode = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_PASSCODE))

                users.add(UserModel(userid, name, email, passcode))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun listAllUsers(): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.User.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return users
        }

        var userid: String
        var name: String
        var email: String
        var passcode : String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userid = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_NAME))
                email = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_EMAIL))
                passcode = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_PASSCODE))

                users.add(UserModel(userid, name, email, passcode))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun getLatestUser(): UserModel? {
        var user : UserModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.User.TABLE_NAME + " order by ROWID desc", null)
        } catch (e: SQLiteException) {
            createDB(db)
            return user
        }

        var userid: String
        var name: String
        var email: String
        var passcode : String
        if (cursor!!.moveToFirst()) {
                userid = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_NAME))
                email = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_EMAIL))
                passcode = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_PASSCODE))

                user = UserModel(userid, name, email, passcode)
        }
        return user
    }

    fun getUserByRowId(rowId : String): UserModel? {
        var user : UserModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.User.TABLE_NAME + " where ROWID = "+rowId, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return user
        }

        var userid: String
        var name: String
        var email: String
        var passcode : String
        if (cursor!!.moveToFirst()) {
            userid = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_ID))
            name = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_NAME))
            email = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_EMAIL))
            passcode = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_PASSCODE))

            user = UserModel(userid, name, email, passcode)
        }
        return user
    }

    fun getUserByUserId(userId : String): UserModel? {
        var user : UserModel? = null
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.User.TABLE_NAME + " where userId = "+userId, null)
        } catch (e: SQLiteException) {
            createDB(db)
            return user
        }

        var userid: String
        var name: String
        var email: String
        var passcode : String
        if (cursor!!.moveToFirst()) {
            userid = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_ID))
            name = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_USER_NAME))
            email = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_EMAIL))
            passcode = cursor.getString(cursor.getColumnIndex(DBContract.User.COLUMN_PASSCODE))

            user = UserModel(userid, name, email, passcode)
        }
        return user
    }
}