package com.example.havagas

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.havagas.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupBirthDatePicker()
        setupConditionalSections()
        setupButtons()
    }

    private fun setupSpinners() {
        // Sexo
        val genderItems = listOf("Masculino", "Feminino", "Prefiro não informar")
        binding.genderSp.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderItems
        )

        // Formação
        val educationItems = listOf(
            "Fundamental", "Médio",
            "Graduação", "Especialização",
            "Mestrado", "Doutorado"
        )
        binding.educationLevelSp.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            educationItems
        )

        binding.educationLevelSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position)?.toString().orEmpty()
                updateEducationGroups(selected)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateEducationGroups("") // esconde tudo
            }
        }
    }

    private fun updateEducationGroups(level: String) {
        // Esconde todos
        binding.fundamentalMedioGroup.visibility = View.GONE
        binding.graduacaoEspecGroup.visibility = View.GONE
        binding.mestradoDoutoradoGroup.visibility = View.GONE

        when (level) {
            "Fundamental", "Médio" -> {
                binding.fundamentalMedioGroup.visibility = View.VISIBLE
            }
            "Graduação", "Especialização" -> {
                binding.graduacaoEspecGroup.visibility = View.VISIBLE
            }
            "Mestrado", "Doutorado" -> {
                binding.mestradoDoutoradoGroup.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBirthDatePicker() {
        binding.birthDateEt.setOnClickListener {
            val c = Calendar.getInstance()
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dd = dayOfMonth.toString().padStart(2, '0')
                    val mm = (month + 1).toString().padStart(2, '0')
                    binding.birthDateEt.setText("$dd/$mm/$year")
                },
                y, m, d
            ).show()
        }
    }

    private fun setupConditionalSections() {
        // Mostrar/ocultar celular
        binding.addCellphoneCb.setOnCheckedChangeListener { _, checked ->
            binding.cellphoneContainer.visibility = if (checked) View.VISIBLE else View.GONE
        }
    }

    private fun setupButtons() {
        binding.clearBtn.setOnClickListener {
            binding.nomeEt.text?.clear()
            binding.emailEt.text?.clear()
            binding.emailUpdatesCb.isChecked = false
            binding.phoneEt.text?.clear()
            binding.phoneTypeRg.clearCheck()
            binding.addCellphoneCb.isChecked = false
            binding.cellphoneEt.text?.clear()
            binding.genderSp.setSelection(0)
            binding.birthDateEt.text?.clear()
            binding.educationLevelSp.setSelection(0)
            binding.fundamentalMedioYearEt.text?.clear()
            binding.graduacaoEspecYearEt.text?.clear()
            binding.graduacaoEspecInstitutionEt.text?.clear()
            binding.mestradoDoutoradoYearEt.text?.clear()
            binding.mestradoDoutoradoInstitutionEt.text?.clear()
            binding.mestradoDoutoradoTitleEt.text?.clear()
            binding.mestradoDoutoradoAdvisorEt.text?.clear()
            binding.vagasInteresseEt.text?.clear()

            // Esconde grupos condicionais
            updateEducationGroups("")
            binding.cellphoneContainer.visibility = View.GONE

            Toast.makeText(this, "Formulário limpo.", Toast.LENGTH_SHORT).show()
        }

        binding.saveBtn.setOnClickListener {
            val nome = binding.nomeEt.text?.toString().orEmpty()
            val email = binding.emailEt.text?.toString().orEmpty()
            val recebeEmails = binding.emailUpdatesCb.isChecked
            val phone = binding.phoneEt.text?.toString().orEmpty()
            val phoneType = when (binding.phoneTypeRg.checkedRadioButtonId) {
                binding.phoneTypeCommercialRb.id -> "Comercial"
                binding.phoneTypeResidentialRb.id -> "Residencial"
                else -> ""
            }
            val hasCell = binding.addCellphoneCb.isChecked
            val cellphone = binding.cellphoneEt.text?.toString().orEmpty()
            val gender = binding.genderSp.selectedItem?.toString().orEmpty()
            val birth = binding.birthDateEt.text?.toString().orEmpty()
            val education = binding.educationLevelSp.selectedItem?.toString().orEmpty()
            val vagas = binding.vagasInteresseEt.text?.toString().orEmpty()

            // Extras por nível
            val eduExtra = when (education) {
                "Fundamental", "Médio" ->
                    "Ano formatura: ${binding.fundamentalMedioYearEt.text}"
                "Graduação", "Especialização" ->
                    "Ano conclusão: ${binding.graduacaoEspecYearEt.text}, Instituição: ${binding.graduacaoEspecInstitutionEt.text}"
                "Mestrado", "Doutorado" ->
                    "Ano: ${binding.mestradoDoutoradoYearEt.text}, Inst.: ${binding.mestradoDoutoradoInstitutionEt.text}, " +
                            "Título: ${binding.mestradoDoutoradoTitleEt.text}, Orient.: ${binding.mestradoDoutoradoAdvisorEt.text}"
                else -> ""
            }

            // Validação mínima
            if (nome.isBlank()) {
                binding.nomeEt.error = "Informe o nome"
                return@setOnClickListener
            }
            if (email.isBlank()) {
                binding.emailEt.error = "Informe o e-mail"
                return@setOnClickListener
            }

            // Aqui você pode enviar para API/banco
            val resumo = buildString {
                appendLine("Salvo!")
                appendLine("Nome: $nome")
                appendLine("E-mail: $email (receber updates: $recebeEmails)")
                appendLine("Telefone: $phone ${if (phoneType.isNotBlank()) "($phoneType)" else ""}")
                if (hasCell) appendLine("Celular: $cellphone")
                appendLine("Sexo: $gender")
                appendLine("Nascimento: $birth")
                appendLine("Formação: $education")
                if (eduExtra.isNotBlank()) appendLine(eduExtra)
                appendLine("Vagas: $vagas")
            }
            Toast.makeText(this, resumo, Toast.LENGTH_LONG).show()
        }
    }
}
