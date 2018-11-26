package com.pdg.gesundheitscloudtest

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import kotlinx.android.synthetic.main.play_song.*
import java.util.ArrayList

class SongPlayer: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.play_song)

        val receivedArrayPos = intent.getIntExtra("selectedSong", -1)
        val receivedArray = intent.getSerializableExtra("array") as? ArrayList<SearchResultItem>

        var currentSong = receivedArray?.get(receivedArrayPos)

        if(currentSong!=null) {
            Glide.with(this).load(currentSong.artworkUrl100).placeholder(R.drawable.loading).into(song_imageview)

            song_title.text = currentSong.trackName

            song_artist.text = currentSong.artistName

            song_album.text = currentSong.collectionName

            song_genre.text = currentSong.primaryGenreName

            song_length.text = SimpleDateFormat("mm:ss").format(currentSong.trackTimeMillis).toString()
        }

    }
}