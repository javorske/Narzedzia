package com.example.crudnarzedzia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity()
{
    internal var dbHelper = DatabaseHelper(this)

    fun showToast (text: String)
    {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(Title: String, Message : String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(Title)
        builder.setMessage(Message)
        builder.show()
    }

    fun clearEditTexts()
    {
        nameTxt.setText("")
        numberOfToolTxt.setText("")
        dateTxt.setText("")
        idTxt.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleInserts()
        handleUpdates()
        handleDeletes()
        handleViewing()
        setCurrentDate()
        pickDateFromDialog()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun pickDateFromDialog()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        dateTxt.setOnClickListener()
        {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view: DatePicker?, mYear: Int, mMonth: Int, mDay: Int ->

                dateTxt.setText(""+mDay+"."+ (mMonth + 1)  + "." + mYear)
            }, year, month, day)

            dpd.show()

            dateCheckBox.isChecked = false
        }
    }

    fun setCurrentDate()
    {
        dateCheckBox.setOnClickListener(View.OnClickListener {
            if (dateCheckBox.isChecked) {
                dateTxt.setText(currentDate())
            }
        })
    }

    fun currentDate(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        return formatedDate
    }

    fun handleInserts()
    {
        insertBtn.setOnClickListener()
        {
                try
                {
                    if (nameTxt.text.isEmpty()||dateTxt.text.isEmpty())
                    {
                        showDialog("Wypełnij pola", "Nazwa oraz Data pobrania.")
                    }
                    else
                    {
                        if (numberOfToolTxt.text.isEmpty())
                            numberOfToolTxt.setText("Brak")
                        dbHelper.insertData(
                            nameTxt.text.toString(),
                            numberOfToolTxt.text.toString(),
                            dateTxt.text.toString())
                        clearEditTexts()
                        showToast("Dodano")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    showToast(e.message.toString())
                }
        }
    }

    fun handleUpdates()
    {
        updateBtn.setOnClickListener()
        {
                try
                {
                    if (idTxt.text.isEmpty()||numberOfToolTxt.text.isEmpty()) {
                        showDialog("Wypełnij pola", "ID oraz Numer narzędzia.")
                    }
                        else
                    {
                        dateTxt.setText(currentDate())
                        dbHelper.updateData(
                            idTxt.text.toString(),
                            numberOfToolTxt.text.toString(),
                            dateTxt.text.toString())
                        showToast("Zaktualizowano")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    showToast(e.message.toString())
                }
        }
    }


    fun handleDeletes()
    {
        deleteBtn.setOnClickListener()
        {
            try
            {
                val isDeleted = dbHelper.deleteData(idTxt.text.toString())
                if (isDeleted > 0)
                    showToast("Usunięto")
                else
                {
                    if (idTxt.text.isEmpty())
                        showToast("Wypełnij pole ID")
                    else
                        showToast("Brak wpisu")
                    clearEditTexts()
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                showToast(e.message.toString())
            }
        }
    }

    fun handleViewing()
    {
        viewBtn.setOnClickListener(View.OnClickListener
        {
            val res = dbHelper.allData
            if (res.count == 0)
            {
                showDialog("Błąd", "Brak wpisów")
                return@OnClickListener
            }
            val buffer = StringBuffer()
            while (res.moveToNext())
            {
                buffer.append("ID : " + res.getString(0) + "\n")
                buffer.append("Nazwa : " + res.getString(1) + "\n")
                buffer.append("Nr narzedzia : " + res.getString(2) + "\n")
                buffer.append("Data pobrania : " + res.getString(3) + "\n")
            }
            showDialog("Lista narzędzi", buffer.toString())
        })
    }
}
