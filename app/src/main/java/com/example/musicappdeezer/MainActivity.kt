package com.example.musicappdeezer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myAdapter: TrackAdapter
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar


    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myRecyclerView = findViewById(R.id.recyclerView)
        myRecyclerView.layoutManager = LinearLayoutManager(this)
        myRecyclerView.setHasFixedSize(true)
        searchView = findViewById(R.id.searchView)
        progressBar = findViewById(R.id.progressBar)

        fetchSongs("Shubh") // Initial search

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    fetchSongs(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun fetchSongs(query: String) {
        progressBar.visibility = View.VISIBLE

        val retrofitData = retrofitBuilder.getData(query)

        retrofitData.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val dataList = response.body()?.data ?: emptyList()
                    myAdapter = TrackAdapter(this@MainActivity, dataList)
                    myRecyclerView.adapter = myAdapter
                    Log.d("TAG:onResponse", "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.d("TAG:onFailure", "OnFailure: ${t.message}")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::myAdapter.isInitialized) {
            myAdapter.releasePlayer()
        }
    }

}






