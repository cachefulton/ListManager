package com.example.listmanagercf

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val mainActivity: MainActivity,
    private val bookList: ListManager)
    : RecyclerView.Adapter<BookAdapter.ListItemHolder>() {

    inner class ListItemHolder(view: View) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        internal var title =
            view.findViewById<View>(
                R.id.textViewTitle) as TextView

        internal var author =
            view.findViewById<View>(
                R.id.textViewAuthor) as TextView

        init {

            view.isClickable = true
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mainActivity.updateBook(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)

        return ListItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val book = bookList.getItemAt(position)
        holder.title.text = book.title
        holder.author.text = book.author
    }

    override fun getItemCount(): Int {
        if (bookList != null) {
            return bookList.getSize()
        }
        // error
        return -1
    }
}