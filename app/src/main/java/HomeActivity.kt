package com.example.biblioteca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.biblioteca.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar o BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_books -> {
                    loadFragment(BookListFragment(), "Meus Livros")
                    true
                }
                R.id.nav_stats -> {
                    loadFragment(StatsFragment(), "Estatísticas")
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment(), "Perfil")
                    true
                }
                else -> false
            }
        }

        // Carregar o fragmento inicial (Lista de Livros)
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_books
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        // Alterar o título da ActionBar
        supportActionBar?.title = title

        // Substituir o fragmento no container
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .setReorderingAllowed(true) // Melhorar o desempenho
            .commit()
    }

    override fun onBackPressed() {
        // Verificar se o fragmento atual é o inicial
        if (binding.bottomNavigation.selectedItemId != R.id.nav_books) {
            binding.bottomNavigation.selectedItemId = R.id.nav_books
        } else {
            super.onBackPressed() // Sair do aplicativo
        }
    }
}