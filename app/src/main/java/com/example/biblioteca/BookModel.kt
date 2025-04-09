package com.example.biblioteca

data class BookModel(
    val id: String = "",          // ID único do livro
    val titulo: String = "",      // Título do livro
    val autor: String = "",       // Autor do livro
    val genero: String = "",      // Gênero do livro
    val totalPaginas: Int = 0,    // Total de páginas do livro
    val userId: String = ""       // ID do usuário que adicionou o livro
)