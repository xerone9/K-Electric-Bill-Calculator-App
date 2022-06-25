package com.example.k_electric

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.annotation.RequiresApi
import android.os.Build
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import java.io.*
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && v is EditText
            && !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.getLeft() - scrcoords[0]
            val y = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()
            ) hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null
        ) {
            val imm = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                activity.window.decorView
                    .windowToken, 0
            )
        }
    }


    internal var x1: Float = 0.toFloat()
    internal var x2: Float = 0.toFloat()
    internal var y1: Float = 0.toFloat()
    internal var y2: Float = 0.toFloat()





        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)




        val sharedPreferences = getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)

        val save = findViewById(R.id.save) as Button
        val meterReading = findViewById(R.id.meterReading) as TextView
        val editMeterReading = findViewById(R.id.editMeterReading) as EditText
        val date = findViewById(R.id.date) as EditText
        val meterName = findViewById(R.id.meterName) as EditText
        val textView = findViewById(R.id.textView) as TextView
        val textView2 = findViewById(R.id.textView2) as TextView
        val textHeading = findViewById(R.id.textHeading) as TextView

        val editCurrentReading = findViewById(R.id.editCurrentReading) as EditText
        val amount = findViewById(R.id.amount) as TextView
        val calculate = findViewById(R.id.calculate) as Button
        val dueDate = findViewById(R.id.dueDate) as TextView
        val textHeading1 = findViewById(R.id.textHeading1) as TextView
        val textHeading2 = findViewById(R.id.textHeading2) as TextView
        val textHeading3 = findViewById(R.id.textHeading3) as TextView
        val textHeading4 = findViewById(R.id.textHeading4) as TextView
        val textHeading5 = findViewById(R.id.textHeading5) as TextView
        val unit = findViewById(R.id.units) as TextView
        val slab = findViewById(R.id.slab) as TextView
        val fuelCharges = findViewById(R.id.fuelCharges) as TextView
        val nextSlab = findViewById(R.id.nextSlab) as TextView
        val dayCount = findViewById(R.id.dayCount) as TextView


        val reset = findViewById(R.id.reset) as Button

            val sdf         =   SimpleDateFormat("dd/MM/yyyy")
            val currentDate =   sdf.format(Date())
            textView2.setText(currentDate)
            editCurrentReading.setText("")

        val sharedMeterReading = sharedPreferences.getInt("sharedMeterReading", 0)
        meterReading.text = "$sharedMeterReading"
        val sharedDate = sharedPreferences.getString("sharedDate", "")
        textView.text = "$sharedDate"
        val sharedMeterName = sharedPreferences.getString("sharedMeterName", "")
        textHeading3.text = "$sharedMeterName"
        val sharedCurrentReading = sharedPreferences.getString("sharedCurrentReading", "")
        //editCurrentReading.text = "$sharedCurrentReading"
        editCurrentReading.setText(sharedCurrentReading)

            @RequiresApi(Build.VERSION_CODES.N)
            fun EditText.transformIntoDatePicker(
                context: Context,
                format: String,
                maxDate: Date? = null
            ) {
                isFocusableInTouchMode = true
                isClickable = true
                isFocusable = true

                val myCalendar = Calendar.getInstance()
                val datePickerOnDataSetListener =
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val sdf = SimpleDateFormat(format, Locale.UK)
                        setText(sdf.format(myCalendar.time))
                    }

                setOnClickListener {
                    DatePickerDialog(
                        context, datePickerOnDataSetListener, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                    ).run {
                        maxDate?.time?.also { datePicker.maxDate = it }
                        show()
                    }
                }
            }

            date.setOnClickListener {
                date.transformIntoDatePicker(this, "dd/MM/yyyy")
                date.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
            }




        val mamba = meterReading.text.toString().toInt()

        if (mamba == 0) {
            calculate.setEnabled(false)
            reset.setEnabled(false)
            save.setEnabled(true)

            meterReading.setVisibility(View.INVISIBLE)
            editCurrentReading.setVisibility(View.INVISIBLE)
            amount.setVisibility(View.INVISIBLE)
            calculate.setVisibility(View.INVISIBLE)
            reset.setVisibility(View.INVISIBLE)
            dueDate.setVisibility(View.INVISIBLE)
            textHeading2.setVisibility(View.INVISIBLE)
            textHeading3.setVisibility(View.INVISIBLE)
            textHeading4.setVisibility(View.INVISIBLE)

            textView.setVisibility(View.INVISIBLE)
            textView2.setVisibility(View.INVISIBLE)


            textHeading1.setVisibility(View.VISIBLE)
            save.setVisibility(View.VISIBLE)
            editMeterReading.setVisibility(View.VISIBLE)
            textHeading5.setVisibility(View.VISIBLE)
            date.setVisibility(View.VISIBLE)
            meterName.setVisibility(View.VISIBLE)
            textHeading.setVisibility(View.VISIBLE)


        }
        else
        {
            calculate.setEnabled(true);
            save.setEnabled(false)
            reset.setEnabled(true)

            meterReading.setVisibility(View.VISIBLE)
            editCurrentReading.setVisibility(View.VISIBLE)
            amount.setVisibility(View.VISIBLE)
            calculate.setVisibility(View.VISIBLE)
            dueDate.setVisibility(View.VISIBLE)
            textHeading2.setVisibility(View.VISIBLE)
            textHeading3.setVisibility(View.VISIBLE)
            textHeading4.setVisibility(View.VISIBLE)

            textHeading1.setVisibility(View.INVISIBLE)
            save.setVisibility(View.INVISIBLE)
            editMeterReading.setVisibility(View.INVISIBLE)
            textHeading5.setVisibility(View.INVISIBLE)
            date.setVisibility(View.INVISIBLE)

            textView.setVisibility(View.INVISIBLE)
            textView2.setVisibility(View.INVISIBLE)

            meterName.setVisibility(View.INVISIBLE)
            textHeading.setVisibility(View.INVISIBLE)


        }


        editMeterReading.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    save.performClick()


                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }


        save.setOnClickListener {

            if (editMeterReading.text.trim().length == 0) {
                textHeading1.setTextColor(getResources().getColor(R.color.red))
                Toast.makeText(this,"Enter Meter Reading",Toast.LENGTH_LONG).show()
            }
            else if (date.text.trim().length == 0) {
                textHeading5.setTextColor(getResources().getColor(R.color.red))
                Toast.makeText(this,"Enter Date",Toast.LENGTH_LONG).show()
            }
            else if (date.text.trim().length < 10 || date.text.trim().length > 10) {
                textHeading5.setTextColor(getResources().getColor(R.color.red))
                Toast.makeText(this,"Date Format is Incorrect",Toast.LENGTH_LONG).show()
            }
            else if (meterName.text.trim().length == 0) {
                textHeading.setTextColor(getResources().getColor(R.color.red))
                Toast.makeText(this, "Give Your Meter A Name", Toast.LENGTH_LONG).show()
            }

            else {

                val saver = editMeterReading.text.toString().toInt()
                meterReading.setText("$saver")

                val dateSaver = date.text.toString().trim()
                textView.setText("$dateSaver")

                val meterNameSaver = date.text.toString().trim()
                textHeading3.setText("$meterNameSaver")




                val sharedMeterReading = Integer.parseInt(meterReading.text.toString().trim())
                val sharedDate = date.text.toString().trim()
                val sharedMeterName = meterName.text.toString().trim()
                val editor = sharedPreferences.edit()

                editor.putInt("sharedMeterReading", sharedMeterReading)
                editor.putString("sharedDate", sharedDate)
                editor.putString("sharedMeterName", sharedMeterName)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)


            }
        }

        editCurrentReading.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    calculate.performClick()


                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }


           calculate.setOnClickListener {




            val editCurrentReading1 = editCurrentReading.getText().toString()
            val meterReading1 = meterReading.text.toString()


            if (editCurrentReading.text.length == 0) {
                textHeading2.setTextColor(getResources().getColor(R.color.red))
                Toast.makeText(this,"Enter Current Meter Reading",Toast.LENGTH_LONG).show()
            }



                else if (editCurrentReading1.toInt() < meterReading1.toInt()) {
                unit.setText("")
                slab.setText("")
                fuelCharges.setText("")
                nextSlab.setText("")
                dayCount.setText("")
                amount.setText("")
                dueDate.setText("")
                textHeading4.setText("")
                Toast.makeText(this,"Either Meter Reading or Current Reading is INCORRECT",Toast.LENGTH_LONG).show()
               }
                else {

                val currentReading = editCurrentReading.text.toString().toInt()
                val meterReading = meterReading.text.toString().toInt()
                val calcula1 = currentReading - meterReading
                val units = calcula1.toString()
                textHeading2.setTextColor(getResources().getColor(R.color.black))
                val textView = textView.text.toString()
                val textView2 = textView2.text.toString()
                //val startDate =   sdf.format(textView)
                //val endDate = sdf.format(textView2)
                val start1 = textView
                val end1 = textView2
                //val start: LocalDate = LocalDate.of(2017, 2, 3)
                //val end: LocalDate = LocalDate.of(2017, 3, 3)

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                val start: LocalDate = LocalDate.parse(start1, formatter)
                val end: LocalDate = LocalDate.parse(end1, formatter)

                val totalDays = (ChronoUnit.DAYS.between(start, end)) // 28
                dayCount.setText("Total Days Passed: ${totalDays.toString()}")
                //val millionSeconds = textView.time - textView2.time

                if (calcula1 < 50) {
                    val calcula3 = calcula1 * 3.95
                    val calculaFuel = calcula3 * 0.8 / 100
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula3 + calculaFuel
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty
                    val calculaTVL = calculaGST + 35
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 3.95Rs /Unit")
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    nextSlab.setText("Total Units: $units")
                    nextSlab.setTextColor(getResources().getColor(R.color.black))
                    unit.setText("")

                } else if (calcula1 < 101) {

                    val calcula3 = calcula1 * 7.74

                    val calculaFuel = calcula3 * 0.8 / 100
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula3 + calculaFuel
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty
                    val calculaTVL = calculaGST + 35
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 7.74 Rs/Unit")
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    unit.setText("Total Units: $units")
                    val unitRemaining = 100 - calcula1
                    val unitRemaining1 = unitRemaining.toInt().toString()
                    if (unitRemaining < 50) {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.red))
                    } else {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.black))
                    }
                } else if (calcula1 > 100 && calcula1 < 201) {

                    val calcula3 = calcula1 - 100
                    val calcula4 = 100 * 7.74
                    val calcula5 = calcula3 * 10.06

                    val calculaFuelX = calcula4 + calcula5
                    val calculaFuel = calculaFuelX * 0.8 / 100
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula4 + calcula5 + calculaFuel
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty
                    val calculaTVL = calculaGST + 35
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 10.06 Rs/Unit")
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    unit.setText("Total Units: $units")
                    val unitRemaining = 200 - calcula1
                    val unitRemaining1 = unitRemaining.toInt().toString()
                    if (unitRemaining < 50) {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.red))
                    } else {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.black))
                    }
                } else if (calcula1 > 200 && calcula1 < 301) {

                    val calcula3 = calcula1 - 200
                    val calcula4 = 200 * 10.06
                    val calcula5 = calcula3 * 12.15

                    val calculaFuelX = calcula4 + calcula5
                    val calculaFuel = calculaFuelX * 0.8 / 100
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula4 + calcula5 + calculaFuel
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty
                    val calculaTVL = calculaGST + 35
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 12.15 Rs/Unit")
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    unit.setText("Total Units: $units")
                    val unitRemaining = 300 - calcula1
                    val unitRemaining1 = unitRemaining.toInt().toString()
                    if (unitRemaining < 50) {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.red))
                    } else {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.black))
                    }
                } else if (calcula1 > 300 && calcula1 < 701) {

                    val calcula3 = calcula1 - 300
                    val calcula4 = 300 * 12.15
                    val calcula5 = calcula3 * 19.55
                    val calcula6 = calcula1 * 1.65
                    val calculaFuelX = calcula4 + calcula5
                    val calculaFuel = calculaFuelX * 0.8 / 100
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula4 + calcula5 + calcula6 + calculaFuel
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty
                    val calculaTVL = calculaGST + 35
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    unit.setText("Total Units: $units")
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 19.55 Rs/Unit")
                    slab.setTextColor(getResources().getColor(R.color.red))
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    val unitRemaining = 700 - calcula1
                    val unitRemaining1 = unitRemaining.toInt().toString()
                    if (unitRemaining < 50) {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.red))
                    } else {
                        nextSlab.setText("$unitRemaining1 units remaining to get to next slab")
                        nextSlab.setTextColor(getResources().getColor(R.color.black))
                    }
                } else {

                    val calcula3 = calcula1 - 700                   //317
                    val calcula4 = 700 * 19.55                      //13685
                    val calcula5 = calcula3 * 22.65                 //7180
                    val calcula6 = calcula1 * 1.65                  //1678
                    val calculaFuelX = calcula4 + calcula5         //20865
                    val calculaFuel = calculaFuelX * 0.8 / 100          //166.92
                    val calculaFuel1 = calculaFuel.toInt().toString()
                    val calcula2 = calcula4 + calcula5 + calcula6 + calculaFuel //22709
                    val calculaDuty = calcula2 * 1.5 / 100 + calcula2   //340.635 + 22709 = 23049
                    val calculaGST = calculaDuty * 17 / 100 + calculaDuty   //26967
                    val calculaTVL = calculaGST + 35                    //27002
                    val dec = DecimalFormat("###,###")
                    val result = dec.format(calculaTVL)
                    unit.setText("Total Units: $units")
                    amount.setText("$result")
                    slab.setText("Current Highest Slab: 22.65 Rs/Unit")
                    slab.setTextColor(getResources().getColor(R.color.red))
                    fuelCharges.setText("Fuel Charges (approx): $calculaFuel1")
                    val calculaDue = calcula2 * 10 / 100 + calculaTVL
                    val calculaDueDate = dec.format(calculaDue)
                    dueDate.setText("$calculaDueDate")
                    nextSlab.setText("You are at Highest Slab")
                    nextSlab.setTextColor(getResources().getColor(R.color.red))


                }
                textHeading4.setText("After Due Date")
            }

               val sharedCurrentReading = editCurrentReading.text.toString().trim()
               val editor = sharedPreferences.edit()
               editor.putString("sharedCurrentReading", sharedCurrentReading)
               editor.apply()

        }

            val handler = Handler()

                if (mamba != 0) {

                    calculate.performClick()
                }




        reset.setOnClickListener {

            meterReading.setText("0")
            editCurrentReading.setText("0")
            date.setText("")
            meterName.setText("")

            val sharedMeterReading = Integer.parseInt(meterReading.text.toString().trim())
            val sharedCurrentReading = editCurrentReading.text.toString().trim()
            val sharedDate = date.text.toString().trim()
            val sharedMeterName = textHeading.text.toString().trim()
            val editor = sharedPreferences.edit()

            editor.putInt("sharedMeterReading", sharedMeterReading)
            editor.putString("sharedCurrentReading", sharedCurrentReading)
            editor.putString("sharedDate", sharedDate)
            editor.putString("sharedMeterName", sharedMeterName)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra("ggwp", "$StringBuilder")

            startActivity(intent)

        }

    }

    override fun onTouchEvent(tochevent: MotionEvent): Boolean {
        when (tochevent.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = tochevent.x
                y1 = tochevent.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = tochevent.x
                y2 = tochevent.y
                if (x1 < x2) {
                    val i = Intent(this@MainActivity, SecondActivity::class.java)
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
                    startActivity(i)

                    Handler()


                }
            }
        }
        return false
    }



}