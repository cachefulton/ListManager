package com.example.listmanagercf

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class NewBookActivity : AppCompatActivity() {
    lateinit var title: EditText
    lateinit var author: EditText
    lateinit var genre: Spinner
    lateinit var btnOk: Button
    lateinit var btnCancel: Button
    lateinit var btnPopulate: Button
    lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        btnOk = findViewById(R.id.btnOk)
        btnCancel = findViewById(R.id.btnCancel)
        btnClear = findViewById(R.id.btnClear)
        btnPopulate = findViewById(R.id.btnPopulate)

        title = findViewById(R.id.editTitle)
        author = findViewById(R.id.editAuthor)
        genre = findViewById(R.id.spinnerGenre)

        btnOk.setOnClickListener{doAdd()}
        btnCancel.setOnClickListener{doCancel()}
        btnPopulate.setOnClickListener{doPopulate()}
        btnClear.setOnClickListener{doClear()}


        ArrayAdapter.createFromResource(
            this,
            R.array.genres,
            android.R.layout.simple_spinner_item
        ).also { adap ->
            // Specify the layout to use when the list of choices appears
            adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            genre.adapter = adap
        }
    }

    fun doAdd() {
        val intent = Intent()
        val strTitle = title.text.toString().trim()
        val strAuthor = author.text.toString().trim()
        val strGenre = genre.selectedItem.toString()
        intent.putExtra("title", strTitle)
        intent.putExtra("author", strAuthor)
        intent.putExtra("genre", strGenre)
        setResult(RESULT_OK, intent)
        if (strTitle == "" || strAuthor == "") setResult(RESULT_CANCELED)
        finish()
    }

    fun doCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    fun doPopulate() {
        setResult(301)
        finish()
    }

    fun doClear() {
        setResult(404)
        finish()
    }
}