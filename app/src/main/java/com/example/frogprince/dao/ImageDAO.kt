package com.example.frogprince.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.frogprince.contract.DBContract
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.MainQuestionModel
import com.example.frogprince.db.model.SubQuestionModel
import com.example.frogprince.db.model.UserModel
import java.lang.Exception
import java.sql.Blob
import java.util.ArrayList

// ** Acknowledgement and Declaration of Code Use **
// This code is based on the professor's demo on SQLLite (SQL Demo), and I have
// modified it and added some features and functions.
class ImageDAO(context : Context) : SQLiteOpenHelper(context,
    ImageDAO.DATABASE_NAME, null,
    ImageDAO.DATABASE_VERSION
){

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FrogPrince.db"

        private val SQL_CREATE_IMAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+DBContract.Image.TABLE_NAME+" (" +
                    DBContract.Image.COLUMN_IMAGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.Image.COLUMN_IMAGE+" BLOB)"

        val SQL_DELETE_IMAGE_TABLE = "DROP TABLE IF EXISTS IMAGE_TABLE"
    }

    fun deleteDB(db: SQLiteDatabase){
        db.execSQL(ImageDAO.SQL_DELETE_IMAGE_TABLE)
    }

    fun createDB(db : SQLiteDatabase){
        db.execSQL(ImageDAO.SQL_CREATE_IMAGE_TABLE)
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
    fun insertImage(byteArray: ByteArray): Long{
        var result : Boolean = true
        var newRowId : Long = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.Image.COLUMN_IMAGE, byteArray)

            newRowId = db.insert(DBContract.Image.TABLE_NAME, null, entity)
            System.out.println("newRowId = "+newRowId)
            db.close()

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return newRowId
    }

    fun getImage(id : String): Bitmap? {
        val db = writableDatabase
        val cursor: Cursor =
            db.rawQuery(" SELECT * FROM "+DBContract.Image.TABLE_NAME
                        +" WHERE "+DBContract.Image.COLUMN_IMAGE_ID+" ='" + id + "'",
                null, null) as Cursor
        if (cursor.moveToFirst()) {
            val imgByte: ByteArray = cursor.getBlob(cursor.getColumnIndex(DBContract.Image.COLUMN_IMAGE))
            cursor.close()
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close()
        }
        return null
    }

    @Throws(SQLiteConstraintException::class)
    fun modifyImage(id: String, byteArray: ByteArray): Int{
        var result : Int = -1
        try{
            val db = writableDatabase
            var cursor : Cursor? = null

            // Define 'where' part of query.
            val selection = DBContract.Image.COLUMN_IMAGE_ID+" = ?"

            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(id)

            // Create a new map of ContentValues (column names are the keys).
            val entity = ContentValues()
            entity.put(DBContract.Image.COLUMN_IMAGE, byteArray)
            result = db.update(DBContract.Image.TABLE_NAME, entity, selection, selectionArgs)
            System.out.println("update result = "+result)

        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return result
    }

}