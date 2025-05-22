package com.example.musicappdeezer

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import androidx.core.net.toUri


class TrackAdapter(
    private val context: Activity,
    private val dataList: List<Data>
) : RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var playingPosition: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentData = dataList[position]
        holder.title.text = currentData.title
        Picasso.get()
            .load(currentData.album.cover_medium)
            .placeholder(R.drawable.placeholder)
            .into(holder.image)

        holder.play.visibility = View.VISIBLE
        holder.pause.visibility = View.GONE

        if (position == playingPosition) {
            holder.play.visibility = View.GONE
            holder.pause.visibility = View.VISIBLE
        }

        holder.play.setOnClickListener {
            if (playingPosition != position) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                notifyItemChanged(playingPosition)

                mediaPlayer = MediaPlayer.create(context, currentData.preview.toUri())
                mediaPlayer?.start()

                android.widget.Toast.makeText(
                    context,
                    "Playing: ${currentData.title}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

                playingPosition = position
                notifyItemChanged(position)

                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    val oldPosition = playingPosition
                    playingPosition = -1
                    notifyItemChanged(oldPosition)
                }
            } else {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                    android.widget.Toast.makeText(
                        context,
                        "Resumed: ${currentData.title}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                notifyItemChanged(position)
            }
        }

        holder.pause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                holder.play.visibility = View.VISIBLE
                holder.pause.visibility = View.GONE
            }
        }
    }

    fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        playingPosition = -1
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.musicImage)
        val title: TextView = itemView.findViewById(R.id.musicTitle)
        val play: ImageButton = itemView.findViewById(R.id.btnPlay)
        val pause: ImageButton = itemView.findViewById(R.id.btnPause)
    }
}
