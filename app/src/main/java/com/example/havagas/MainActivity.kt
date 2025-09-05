package com.example.havagas

import android.app.AlertDialog
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                updateEducationGroups(parent?.getItemAtPosition(pos)?.toString().orEmpty())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateEducationGroups("")
            }
        }
    }

    private fun updateEducationGroups(level: String) {
        binding.fundamentalMedioGroup.visibility = View.GONE
        binding.graduacaoEspecGroup.visibility = View.GONE
        binding.mestradoDoutoradoGroup.visibility = View.GONE

        when (level) {
            "Fundamental", "Médio" -> binding.fundamentalMedioGroup.visibility = View.VISIBLE
            "Graduação", "Especialização" -> binding.graduacaoEspecGroup.visibility = View.VISIBLE
            "Mestrado", "Doutorado" -> binding.mestradoDoutoradoGroup.visibility = View.VISIBLE
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

            updateEducationGroups("")
            binding.cellphoneContainer.visibility = View.GONE

            Toast.makeText(this, "Formulário limpo.", Toast.LENGTH_SHORT).show()
        }

        binding.saveBtn.setOnClickListener {
            // Validação mínima
            if (binding.nomeEt.text.isNullOrBlank()) {
                binding.nomeEt.error = "Informe o nome"
                return@setOnClickListener
            }
            if (binding.emailEt.text.isNullOrBlank()) {
                binding.emailEt.error = "Informe o e-mail"
                return@setOnClickListener
            }

            // Monta apenas campos preenchidos
            val lines = mutableListOf<String>()

            fun add(label: String, value: String?) {
                val v = value?.trim().orEmpty()
                if (v.isNotEmpty()) lines.add("$label: $v")
            }

            add("Nome", binding.nomeEt.text?.toString())
            add("E-mail", binding.emailEt.text?.toString())
            if (binding.emailUpdatesCb.isChecked) lines.add("Receber e-mails: Sim")

            add("Telefone", binding.phoneEt.text?.toString())
            val phoneType = when (binding.phoneTypeRg.checkedRadioButtonId) {
                binding.phoneTypeCommercialRb.id -> "Comercial"
                binding.phoneTypeResidentialRb.id -> "Residencial"
                else -> ""
            }
            if (phoneType.isNotEmpty()) add("Tipo do telefone", phoneType)

            if (binding.addCellphoneCb.isChecked) {
                add("Celular", binding.cellphoneEt.text?.toString())
            }

            add("Sexo", binding.genderSp.selectedItem?.toString())
            add("Data de nascimento", binding.birthDateEt.text?.toString())

            val education = binding.educationLevelSp.selectedItem?.toString().orEmpty()
            add("Formação", education)

            when (education) {
                "Fundamental", "Médio" -> {
                    add("Ano de formatura", binding.fundamentalMedioYearEt.text?.toString())
                }
                "Graduação", "Especialização" -> {
                    add("Ano de conclusão", binding.graduacaoEspecYearEt.text?.toString())
                    add("Instituição", binding.graduacaoEspecInstitutionEt.text?.toString())
                }
                "Mestrado", "Doutorado" -> {
                    add("Ano de conclusão", binding.mestradoDoutoradoYearEt.text?.toString())
                    add("Instituição", binding.mestradoDoutoradoInstitutionEt.text?.toString())
                    add("Título da monografia", binding.mestradoDoutoradoTitleEt.text?.toString())
                    add("Orientador", binding.mestradoDoutoradoAdvisorEt.text?.toString())
                }
            }

            add("Vagas de interesse", binding.vagasInteresseEt.text?.toString())

            val msg = if (lines.isEmpty()) "Nenhum campo preenchido." else lines.joinToString("\n")

            AlertDialog.Builder(this)
                .setTitle("Resumo do cadastro")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
