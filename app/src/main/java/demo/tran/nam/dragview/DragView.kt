package demo.tran.nam.dragview

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.*
import android.widget.AbsoluteLayout
import android.widget.RelativeLayout
import demo.tran.nam.dragview.databinding.DragViewLayoutBinding
import demo.tran.nam.dragview.databinding.ItemCrossWordBinding
import tran.nam.common.DataBoundViewHolder
import java.util.*
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.drawable.Drawable
import android.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import android.graphics.drawable.TransitionDrawable




class DragView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private val mBinding: DragViewLayoutBinding
    private var mAdapter: AdapterCrossWord? = null
    var onDragListener: onDragViewListener? = null

    init {
        mBinding = DragViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setData(data: List<String>) {

        for (char in data) {
            mBinding.containText.removeAllViews()
            mBinding.containText.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                @SuppressLint("ClickableViewAccessibility")
                override fun onGlobalLayout() {

                    val text = AppCompatTextView(context)
                    text.background = ContextCompat.getDrawable(context, R.drawable.bluebox)
                    text.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    text.text = char
                    text.setTypeface(text.getTypeface(), Typeface.BOLD)
                    text.gravity = Gravity.CENTER
                    text.setOnTouchListener(OnTouchToDrag)

                    val layoutParams = AbsoluteLayout.LayoutParams(
                            context.resources.getDimension(R.dimen.positive_50dp).toInt(),
                            context.resources.getDimension(R.dimen.positive_30dp).toInt(), 0, 0)

                    randomCoordinates(layoutParams)

                    text.setLayoutParams(layoutParams)
                    mBinding.containText.addView(text)

                    mBinding.containText.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
            })
        }

        mAdapter = AdapterCrossWord()
        mAdapter?.replace(data)
        mBinding.rvWord.adapter = mAdapter
    }

    fun randomCoordinates(layoutParams: AbsoluteLayout.LayoutParams) {
        layoutParams.x = randomCoordinate(true)
        layoutParams.y = randomCoordinate(false)
    }

    fun randomCoordinate(isX: Boolean, isHalf: Boolean = false): Int {
        val r = Random()
        val low = if (isX) minX() else minY()
        val high = if (isX) {
            if (isHalf) maxX() / 4 else maxX()
        } else {
            maxY()
        }
        return r.nextInt(high - low) + low
    }

    fun minX(): Int {
        return mBinding.containText.left + (context.resources.getDimension(R.dimen.positive_50dp)).toInt()
    }

    fun minY(): Int {
        return mBinding.rvWord.top + (context.resources.getDimension(R.dimen.positive_30dp).toInt())
    }

    fun maxX(): Int {
        return mBinding.containText.right - (context.resources.getDimension(R.dimen.positive_50dp)).toInt()
    }

    fun maxY(): Int {
        return mBinding.containText.bottom - (context.resources.getDimension(R.dimen.positive_30dp).toInt()) - mBinding.rvWord.height
    }

    /**
     * Draggable object ontouch listener
     * Handle the movement of the object when dragged and dropped
     */
    val OnTouchToDrag = object : View.OnTouchListener {

        private var downX: Float = 0.toFloat()
        private var downY: Float = 0.toFloat()

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onDragListener?.onDragStart()
                    downX = event.x
                    downY = event.y
                }
                MotionEvent.ACTION_MOVE -> {

                    val xDistance = event.x - downX
                    val yDistance = event.y - downY
                    if (xDistance != 0f && yDistance != 0f) {
                        val l = (v.left + xDistance).toInt()
                        val r = (v.right + xDistance).toInt()
                        val t = (v.top + yDistance).toInt()
                        val b = (v.bottom + yDistance).toInt()
                        v.layout(l, t, r, b)
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    if (isViewOverlapping(v, mBinding.containRv)) {
                        val layoutManager = mBinding.rvWord.layoutManager
                        layoutManager?.let {
                            if (it is LinearLayoutManager) {
                                val firstVisible = it.findFirstVisibleItemPosition()
                                val lastVisible = it.findLastVisibleItemPosition()
                                for (i in firstVisible..lastVisible) {
                                    val holder = mBinding.rvWord.findViewHolderForAdapterPosition(i) as DataBoundViewHolder<*>
                                    if (v is AppCompatTextView && holder.binding is ItemCrossWordBinding) {
                                        if (v.text != holder.binding.text) {
                                            continue
                                        } else {
                                            if (isViewOverlapping(v, holder.itemView)) {
                                                val rect = Rect()
                                                holder.itemView.getGlobalVisibleRect(rect)
                                                v.animate().x(rect.left.toFloat()).y(mBinding.containText.bottom - holder.itemView.height.toFloat()).alpha(0f).setDuration(500).setListener(object :Animator.AnimatorListener{
                                                    override fun onAnimationRepeat(animation: Animator?) {

                                                    }

                                                    override fun onAnimationEnd(animation: Animator?) {

                                                    }

                                                    override fun onAnimationCancel(animation: Animator?) {

                                                    }

                                                    override fun onAnimationStart(animation: Animator?) {
                                                        val layers = arrayOfNulls<Drawable>(2)
                                                        layers[0] = ContextCompat.getDrawable(context,R.drawable.blackbox)
                                                        layers[1] = ContextCompat.getDrawable(context,R.drawable.bluebox)
                                                        val transition = TransitionDrawable(layers)
                                                        holder.binding.tvChar.setBackgroundDrawable(transition)
                                                        transition.startTransition(500)
                                                        holder.binding.tvChar.text = v.text
                                                    }
                                                })
                                                onDragListener?.onDragSuccess()
                                            } else {
                                                val xValue = randomCoordinate(true)/* * if (Math.random() < 0.5) -1 else 1*/
                                                val yValue = randomCoordinate(false)

                                                v.animate().x(xValue.toFloat()).y(yValue.toFloat()).setDuration(1000).start()
                                                onDragListener?.onDragWrong()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        onDragListener?.onDragCancel()
                    }
                }
            }
            return true
        }

    }

    private fun isViewOverlapping(firstView: View, secondView: View): Boolean {
        val myViewRect = Rect()
        firstView.getGlobalVisibleRect(myViewRect)

        val otherViewRect1 = Rect()
        secondView.getGlobalVisibleRect(otherViewRect1)
        return Rect.intersects(myViewRect, otherViewRect1)
    }

    interface onDragViewListener {
        fun onDragStart()
        fun onDragWrong()
        fun onDragCancel()
        fun onDragSuccess()
    }
}