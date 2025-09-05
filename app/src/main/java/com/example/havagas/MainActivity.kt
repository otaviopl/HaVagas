package com.example.havagas

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.havagas.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import android.app.DatePickerDialog

class MainActivity : ComponentActivity() {
    private val EMAIL_REGEX =
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".toRegex(RegexOption.IGNORE_CASE)

        // Somente dígitos
        //  - Celular: [55] + [DD] + 9 + 8 dígitos  (ex.: +55 11 9 1234 5678 -> 13 dígitos)
        //  - Fixo:    [55] + [DD] + 8 dígitos      (ex.: +55 11 1234 5678  -> 12 dígitos)
        // Sem DDI/DDD também funciona (8 ou 9 dígitos).

    private val BR_MOBILE_DIGITS = "^(?:55)?(?:\\d{2})?9\\d{8}$".toRegex()
    private val BR_LANDLINE_DIGITS = "^(?:55)?(?:\\d{2})?[2-5]\\d{7}$".toRegex()

    private fun isValidEmail(s: String): Boolean =
        EMAIL_REGEX.matches(s.trim())

    private fun isValidBrPhone(s: String): Boolean {
        val d = s.replace("\\D".toRegex(), "") // remove tudo que não é dígito
        return BR_MOBILE_DIGITS.matches(d) || BR_LANDLINE_DIGITS.matches(d)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdowns()
        setupBirthDatePicker()
        setupConditionalSections()
        setupButtons()
    }

    private fun setupDropdowns() {
        // Sexo
        val genderItems = listOf("Masculino", "Feminino", "Prefiro não informar")
        (binding.genderSp as AutoCompleteTextView).setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, genderItems)
        )

        // Formação
        val educationItems = listOf(
            "Fundamental", "Médio",
            "Graduação", "Especialização",
            "Mestrado", "Doutorado"
        )
        val eduView = (binding.educationLevelSp as AutoCompleteTextView)
        eduView.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, educationItems))
        eduView.setOnItemClickListener { parent, _, position, _ ->
            updateEducationGroups(parent.getItemAtPosition(position).toString())
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
        binding.birthDateEt.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val c = Calendar.getInstance()
                val y = c.get(Calendar.YEAR)
                val m = c.get(Calendar.MONTH)
                val d = c.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(
                    this@MainActivity,
                    { _, year, month, dayOfMonth ->
                        val dd = dayOfMonth.toString().padStart(2, '0')
                        val mm = (month + 1).toString().padStart(2, '0')
                        setText("$dd/$mm/$year")
                    },
                    y, m, d
                ).show()
            }
        }
    }

    private fun setupConditionalSections() {
        binding.addCellphoneCb.setOnCheckedChangeListener { _, checked ->
            binding.cellphoneContainer.visibility = if (checked) View.VISIBLE else View.GONE
        }
    }

    private fun setupButtons() {
        // Limpar formulário
        binding.clearBtn.setOnClickListener {
            binding.nomeEt.text?.clear()
            binding.emailEt.text?.clear()
            binding.emailUpdatesCb.isChecked = false

            binding.phoneEt.text?.clear()
            binding.phoneTypeGroup.clearChecked()

            binding.addCellphoneCb.isChecked = false
            binding.cellphoneEt.text?.clear()
            binding.cellphoneContainer.visibility = View.GONE

            (binding.genderSp as AutoCompleteTextView).setText("", false)
            binding.birthDateEt.text?.clear()

            (binding.educationLevelSp as AutoCompleteTextView).setText("", false)
            binding.fundamentalMedioYearEt.text?.clear()
            binding.graduacaoEspecYearEt.text?.clear()
            binding.graduacaoEspecInstitutionEt.text?.clear()
            binding.mestradoDoutoradoYearEt.text?.clear()
            binding.mestradoDoutoradoInstitutionEt.text?.clear()
            binding.mestradoDoutoradoTitleEt.text?.clear()
            binding.mestradoDoutoradoAdvisorEt.text?.clear()

            binding.vagasInteresseEt.text?.clear()

            updateEducationGroups("")
            Toast.makeText(this, "Formulário limpo.", Toast.LENGTH_SHORT).show()
        }

        // Salvar: mostra somente campos preenchidos
        binding.saveBtn.setOnClickListener {
            // Validação mínima
            if (binding.nomeEt.text.isNullOrBlank()) {
                binding.nomeEt.error = "Informe o nome"; return@setOnClickListener
            }
            if (binding.emailEt.text.isNullOrBlank()) {
                binding.emailEt.error = "Informe o e-mail"; return@setOnClickListener
            }
            val email = binding.emailEt.text!!.toString()

            if (!isValidEmail(email)) {
                binding.emailEt.error = "E-mail inválido"
                return@setOnClickListener
            }
            val phone = binding.phoneEt.text?.toString().orEmpty()

            if (phone.isNotBlank() && !isValidBrPhone(phone)) {
                binding.phoneEt.error = "Telefone inválido"
                return@setOnClickListener
            }

            // 3) Celular (obrigatório se a chavinha 'Adicionar celular' estiver marcada)
            if (binding.addCellphoneCb.isChecked) {
                val cell = binding.cellphoneEt.text?.toString().orEmpty()
                if (cell.isBlank() || !isValidBrPhone(cell)) {
                    binding.cellphoneEt.error = "Celular inválido"
                    return@setOnClickListener
                }
            }

            val out = mutableListOf<String>()
            fun add(label: String, value: String?) {
                val v = value?.trim().orEmpty()
                if (v.isNotEmpty()) out += "$label: $v"
            }

            add("Nome", binding.nomeEt.text?.toString())
            add("E-mail", binding.emailEt.text?.toString())
            if (binding.emailUpdatesCb.isChecked) out += "Receber e-mails: Sim"

            add("Telefone", binding.phoneEt.text?.toString())
            val phoneType = when (binding.phoneTypeGroup.checkedButtonId) {
                binding.phoneTypeCommercialRb.id -> "Comercial"
                binding.phoneTypeResidentialRb.id -> "Residencial"
                else -> ""
            }
            if (phoneType.isNotEmpty()) add("Tipo do telefone", phoneType)

            if (binding.addCellphoneCb.isChecked) {
                add("Celular", binding.cellphoneEt.text?.toString())
            }

            add("Sexo", (binding.genderSp as AutoCompleteTextView).text?.toString())
            add("Data de nascimento", binding.birthDateEt.text?.toString())

            val education =
                (binding.educationLevelSp as AutoCompleteTextView).text?.toString().orEmpty()
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

            MaterialAlertDialogBuilder(this)
                .setTitle("Resumo do cadastro")
                .setMessage(if (out.isEmpty()) "Nenhum campo preenchido." else out.joinToString("\n"))
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
