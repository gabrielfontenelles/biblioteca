package com.example.biblioteca

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca.databinding.FragmentBookListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookListFragment : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadBooks()

        // Configurar FAB para adicionar novo livro
        binding.fabAddBook.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = BookAdapter(
            onEditClick = { book -> editBook(book) },
            onDeleteClick = { book -> deleteBook(book) }
        )
        binding.rvLivros.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLivros.adapter = adapter
    }

    private fun editBook(book: BookModel) {
        val intent = Intent(requireContext(), AddBookActivity::class.java).apply {
            putExtra("bookId", book.id)
            putExtra("titulo", book.titulo)
            putExtra("autor", book.autor)
            putExtra("genero", book.genero)
            putExtra("totalPaginas", book.totalPaginas)
        }
        startActivity(intent)
    }

    private fun deleteBook(book: BookModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Livro")
            .setMessage("Tem certeza que deseja excluir este livro?")
            .setPositiveButton("Sim") { _, _ ->
                firestore.collection("books").document(book.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Livro excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        loadBooks() // Recarregar a lista
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao excluir livro", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun loadBooks() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("books")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val books = snapshot.toObjects(BookModel::class.java)
                    adapter.submitList(books)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao carregar livros", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        loadBooks() // Recarregar livros quando voltar para o fragmento
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}