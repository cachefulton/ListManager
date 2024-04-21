package com.example.listmanagercf

class BookItem(val id: Int, var title: String, var author: String, var genre: String) {

}


open class ListManager() {
    private val list = mutableListOf<BookItem>()


    fun addItem(obj: BookItem, front: Boolean=false) {
        if (!front) {
            list.add(obj)
        }
        else {
            list.add(0, obj)
        }
    }

    fun getItemAt(ind: Int): BookItem {
        return list[ind]
    }

    fun getItemById(theId: Int): BookItem? {
        for (item in list) {
            if (item.id == theId) return item
        }
        return null
    }

    fun getSize(): Int {
        return list.size
    }

    fun deleteItemById(theId: Int): Int {
        for (i in 0..<list.size) {
            if (list[i].id == theId) {
                list.removeAt(i)
                return i
            }
        }
        return -1
    }

    fun clearAll() {
        list.clear()
    }

    fun updateItemById(theId: Int, newTitle: String, newAuthor: String, newGenre: String): Int {
        for (i in 0..<list.size) {
            if (list[i].id == theId) {
                list[i].genre = newGenre
                list[i].author = newAuthor
                list[i].title = newTitle
                return i
            }
        }
        return -1
    }

    fun printList(): String {
        var str = ""
        for (item in list) {
            str += "${item.title}, ${item.author}, ${item.genre}\n"
        }
        return str
    }
}