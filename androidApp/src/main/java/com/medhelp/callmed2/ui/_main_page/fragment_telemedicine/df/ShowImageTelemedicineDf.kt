package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnStateChangedListener
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmed2.databinding.DfShowImageTelemedicineBinding
import java.io.File
import java.util.*

class ShowImageTelemedicineDf : DialogFragment() {
    lateinit var clickItem: MessageRoomItem
    lateinit var listFile: MutableList<MessageRoomItem>
    fun setData(clickItem: MessageRoomItem, listFile: MutableList<MessageRoomItem>){
        this.clickItem = clickItem
        this.listFile = listFile
    }

    lateinit var binding: DfShowImageTelemedicineBinding

    private var gestureDetector: GestureDetector? = null
    private var latchClickImg = true
    var scaleCurrent = 0f

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.df_show_image_telemedicine, null)
        binding = DfShowImageTelemedicineBinding.bind(view)
        initValue()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initValue() {
        binding.toolbar.inflateMenu(R.menu.menu_share)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.btnShare -> {
                    clickShare()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }


        showImg()
        gestureDetector = GestureDetector(requireContext(), GestureListener())
        binding.image.setOnStateChangedListener(object : OnStateChangedListener {
            override fun onScaleChanged(newScale: Float, origin: Int) {
                scaleCurrent = newScale
                if (newScale == binding.image.getMinScale()) {
                    binding.toolbar.setVisibility(View.VISIBLE)
                } else {
                    binding.toolbar.setVisibility(View.GONE)
                }
            }

            override fun onCenterChanged(newCenter: PointF, origin: Int) {}
        })

        binding.image.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) latchClickImg = true
            if (event.action == MotionEvent.ACTION_MOVE) latchClickImg = false
            if (event.action == MotionEvent.ACTION_UP && latchClickImg) {
                if (binding.toolbar != null) {
                    if (binding.toolbar.visibility == View.VISIBLE)
                        binding.toolbar.visibility = View.GONE
                    else
                        binding.toolbar.visibility = View.VISIBLE
                }
            }
            if (scaleCurrent == binding.image.getMinScale() || scaleCurrent == 0.0f) return@OnTouchListener gestureDetector!!.onTouchEvent(
                event
            ) else return@OnTouchListener false
        })
    }

    private fun clickShare() {
        val toSendMessage = Intent(Intent.ACTION_SEND)
        toSendMessage.type = "image/*"
        val uri = Uri.parse(listFile!![currentItem].text)
        toSendMessage.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(toSendMessage, "MedHelper"))
    }

    var currentItem = 0
    private fun showImg() {
        for(i in 0 until listFile.size){
            if(listFile[i].idMessage == clickItem.idMessage )
                currentItem = i
        }

        if (listFile.size == 0)
            return

        binding.image!!.setImage(ImageSource.uri(Uri.parse(listFile[currentItem].text)))
    }

    private fun onSwipeBottom() {
        //onBackPressed()
    }

    private fun onSwipeTop() {
        //onBackPressed()
    }

    private fun onSwipeLeft() {
        if (currentItem <= listFile!!.size - 2) {
            currentItem++
            binding.image!!.setImage(ImageSource.uri(Uri.parse(listFile[currentItem].text)))
        }
    }

    private fun onSwipeRight() {
        if (currentItem - 1 >= 0) {
            currentItem--
            binding.image!!.setImage(ImageSource.uri(Uri.parse(listFile[currentItem].text)))
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > Companion.SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {

                if (distanceY > 0)
                    onSwipeBottom()
                else
                    onSwipeTop()

                return true
            }
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > Companion.SWIPE_DISTANCE_THRESHOLD && Math.abs(
                    velocityX
                ) > Companion.SWIPE_VELOCITY_THRESHOLD
            ) {

                if (distanceX > 0)
                    onSwipeRight()
                else
                    onSwipeLeft()

                return true
            }
            return false
        }
    }

    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}