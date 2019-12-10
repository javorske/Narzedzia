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
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity()
{

    internal var dbHelper = DatabaseHelper(this)

    fun showToast (text: String)
    {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }

    fun showDialog(title: String, Message : String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
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
        var date = dateTxt.text.toString()
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

            if (dateTxt.text.toString() != date)
                dateCheckBox.isChecked = false
        }
    }



    fun setCurrentDate()
    {
        dateCheckBox.setOnClickListener(View.OnClickListener {
            if (dateCheckBox.isChecked) {
                val date = Calendar.getInstance().time
                val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
                val formatedDate = formatter.format(date)
                dateTxt.setText(formatedDate.toString())
            }
        })
    }
    fun handleInserts()
    {
        insertBtn.setOnClickListener()
        {
                try
                {
                    if ((nameTxt.text.toString()!="")||(numberOfToolTxt.text.toString()!="")||(dateTxt.text.toString()!="") )
                    {
                        dbHelper.insertData(
                            nameTxt.text.toString(),
                            numberOfToolTxt.text.toString(),
                            dateTxt.text.toString())
                        clearEditTexts()
                    }
                    else
                        showToast("Wypełnij pola")
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
            run {
                try {
                    val isUpdate = dbHelper.updateData(idTxt.text.toString(),
                        nameTxt.text.toString(),
                        numberOfToolTxt.text.toString(),
                        dateTxt.text.toString())
                    if (isUpdate == true)
                        showToast("Data Updated Succesfully")
                    else
                        showToast("Data Not Updated")
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(e.message.toString())
                }
            }
        }
    }

    fun handleDeletes()
    {
        deleteBtn.setOnClickListener()
        {
            try
            {
                dbHelper.deleteData(idTxt.text.toString())
                clearEditTexts()
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
                showDialog("Error", "No Data Found")
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
            showDialog("Data Listing", buffer.toString())
        })
    }

}
