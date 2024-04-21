package com.example.listmanagercf

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    lateinit var db: SQLiteDatabase
    lateinit var dbHelper: MainDbHelper
    lateinit var addFab: FloatingActionButton
    lateinit var clearSearchFab: ExtendedFloatingActionButton
    lateinit var btnSearch: Button
    lateinit var searchBar: EditText
    private val bookList = ListManager()
    private val authorList = ListManager()
    private var recyclerView: RecyclerView? = null
    private var bookAdapter: BookAdapter? = null
    private var authorAdapter: BookAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFab = findViewById(R.id.fabPlus)
        clearSearchFab = findViewById(R.id.fabClear)
        clearSearchFab.visibility = View.INVISIBLE

        btnSearch = findViewById(R.id.searchBtn)
        searchBar = findViewById(R.id.searchBar)

        recyclerView = findViewById<View>(R.id.recyclerView)
                    as RecyclerView

        bookAdapter = BookAdapter(this, bookList)
        authorAdapter = BookAdapter(this, authorList)

        val layoutManager = LinearLayoutManager(applicationContext)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        // Add a neat dividing line between items in the list
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL)
        )

        // set the adapter
        recyclerView!!.adapter = bookAdapter


        addFab.setOnClickListener{ addBook() }
        clearSearchFab.setOnClickListener { clearSearch() }
        btnSearch.setOnClickListener { searchAuthors() }

        dbHelper = MainDbHelper(this)
        db = dbHelper.writableDatabase
        dbHelper.reset(db, bookList)
        dbHelper.insertRecords(db, bookList)


    }

    private fun searchAuthors() {
        val text = searchBar.text.toString().trim()
        if (text == "") {
            searchBar.setText("")
            return
        }
        val cursor = dbHelper.printAuthorQuery(db, text)
        while (cursor.moveToNext()) {
            authorList.addItem(
                BookItem(
                    cursor.getString(0).toInt(),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
            )
        }
        authorAdapter!!.notifyDataSetChanged()
        recyclerView!!.swapAdapter(authorAdapter, false)
        clearSearchFab.visibility = View.VISIBLE
    }

    private fun resetSearch() {
        authorList.clearAll()
        searchAuthors()
    }

    private fun clearSearch() {
        searchBar.setText("")
        recyclerView!!.swapAdapter(bookAdapter, true)
        authorList.clearAll()
        clearSearchFab.visibility = View.INVISIBLE
    }

    private fun createNewBook(title: String, author: String, genre: String) {
        val newId = dbHelper.insertSingle(
            db,
            title,
            author,
            genre
        )
        bookList.addItem(BookItem(newId.toInt(), title, author, genre))
        bookAdapter!!.notifyItemInserted(bookList.getSize()-1)
        recyclerView!!.scrollToPosition(bookList.getSize()-1)
        if (recyclerView!!.adapter == authorAdapter) {
            resetSearch()
        }
    }

    private fun changeOldBook(id: Int, title: String, author: String, genre: String) {
        val pos = bookList.updateItemById(id, title, author, genre)
        dbHelper.update(db, id.toString(), title, author, genre)
        bookAdapter!!.notifyItemChanged(pos)
        if (recyclerView!!.adapter == authorAdapter) {
            resetSearch()
        }
        //scroll to see item updated
        recyclerView!!.scrollToPosition(pos)
    }

    private val getResultUpdate = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        val checkDel = it.data?.getIntExtra("delete", -1)
        if(it.resultCode == Activity.RESULT_OK && checkDel == -1) {
            val title = it.data?.getStringExtra("title")
            val author = it.data?.getStringExtra("author")
            val genre = it.data?.getStringExtra("genre")
            val id = it.data?.getIntExtra("id", -1)
            changeOldBook(id!!, title!!, author!!, genre!!)
        }
        else if (checkDel != null) {
            deleteBook(checkDel!!)
        }
    }
    fun updateBook(bookToShow: Int) {
        val currBook =
            if (recyclerView!!.adapter == bookAdapter) bookList.getItemAt(bookToShow)
            else authorList.getItemAt(bookToShow)
        val intent = Intent(this, UpdateBookActivity::class.java)
        intent.putExtra("title", currBook.title)
        intent.putExtra("author", currBook.author)
        intent.putExtra("genre", currBook.genre)
        intent.putExtra("id", currBook.id)
        getResultUpdate.launch(intent)
    }

    // Receiver for adding book
    private val getResultAdd = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val title = it.data?.getStringExtra("title")
                val author = it.data?.getStringExtra("author")
                val genre = it.data?.getStringExtra("genre")
                createNewBook(title!!, author!!, genre!!)
            }
            else if (it.resultCode == 404) {
                dbHelper.reset(db, bookList)
                bookAdapter!!.notifyDataSetChanged()
                if (recyclerView!!.adapter == authorAdapter) {
                    clearSearch()
                }
            }
            else if (it.resultCode == 301) {
                dbHelper.insertRecords(db, bookList)
                bookAdapter!!.notifyItemRangeInserted(bookList.getSize(), dbHelper.NUM_OF_MOCK_BOOKS)
                if (recyclerView!!.adapter == authorAdapter) {
                    resetSearch()
                }
            }
        }
    fun addBook() {
        val intent = Intent(this, NewBookActivity::class.java)
        getResultAdd.launch(intent)
    }

    fun deleteBook(id: Int) {
        val pos = bookList.deleteItemById(id)
        dbHelper.delete(db, id.toString())
        bookAdapter!!.notifyItemRemoved(pos)
        if (recyclerView!!.adapter == authorAdapter) {
            resetSearch()
        }
    }
}