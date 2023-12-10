package com.shashi.mybookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.shashi.mybookhub.R
import com.shashi.mybookhub.adapter.DashboardRecyclerAdapter
import com.shashi.mybookhub.model.Book
import com.shashi.mybookhub.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


//    val bookList = arrayListOf(
//        "hello 1",
//        "hello 2",
//        "hello 3",
//        "hello 4",
//        "hello 5",
//        "hello 6",
//        "hello 7",
//        "hello 8",
//        "hello 9",
//        "hello 10",
//    )


    val bookInfoList = arrayListOf<Book>()

//    val bookInfoList= arrayListOf<Book>(
//        Book("Let us C", "Yashwant Kanetkar","299","4.4",R.drawable.letus_c),
//        Book("Let us C++", "Yashwant Kanetkar","399","4.3",R.drawable.letus_cpp),
//        Book("Let us Java", "Yashwant Kanetkar","499","4.2",R.drawable.letus_java),
//        Book("Let us Python", "Yashwant Kanetkar","599","4.1",R.drawable.letus_python),
//        Book("C in Depth", "Yashwant Kanetkar","699","4.4",R.drawable.c_in_depth),
//        Book("Computer Networks", "Yashwant Kanetkar","799","4.0",R.drawable.computer_networks),
//        Book("DBMS", "Yashwant Kanetkar","899","4.5",R.drawable.dbms),
//        Book("DS through C in Depth", "Yashwant Kanetkar","999","4.6",R.drawable.ds_through_c_in_depth),
//        Book("Graph Theory", "Yashwant Kanetkar","1099","4.7",R.drawable.graph_theory),
//        Book("Software Engineering", "Yashwant Kanetkar","1199","4.8",R.drawable.software_engineering)
//
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)

        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)

        progressLayout.visibility=View.VISIBLE


        layoutManager=LinearLayoutManager(activity)




        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {

                //Here we will handle the response
//            println("Response is $it")
                try {
                    progressLayout.visibility=View.GONE

                    val success = it.getBoolean("success")

                    if (success){

                        val data = it.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val bookJsonObject = data.getJSONObject(i)
                            val bookObject = Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )
                            bookInfoList.add(bookObject)

                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)

                            recyclerDashboard.adapter=recyclerAdapter
                            recyclerDashboard.layoutManager=layoutManager

                        }

                    }else{
                        Toast.makeText(activity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                    }

                }catch (e: JSONException){
                    Toast.makeText(activity as Context,"Some Unexpected Error Occurred...!!",Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {
                // Here we will handle errors
//                println("Error is $it")
                Toast.makeText(activity as Context,"Volly Error occurred...!!",Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)


        } else{
            //Internet Is Not Available on App Starts

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){text,listner ->
                //open Settings
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){text,listner ->
                //Exit the app
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}