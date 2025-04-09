package com.example.biblioteca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.databinding.ItemBookBinding

class BookAdapter(
    private val onEditClick: (BookModel) -> Unit,
    private val onDeleteClick: (BookModel) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val books = mutableListOf<BookModel>()

    fun submitList(newBooks: List<BookModel>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookModel) {
            binding.apply {
                tvTituloLivro.text = book.titulo
                tvAutorLivro.text = book.autor
                tvGeneroLivro.text = book.genero
                tvPaginasLivro.text = "${book.totalPaginas} páginas"

                // Configurar o botão de menu
                btnMenuLivro.setOnClickListener { view ->
                    showPopupMenu(view, book)
                }
            }
        }

        private fun showPopupMenu(view: View, book: BookModel) {
            PopupMenu(view.context, view).apply {
                // Inflar o menu
                menuInflater.inflate(R.menu.menu_book_item, menu)

                // Configurar o click listener
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClick(book)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(book)
                            true
                        }
                        else -> false
                    }
                }
                // Mostrar o menu
                show()
            }
        }
    }
}