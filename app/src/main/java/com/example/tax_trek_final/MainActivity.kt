package com.example.tax_trek_final

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.content.Intent
import android.net.Uri
import android.view.animation.AnimationUtils
import android.widget.*



//Logic we are using (formula's or the percentage)
//For Individuals below 60 years:
//  Income up to ₹2,50,000: No tax
//  Income from ₹2,50,001 to ₹5,00,000: 5%
//  Income from ₹5,00,001 to ₹10,00,000: 20%
//  Income above ₹10,00,000: 30%
//For Senior Citizens (60 to 80 years):
//  Income up to ₹3,00,000: No tax
//  Income from ₹3,00,001 to ₹5,00,000: 5%
//  Income from ₹5,00,001 to ₹10,00,000: 20%
//  Income above ₹10,00,000: 30%
//For Super Senior Citizens (above 80 years):
//  Income up to ₹5,00,000: No tax
//  Income from ₹5,00,001 to ₹10,00,000: 20%
//  Income above ₹10,00,000: 30%



class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etIncome: EditText
    private lateinit var etDeductions: EditText
    private lateinit var spinnerIncomeType: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var btnReset: Button
    private lateinit var tvResult: TextView
    private lateinit var tvDeductionsInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Using to change status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.st)
        }


        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etIncome = findViewById(R.id.etIncome)
        etDeductions = findViewById(R.id.etDeductions)
        spinnerIncomeType = findViewById(R.id.spinnerIncomeType)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        tvResult = findViewById(R.id.tvResult)
        tvDeductionsInfo = findViewById(R.id.tvDeductionsInfo)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.income_types,// We are storing types in string xml file
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIncomeType.adapter = adapter

        btnCalculate.setOnClickListener { calculateTaxableIncome() }
        btnReset.setOnClickListener { resetFields() }
        tvDeductionsInfo.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.incometaxindia.gov.in/pages/tax-information-services.aspx") //gov site link
            )
            startActivity(browserIntent) //Asking net permission to access the browser
        }
    }

    private fun calculateTaxableIncome() {
        // taking input
        val name = etName.text.toString()
        val ageStr = etAge.text.toString()
        val incomeStr = etIncome.text.toString()
        val deductionsStr = etDeductions.text.toString()
        val incomeType = spinnerIncomeType.selectedItem.toString()

        // checking input values that are all field or not
        if (name.isEmpty() || ageStr.isEmpty() || incomeStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toIntOrNull()
        val income = incomeStr.toDoubleOrNull()
        val deductions = deductionsStr.toDoubleOrNull() ?: 0.0

        if (age == null || income == null) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show() //input should only in intform
            return
        }

        // deductions
        val taxableIncome = income - deductions
        val tax = calculateIncomeTax(taxableIncome, age)

        // result
        val result = "Name: $name\nAge: $age\nIncome Type: $incomeType\nTaxable Income: $taxableIncome\nIncome Tax: $tax"
        tvResult.text = result
    }

    //creating fuction to access the types of functions
    private fun calculateIncomeTax(taxableIncome: Double, age: Int): Double {
        return when {
            age < 60 -> calculateTaxForBelow60(taxableIncome)
            age in 60..79 -> calculateTaxForSeniorCitizens(taxableIncome)
            else -> calculateTaxForSuperSeniorCitizens(taxableIncome)
        }
    }

    //if below 60
    private fun calculateTaxForBelow60(taxableIncome: Double): Double {
        return when {
            taxableIncome <= 250000 -> 0.0
            taxableIncome <= 500000 -> (taxableIncome - 250000) * 0.05
            taxableIncome <= 1000000 -> (250000 * 0.05) + (taxableIncome - 500000) * 0.20
            else -> (250000 * 0.05) + (500000 * 0.20) + (taxableIncome - 1000000) * 0.30
        }
    }

    //for 60--79 age gap(musalollu)
    private fun calculateTaxForSeniorCitizens(taxableIncome: Double): Double {
        return when {
            taxableIncome <= 300000 -> 0.0
            taxableIncome <= 500000 -> (taxableIncome - 300000) * 0.05
            taxableIncome <= 1000000 -> (200000 * 0.05) + (taxableIncome - 500000) * 0.20
            else -> (200000 * 0.05) + (500000 * 0.20) + (taxableIncome - 1000000) * 0.30
        }
    }

    //for above 80(Great Great Musalollu)
    private fun calculateTaxForSuperSeniorCitizens(taxableIncome: Double): Double {
        return when {
            taxableIncome <= 500000 -> 0.0
            taxableIncome <= 1000000 -> (taxableIncome - 500000) * 0.20
            else -> (500000 * 0.20) + (taxableIncome - 1000000) * 0.30
        }
    }

    //Reset Button Kosam
    private fun resetFields() {
        etName.text.clear()
        etAge.text.clear()
        etIncome.text.clear()
        etDeductions.text.clear()
        spinnerIncomeType.setSelection(0)
        tvResult.text = "Result will be displayed here."
    }






}




