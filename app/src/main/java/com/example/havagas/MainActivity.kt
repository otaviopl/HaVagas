package com.example.havagas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.havagas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Exemplo: quando o usuário digitar nome e email e sair do campo
        binding.emailEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val nome = binding.nomeEt.text.toString()
                val email = binding.emailEt.text.toString()
                if (nome.isNotBlank() && email.isNotBlank()) {
                    Toast.makeText(this, "Nome: $nome\nE-mail: $email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
