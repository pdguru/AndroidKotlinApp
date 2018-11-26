package com.pdg.gesundheitscloudtest

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import kotlinx.android.synthetic.main.play_song.*
import java.io.IOException
import java.util.*


class SongPlayer : AppCompatActivity(), View.OnClickListener, MediaPlayer.OnCompletionListener{

    val TAG = "GHC"

    var mediaPlayer: MediaPlayer? = null
    var receivedArrayPos: Int? = -1
    var receivedArray: ArrayList<SearchResultItem>? = null
    var handler: Handler? = null

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
            song_title.isSelected = true

            song_artist.text = currentSong.artistName
            song_artist.isSelected = true

            song_album.text = currentSong.collectionName
            song_album.isSelected = true

            song_genre.text = currentSong.primaryGenreName
            song_genre.isSelected = true

            val url = currentSong.previewUrl
            playPreview(url)

        } else {
            Log.e(TAG, "Current selection is NULL.")
        }

    }

    private fun playPreview(url: String?) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
            song_playpause.setImageResource(android.R.drawable.ic_media_play)

        }
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(url)
//                setOnPreparedListener(this@SongPlayer)
                prepare() //Since the data file is known to be small, prepareAsync() can be omitted
                start()
                song_playpause.setImageResource(android.R.drawable.ic_media_pause)
//                progressBarUpdater()
            }

            mediaPlayer?.setOnCompletionListener(this@SongPlayer)
        } catch (iae: IllegalArgumentException) {
            Log.e(TAG, iae.message)
            iae.printStackTrace()

        } catch (ioe: IOException) {
            Log.e(TAG, ioe.message)
            ioe.printStackTrace()
        } catch (ise: IllegalStateException) {
            Log.e(TAG, "IllegalStateException")
            ise.printStackTrace()
        }
    }

    /*progressBar disabled as Song length and
    song preview length are not the same
     */
//    private fun progressBarUpdater() {
//        if(mediaPlayer!=null) {
//            song_progress.progress =
//                (mediaPlayer!!.currentPosition as Float / receivedArray!!.get(receivedArrayPos!!).trackTimeMillis!! * 100) as Int
//             // This math construction give a percentage of "was playing"/"song length"
//            if (mediaPlayer!!.isPlaying()) {
//                val notification = Runnable { progressBarUpdater() }
//                handler?.postDelayed(notification, 1000)
//            }
//        }
//    }


//    override fun onPrepared(mediaPlayer: MediaPlayer) {
//        mediaPlayer.start()
//        song_playpause.setImageResource(android.R.drawable.ic_media_pause)
//        Toast.makeText(this@SongPlayer, "Playing song preview", Toast.LENGTH_LONG).show()
//    }

    override fun onCompletion(mp: MediaPlayer?) {
        song_playpause.setImageResource(android.R.drawable.ic_media_play)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.song_playpause ->
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    song_playpause.setImageResource(android.R.drawable.ic_media_play)
                } else if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    song_playpause.setImageResource(android.R.drawable.ic_media_pause)
                    Toast.makeText(this@SongPlayer, "Playing song preview", Toast.LENGTH_LONG).show()
//                    progressBarUpdater()
                }

            R.id.song_next ->
                if (receivedArrayPos != null && receivedArrayPos != receivedArray?.size) {
                    receivedArrayPos = receivedArrayPos!!.plus(1)
                    setNowPlaying(receivedArray?.get(receivedArrayPos!!))
                    Log.d(TAG, "going to next song: $receivedArrayPos")
                } else {
                    Toast.makeText(this@SongPlayer, "No next song in list.", Toast.LENGTH_LONG).show()
                }

            R.id.song_prev ->
                if (receivedArrayPos != null && receivedArrayPos != 0) {
                    receivedArrayPos = receivedArrayPos!!.minus(1)
                    setNowPlaying(receivedArray?.get(receivedArrayPos!!))
                    Log.d(TAG, "going to prev song: $receivedArrayPos")
                } else {
                    Toast.makeText(this@SongPlayer, "No previous song in list.", Toast.LENGTH_LONG).show()
                }
        }//switch
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Hey, checkout this song I found on iTunes.\n" +
                            receivedArrayPos?.let { receivedArray?.get(it)?.trackViewUrl })
                    type = "text/plain"
                }
                startActivity(sendIntent)
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) Toast.makeText(
            this,
            "Preview song will stop shortly.",
            Toast.LENGTH_LONG
        ).show()
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        super.onDestroy()
    }
}

