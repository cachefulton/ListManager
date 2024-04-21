package com.example.listmanagercf

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner


class UpdateBookActivity : AppCompatActivity() {
    lateinit var title: EditText
    lateinit var author: EditText
    lateinit var genre: Spinner
    lateinit var btnDelete: Button
    lateinit var btnUpdate: Button
    lateinit var btnCancel: Button
    lateinit var extras: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_book)

        btnDelete = findViewById(R.id.btnDelete)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)

        title = findViewById(R.id.editTitle)
        author = findViewById(R.id.editAuthor)
        genre = findViewById(R.id.spinnerGenre)

        extras = intent.extras!!
        title.text = Editable.Factory.getInstance().newEditable(extras.getString("title"))
        author.text = Editable.Factory.getInstance().newEditable(extras.getString("author"))

        btnUpdate.setOnClickListener{doUpdate()}
        btnCancel.setOnClickListener{doCancel()}
        btnDelete.setOnClickListener{doDelete()}

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
        val genreArray = resources.getStringArray(R.array.genres)
        var ind = -1
        for (checkGenre in genreArray) {
            ind++
            if (extras.getString("genre") == checkGenre) {
                break
            }
        }
        genre.setSelection(ind)
    }

    fun doDelete() {
        val intent = Intent()
        intent.putExtra("delete", extras.getInt("id"))
        setResult(RESULT_OK, intent)
        finish()
    }

    fun doUpdate() {
        //TODO change to update
        val intent = Intent()
        val strTitle = title.text.toString().trim()
        val strAuthor = author.text.toString().trim()
        val strGenre = genre.selectedItem.toString()
        intent.putExtra("title", strTitle)
        intent.putExtra("author", strAuthor)
        intent.putExtra("genre", strGenre)
        intent.putExtra("id", extras.getInt("id"))
        setResult(RESULT_OK, intent)
        if (strTitle == "" || strAuthor == "") setResult(RESULT_CANCELED)
        finish()
    }

    fun doCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }
}