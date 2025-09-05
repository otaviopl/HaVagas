package com.example.havagas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.havagas.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
