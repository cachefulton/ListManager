package com.example.listmanagercf

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private val SQL_CREATE = """
CREATE TABLE "books" (
	"id"	TEXT,
	"title"	TEXT,
	"author" TEXT,
	"genre"	TEXT,
	PRIMARY KEY("id"));
"""
private val SQL_INSERT = """INSERT INTO books (id,title,author,genre) VALUES"""
private val SQL_INSERT_BOOKS = """
   The Life And Opinions of Tristram Shandy,Laurence Sterne,Self-Help
   Stories,Anton Chekhov,Comedy
   Blindness,José Saramago,Horror
   Njál's Saga,Unknown,Fantasy
   The Aeneid,Virgil,Sci Fi
   One Hundred Years of Solitude,Gabriel García Márquez,Fantasy
   Tales,Edgar Allan Poe,Horror
   Gypsy Ballads,Federico García Lorca,Documentary
   Things Fall Apart,Chinua Achebe,Action
   Season of Migration to the North,Tayeb Salih,Documentary
   Gargantua and Pantagruel,François Rabelais,Science Fiction
   Sons and Lovers,D. H. Lawrence,Romance
"""
private val SQL_DELETE_MAIN = "DROP TABLE IF EXISTS books"

class MainDbHelper(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {
    val NUM_OF_MOCK_BOOKS = 12
    companion object {
        var currentId = 1
        const val DATABASE_NAME = "main.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun reset(db: SQLiteDatabase, bookList: ListManager) {
        db.execSQL(SQL_DELETE_MAIN)
        onCreate(db)
        bookList.clearAll()
        currentId = 1
    }

    fun insertRecords(db: SQLiteDatabase, bookList: ListManager) {
        val inserts = SQL_INSERT_BOOKS.trim().lines()
        for (insert in inserts) {
            val addBook = insert.split(",").toTypedArray()
            val title = addBook[0].trim()
            val author = addBook[1].trim()
            val genre = addBook[2].trim()
            val id = insertSingle(db, title, author, genre)
            bookList.addItem(BookItem(id.toInt(), title, author, genre))
        }
    }

    fun insertSingle(db: SQLiteDatabase, title: String, author: String, genre: String): String {
        //returns id of created insertion
        var id = currentId.toString()
        currentId++
        val query = SQL_INSERT + "(" +
                "\"" + id + "\"" + ", " +
                "\"" + title + "\"" + ", " +
                "\"" + author + "\"" + ", " +
                "\"" + genre + "\"" +
                ");"
        db.execSQL(query)
        return id
    }

    fun update(db: SQLiteDatabase, id: String, title: String, author: String, genre: String) {
        val query = "UPDATE books " +
                "SET title = \"" + title + "\", " +
                "author = \"" + author + "\", " +
                "genre = \"" + genre + "\" " +
                "WHERE id " +
                "= \"" + id + "\";"
        db.execSQL(query)
    }

    fun delete(db: SQLiteDatabase, id: String) {
        // Delete the details from the table
        // if already exists
        val query = "DELETE FROM books" +
                " WHERE id" +
                " = \"" + id + "\";"
        db.execSQL(query)
    }

    fun printAuthorQuery(db: SQLiteDatabase, author: String): Cursor {
        val selectionArgs = arrayOf<String>("%$author%")
        val querySQL = "SELECT * FROM books WHERE author LIKE ?;"
        return db.rawQuery(querySQL, selectionArgs)
    }

    fun printAll(db: SQLiteDatabase) {
        var rows =  db.rawQuery("SELECT id, title" + " FROM " +
                "books;", null)
        var allIds = "List of book IDs and Titles:\n"
        if (rows.count == 0) {
            allIds = "No books found in the database.\n"
        }
        while (rows.moveToNext()) {
            allIds += rows.getString(0) + ", " + rows.getString(1) + "\n"
        }
        Log.i("myLog", allIds)
    }
}