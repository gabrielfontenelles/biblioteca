package com.example.biblioteca

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid

        // Carregar dados do perfil
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.etNome.setText(document.getString("nome"))
                        binding.etEmail.setText(document.getString("email"))
                        binding.etTelefone.setText(document.getString("telefone"))
                        binding.etEndereco.setText(document.getString("endereco"))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
        }

        // Botão de Salvar
        binding.btnSalvar.setOnClickListener {
            val nome = binding.etNome.text.toString()
            val email = binding.etEmail.text.toString()
            val telefone = binding.etTelefone.text.toString()
            val endereco = binding.etEndereco.text.toString()

            if (userId != null) {
                val user = hashMapOf(
                    "nome" to nome,
                    "email" to email,
                    "telefone" to telefone,
                    "endereco" to endereco
                )

                firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navegar para a HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Finaliza a ProfileActivity
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao salvar perfil", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}