package com.pdg.gesundheitscloudtest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import com.pdg.gesundheitscloudtest.viewcontroller.CustomExpandableListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.util.*
import android.support.v4.view.MenuItemCompat
import android.view.View
import android.widget.*


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var resultArray: ArrayList<SearchResultItem>
    private lateinit var mainListView: ExpandableListView

    var TAG = "GHC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        mainListView = findViewById(R.id.main_listview)

        var queryText: String?

        resultArray = ArrayList()

        main_searchTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                queryText = text.toString()

                if(queryText.isNullOrBlank()){
                    if(resultArray!=null){
                        resultArray.clear()
                    }
                }else {
                    fetchItemsFromURL(queryText!!)

                    if (resultArray != null) {
                        mainListView.setAdapter(CustomExpandableListAdapter(this@MainActivity, resultArray))
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        
        mainListView.setOnGroupClickListener { expandableListView, view, groupPosition, id ->
            if(expandableListView.isGroupExpanded(groupPosition)){
                val songIntent = Intent(this, SongPlayer::class.java)
                songIntent.putExtra("array", resultArray)
                songIntent.putExtra("selectedSong", groupPosition)
                startActivity(songIntent)
            }
            return@setOnGroupClickListener false
        }
    }

    private fun fetchItemsFromURL(searchString: String) {
        Log.i(TAG, "Fetching from URL")

        resultArray.clear()

        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, "https://itunes.apple.com/search?term=$searchString",
                null, Response.Listener { response ->
                    try {
                        val responseResults = response.getJSONArray("results")
                        Log.i(TAG, "onResponse: Response received: " + responseResults.length())
                        for (i in 0 until responseResults.length()) {
                            val searchResultItem = SearchResultItem(
                                responseResults.getJSONObject(i).getString("wrapperType"),
                                responseResults.getJSONObject(i).getString("kind"),
                                responseResults.getJSONObject(i).getInt("artistId"),
                                responseResults.getJSONObject(i).getInt("collectionId"),
                                responseResults.getJSONObject(i).getInt("trackId"),
                                responseResults.getJSONObject(i).getString("artistName"),
                                responseResults.getJSONObject(i).getString("collectionName"),
                                responseResults.getJSONObject(i).getString("trackName"),
                                responseResults.getJSONObject(i).getString("collectionCensoredName"),
                                responseResults.getJSONObject(i).getString("trackCensoredName"),
                                responseResults.getJSONObject(i).getString("artistViewUrl"),
                                responseResults.getJSONObject(i).getString("collectionViewUrl"),
                                responseResults.getJSONObject(i).getString("trackViewUrl"),
                                responseResults.getJSONObject(i).getString("previewUrl"),
                                responseResults.getJSONObject(i).getString("artworkUrl30"),
                                responseResults.getJSONObject(i).getString("artworkUrl60"),
                                responseResults.getJSONObject(i).getString("artworkUrl100"),
                                responseResults.getJSONObject(i).getDouble("collectionPrice").toFloat(),
                                responseResults.getJSONObject(i).getDouble("trackPrice").toFloat(),
                                responseResults.getJSONObject(i).getString("releaseDate"),
                                responseResults.getJSONObject(i).getString("collectionExplicitness"),
                                responseResults.getJSONObject(i).getString("trackExplicitness"),
                                responseResults.getJSONObject(i).getInt("discCount"),
                                responseResults.getJSONObject(i).getInt("discNumber"),
                                responseResults.getJSONObject(i).getInt("trackCount"),
                                responseResults.getJSONObject(i).getInt("trackNumber"),
                                responseResults.getJSONObject(i).getLong("trackTimeMillis"),
                                responseResults.getJSONObject(i).getString("country"),
                                responseResults.getJSONObject(i).getString("currency"),
                                responseResults.getJSONObject(i).getString("primaryGenreName"),
                                responseResults.getJSONObject(i).getBoolean("isStreamable")
                            )

                            resultArray.add(searchResultItem)
                        }
                    } catch (jsonError: JSONException) {
                        Log.e(TAG, "Error parsing JSON: " + jsonError.message)
                        jsonError.printStackTrace()
                    }

                    Log.d(TAG, "onResponse: array size:" + resultArray.size)
                    (mainListView.expandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                }, Response.ErrorListener { error ->
                    Log.e("TAG", "Error retrieving JSON from URL: " + error.message)
                    error.printStackTrace()
                })

        requestQueue.add(jsonObjectRequest)
        Log.i(TAG, "Request sent.")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sort_menu, menu)

        val spinArray = arrayOf("Sort on","Length", "Genre", "Price", "Album")
        val item = menu.findItem(R.id.action_sort)
        val spinner = item.actionView as Spinner

        var adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, spinArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> return
                    1 -> {if(resultArray!=null){
                        resultArray.sortBy({ it.trackTimeMillis })
                        (mainListView.expandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                    }}
                    2 -> {if(resultArray!=null){
                        resultArray.sortBy({ it.primaryGenreName })
                        (mainListView.expandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                    }}
                    3 -> {if(resultArray!=null){
                        resultArray.sortBy({ it.trackPrice })
                        (mainListView.expandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                    }}
                    4 -> {if(resultArray!=null){
                        resultArray.sortBy({ it.collectionName })
                        (mainListView.expandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                    }}
                }

            } //onItemSelected
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
        return true
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                Toast.makeText(this, "Spinner", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }
}

