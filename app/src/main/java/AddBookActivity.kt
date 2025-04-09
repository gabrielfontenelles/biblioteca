package com.example.biblioteca

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Verificar se é modo de edição
        bookId = intent.getStringExtra("bookId")
        if (bookId != null) {
            // Modo de edição
            binding.tvTituloAddLivro.text = "Editar Livro"
            binding.btnSalvarLivro.text = "Atualizar"

            // Preencher os campos com os dados do livro
            binding.etTituloLivro.setText(intent.getStringExtra("titulo"))
            binding.etAutorLivro.setText(intent.getStringExtra("autor"))
            binding.etGeneroLivro.setText(intent.getStringExtra("genero"))
            binding.etPaginasLivro.setText(intent.getIntExtra("totalPaginas", 0).toString())
        }

        binding.btnSalvarLivro.setOnClickListener {
            val titulo = binding.etTituloLivro.text.toString()
            val autor = binding.etAutorLivro.text.toString()
            val genero = binding.etGeneroLivro.text.toString()
            val totalPaginas = binding.etPaginasLivro.text.toString().toIntOrNull() ?: 0

            if (titulo.isNotEmpty() && autor.isNotEmpty() && genero.isNotEmpty() && totalPaginas > 0) {
                val userId = auth.currentUser?.uid
                val book = BookModel(
                    id = bookId ?: firestore.collection("books").document().id,
                    titulo = titulo,
                    autor = autor,
                    genero = genero,
                    totalPaginas = totalPaginas,
                    userId = userId ?: ""
                )

                val documentRef = firestore.collection("books").document(book.id)

                if (bookId != null) {
                    // Atualizar livro existente
                    documentRef.update(
                        mapOf(
                            "titulo" to titulo,
                            "autor" to autor,
                            "genero" to genero,
                            "totalPaginas" to totalPaginas
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Erro ao atualizar livro", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Adicionar novo livro
                    documentRef.set(book)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao salvar livro", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}