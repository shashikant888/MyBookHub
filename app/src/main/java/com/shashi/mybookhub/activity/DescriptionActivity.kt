package com.shashi.mybookhub.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.ExecutorDelivery
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shashi.mybookhub.R
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookRating: TextView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    var bookId : String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        imgBookImage=findViewById(R.id.imgBookImage)
        txtBookRating=findViewById(R.id.txtBookRating)
        txtBookDesc=findViewById(R.id.txtBookDesc)
        btnAddToFav=findViewById(R.id.btnAddTofav)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE

        if (intent!=null){
            bookId = intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()
        }
        if (bookId == "100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)

        val jsonRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

            try {
                val success = it.getBoolean("success")
                if (success){
                    val bookJSONObject = it.getJSONObject("book_data")
                    progressLayout.visibility = View.GONE

                    Picasso.get().load(bookJSONObject.getString("image"))
                    txtBookName.text = bookJSONObject.getString("name")
                    txtBookAuthor.text = bookJSONObject.getString("author")
                    txtBookPrice.text = bookJSONObject.getString("price")
                    txtBookRating.text = bookJSONObject.getString("rating")
                    txtBookDesc.text = bookJSONObject.getString("description")

                }else{
                    Toast.makeText(this@DescriptionActivity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }

            }catch (e:Exception){
                Toast.makeText(this@DescriptionActivity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        },Response.ErrorListener {
            Toast.makeText(this@DescriptionActivity as Context,"Volley Error $it",Toast.LENGTH_SHORT).show()

        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "0fd16822bb9cae"
                return headers
            }
        }

    }
}