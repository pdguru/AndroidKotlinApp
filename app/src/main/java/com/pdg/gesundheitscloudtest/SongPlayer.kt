package com.pdg.gesundheitscloudtest

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import kotlinx.android.synthetic.main.play_song.*
import java.io.IOException
import java.util.*

class SongPlayer : AppCompatActivity(), View.OnClickListener {

    val TAG = "GHC"

    var mediaPlayer: MediaPlayer? = null
    var receivedArrayPos: Int? = -1
    var receivedArray: ArrayList<SearchResultItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.play_song)

        receivedArrayPos = intent.getIntExtra("selectedSong", -1)
        receivedArray = intent.getSerializableExtra("array") as? ArrayList<SearchResultItem>

        var currentSong = receivedArray?.get(receivedArrayPos!!)

        setNowPlaying(currentSong)

        song_playpause.setOnClickListener(this)
        song_prev.setOnClickListener(this)
        song_next.setOnClickListener(this)
    }

    private fun setNowPlaying(currentSong: SearchResultItem?) {
        if (currentSong != null) {
            Glide.with(this).load(currentSong.artworkUrl100).placeholder(R.drawable.loading).into(song_imageview)

            song_title.text = currentSong.trackName

            song_artist.text = currentSong.artistName

            song_album.text = currentSong.collectionName

            song_genre.text = currentSong.primaryGenreName

            val url = currentSong.previewUrl
            playPreview(url)

        } else {
            Log.e(TAG, "Current selection is NULL.")
        }

    }

    private fun playPreview(url: String?) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                song_playpause.setImageResource(android.R.drawable.ic_media_play)
            }
        }
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(url)
                setOnPreparedListener(this@SongPlayer)
                prepare()
                //Since the data file is known to be small, prepareAsync() can be omitted
            }
        }catch (iae: IllegalArgumentException){
            Log.e(TAG, iae.message)
            iae.printStackTrace()

        }catch (ioe: IOException){
            Log.e(TAG, ioe.message)
            ioe.printStackTrace()
        }
    }

    private fun setOnPreparedListener(songPlayer: SongPlayer) {
        fun onPrepared(mediaPlayer: MediaPlayer) {
            mediaPlayer.start()
            song_playpause.setImageResource(android.R.drawable.ic_media_pause)
            Toast.makeText(this@SongPlayer, "Playing song preview", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.song_playpause ->
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    song_playpause.setImageResource(android.R.drawable.ic_media_play)
                } else if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    song_playpause.setImageResource(android.R.drawable.ic_media_pause)
                    Toast.makeText(this@SongPlayer, "Playing song preview", Toast.LENGTH_LONG).show()
                }

            R.id.song_next ->
                if (receivedArrayPos!= null && receivedArrayPos!=receivedArray?.size) {
                    receivedArrayPos = receivedArrayPos!!.plus(1)
                    setNowPlaying(receivedArray?.get(receivedArrayPos!!))
                    Log.d(TAG,"going to next song: $receivedArrayPos")
                } else {
                    Toast.makeText(this@SongPlayer, "No next song in list.", Toast.LENGTH_LONG).show()
                }

            R.id.song_prev ->
                if (receivedArrayPos!= null && receivedArrayPos!=0) {
                    receivedArrayPos = receivedArrayPos!!.minus(1)
                    setNowPlaying(receivedArray?.get(receivedArrayPos!!))
                    Log.d(TAG,"going to prev song: $receivedArrayPos")
                } else {
                    Toast.makeText(this@SongPlayer, "No previous song in list.", Toast.LENGTH_LONG).show()
                }
        }//switch
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "Preview song will stop shortly.", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}

