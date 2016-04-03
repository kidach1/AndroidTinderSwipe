package com.kidach1.tinderswipe.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.database.DataSetObserver
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ListAdapter

import com.kidach1.tinderswipe.R
import com.kidach1.tinderswipe.model.CardModel
import com.kidach1.tinderswipe.model.Orientations.Orientation

class CardContainer : AdapterView<ListAdapter> {

    companion object {
        val INVALID_POINTER_ID = -1
        private val DISORDERED_MAX_ROTATION_RADIANS = Math.PI / 64
        private val TAG = CardContainer::class.java.getSimpleName()
    }

    private var mActivePointerId = INVALID_POINTER_ID
    private var mNumberOfCards = -1
    private val mDataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            clearStack()
            ensureFull()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            clearStack()
        }
    }
    private val boundsRect = Rect()
    private val childRect = Rect()
    private val mMatrix = Matrix()

    private val mMaxVisible = 10
    private var mGestureDetector: GestureDetector? = null
    private var mFlingSlop: Int = 0
    var orientation: Orientation? = null

    private fun orientationIs(orientation: Orientation) {
        if (orientation == null)
            throw NullPointerException("Orientation may not be null")
        if (this.orientation != orientation) {
            this.orientation = orientation
            if (orientation == Orientation.Disordered) {
                for (i in 0..childCount - 1) {
                    val child = getChildAt(i)
                    child.rotation = disorderedRotation
                }
            } else {
                for (i in 0..childCount - 1) {
                    val child = getChildAt(i)
                    child.rotation = 0f
                }
            }
            requestLayout()
        }
    }

    private var mListAdapter: ListAdapter? = null
    private var mLastTouchX: Float = 0.toFloat()
    private var mLastTouchY: Float = 0.toFloat()
    private var mTopCard: View? = null
    private var mTouchSlop: Int = 0
    var gravity: Int = 0
    private var mNextAdapterPosition: Int = 0
    private var mDragging: Boolean = false
    private var mSwipeListener: onSwipeListener? = null

    constructor(context: Context) : super(context) {
        orientation = Orientation.Disordered
        gravity = Gravity.CENTER
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initFromXml(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initFromXml(attrs)
        init()
    }

    private fun init() {
        val viewConfiguration = ViewConfiguration.get(context)
        mFlingSlop = viewConfiguration.scaledMinimumFlingVelocity
        mTouchSlop = viewConfiguration.scaledTouchSlop
        mGestureDetector = GestureDetector(context, GestureListener())
    }

    private fun initFromXml(attr: AttributeSet) {
        val a = context.obtainStyledAttributes(attr,
                R.styleable.CardContainer)

        gravity = a.getInteger(R.styleable.CardContainer_android_gravity, Gravity.CENTER)
        var orientation = a.getInteger(R.styleable.CardContainer_orientation, 1)
        orientationIs(Orientation.fromIndex(orientation))

        a.recycle()
    }

    fun setOnSwipeListener(onSwipeListener: onSwipeListener) {
        this.mSwipeListener = onSwipeListener
    }

    override fun getAdapter(): ListAdapter {
        return mListAdapter!!
    }

    override fun setAdapter(adapter: ListAdapter) {
        if (mListAdapter != null)
            mListAdapter!!.unregisterDataSetObserver(mDataSetObserver)

        clearStack()
        mTopCard = null
        mListAdapter = adapter
        mNextAdapterPosition = 0
        adapter.registerDataSetObserver(mDataSetObserver)

        ensureFull()

        if (childCount != 0) {
            mTopCard = getChildAt(childCount - 1)
            mTopCard!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        mNumberOfCards = getAdapter().count
        requestLayout()
    }

    private fun ensureFull() {
        while (mNextAdapterPosition < mListAdapter!!.count && childCount < mMaxVisible) {
            val view = mListAdapter!!.getView(mNextAdapterPosition, null, this)
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            if (orientation == Orientation.Disordered) {
                view.rotation = disorderedRotation
            }
            addViewInLayout(view, 0, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    mListAdapter!!.getItemViewType(mNextAdapterPosition)), false)

            requestLayout()

            mNextAdapterPosition += 1
        }
    }

    private fun clearStack() {
        removeAllViewsInLayout()
        mNextAdapterPosition = 0
        mTopCard = null
    }

    private val disorderedRotation: Float
        get() = Math.toDegrees(0.0).toFloat() // 最初の自動配置時の傾き
          //    Math.toDegrees(mRandom.nextGaussian() * DISORDERED_MAX_ROTATION_RADIANS)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val requestedWidth = measuredWidth - paddingLeft - paddingRight
        val requestedHeight = measuredHeight - paddingTop - paddingBottom
        val childWidth: Int
        val childHeight: Int

        if (orientation == Orientation.Disordered) {
            val R1: Int
            val R2: Int
            if (requestedWidth >= requestedHeight) {
                R1 = requestedHeight
                R2 = requestedWidth
            } else {
                R1 = requestedWidth
                R2 = requestedHeight
            }
            childWidth = ((R1 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R2 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS)).toInt()
            childHeight = ((R2 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R1 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS)).toInt()
        } else {
            childWidth = requestedWidth
            childHeight = requestedHeight
        }

        val childWidthMeasureSpec: Int
        val childHeightMeasureSpec: Int
        childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.AT_MOST)
        childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.AT_MOST)

        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            child!!.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        for (i in 0..childCount - 1) {
            boundsRect.set(0, 0, width, height)

            val view = getChildAt(i)
            val w: Int
            val h: Int
            w = view.measuredWidth
            h = view.measuredHeight

            Gravity.apply(gravity, w, h, boundsRect, childRect)
            view.layout(childRect.left, childRect.top, childRect.right, childRect.bottom)
        }
    }

    private val scrollProgressPercent: Float
        get() {
            if (movedBeyondLeftBorder()) {
                return -1f
            } else if (movedBeyondRightBorder()) {
                return 1f
            } else {
                val aPosX = mTopCard!!.x
                val halfWidth = mTopCard!!.width / 2f

                val zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder())
                return zeroToOneValue * 2f - 1f
            }
        }

    private fun movedBeyondLeftBorder(): Boolean {
        val aPosX = mTopCard!!.x
        val halfWidth = mTopCard!!.width / 2f

        return aPosX + halfWidth < leftBorder()
    }

    private fun movedBeyondRightBorder(): Boolean {
        val aPosX = mTopCard!!.x
        val halfWidth = mTopCard!!.width / 2f

        return aPosX + halfWidth > rightBorder()
    }


    fun leftBorder(): Float {
        val parentWidth = (mTopCard!!.parent as ViewGroup).width.toFloat()

        return parentWidth / 4.0f
    }

    fun rightBorder(): Float {
        val parentWidth = (mTopCard!!.parent as ViewGroup).width.toFloat()

        return 3 * parentWidth / 4.0f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mTopCard == null) {
            return false
        }
        if (mGestureDetector!!.onTouchEvent(event)) {
            return true
        }
        Log.i(TAG + ": Touch Event", MotionEvent.actionToString(event.actionMasked) + " ")
        val pointerIndex: Int
        val x: Float
        val y: Float
        val dx: Float
        val dy: Float
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mTopCard!!.getHitRect(childRect)

                pointerIndex = event.actionIndex
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)

                if (!childRect.contains(x.toInt(), y.toInt())) {
                    return false
                }
                mLastTouchX = x
                mLastTouchY = y
                mActivePointerId = event.getPointerId(pointerIndex)


                val points = floatArrayOf(x - mTopCard!!.left, y - mTopCard!!.top)
                mTopCard!!.matrix.invert(mMatrix)
                mMatrix.mapPoints(points)
                mTopCard!!.pivotX = points[0]
                mTopCard!!.pivotY = points[1]
            }
            MotionEvent.ACTION_MOVE -> {

                pointerIndex = event.findPointerIndex(mActivePointerId)
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)

                dx = x - mLastTouchX
                dy = y - mLastTouchY

                Log.i(TAG + ": Action move params x:", x.toString() + " y:" + y.toString() + " dx:" + dx.toString() + " dy:" + dy.toString())

                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
                    mDragging = true
                }

                if (!mDragging) {
                    return true
                }

                mTopCard!!.translationX = mTopCard!!.translationX + dx
                mTopCard!!.translationY = mTopCard!!.translationY + dy

                mTopCard!!.rotation = 40 * mTopCard!!.translationX / (width / 2.0f)

                mLastTouchX = x
                mLastTouchY = y

                if (mSwipeListener != null) {
                    mSwipeListener!!.onSwipe(scrollProgressPercent)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!mDragging) {
                    return true
                }
                mDragging = false
                mActivePointerId = INVALID_POINTER_ID

                cancelAnimation(mTopCard!!)

                if (mSwipeListener != null) {
                    mSwipeListener!!.onSwipe(0f)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)

                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = event.getX(newPointerIndex)
                    mLastTouchY = event.getY(newPointerIndex)

                    mActivePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }

        return true
    }

    private fun cancelAnimation(topCard: View) {

        val animator = ObjectAnimator.ofPropertyValuesHolder(topCard,
                PropertyValuesHolder.ofFloat("translationX", 0.0f),
                PropertyValuesHolder.ofFloat("translationY", 0.0f),
                // 一度dragしたカードから手を離してstackに戻ってきた時の傾き
                //                        PropertyValuesHolder.ofFloat("rotation", (float) Math.toDegrees(mRandom.nextGaussian() * DISORDERED_MAX_ROTATION_RADIANS)),
                PropertyValuesHolder.ofFloat("rotation", Math.toDegrees(0.0).toFloat()),
                PropertyValuesHolder.ofFloat("pivotX", topCard.width / 2.0f),
                PropertyValuesHolder.ofFloat("pivotY", topCard.height / 2.0f))
                .setDuration(150)
        animator.interpolator = AccelerateInterpolator()
        animator.start()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (mTopCard == null) {
            return false
        }
        if (mGestureDetector!!.onTouchEvent(event)) {
            return true
        }
        val pointerIndex: Int
        val x: Float
        val y: Float
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mTopCard!!.getHitRect(childRect)

                val cardModel = adapter.getItem(childCount - 1) as CardModel

                if (cardModel.onClickListener != null) {
                    cardModel.onClickListener?.OnClickListener()
                }
                pointerIndex = event.actionIndex
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)

                if (!childRect.contains(x.toInt(), y.toInt())) {
                    return false
                }

                mLastTouchX = x
                mLastTouchY = y
                mActivePointerId = event.getPointerId(pointerIndex)
            }
            MotionEvent.ACTION_MOVE -> {
                pointerIndex = event.findPointerIndex(mActivePointerId)
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)
                if (Math.abs(x - mLastTouchX) > mTouchSlop || Math.abs(y - mLastTouchY) > mTouchSlop) {
                    val points = floatArrayOf(x - mTopCard!!.left, y - mTopCard!!.top)
                    mTopCard!!.matrix.invert(mMatrix)
                    mMatrix.mapPoints(points)
                    mTopCard!!.pivotX = points[0]
                    mTopCard!!.pivotY = points[1]
                    return true
                }
            }
        }

        return false
    }

    override fun getSelectedView(): View {
        return mTopCard!!
    }

    override fun setSelection(position: Int) {
        throw UnsupportedOperationException()
    }


    class LayoutParams : ViewGroup.LayoutParams {

        internal var viewType: Int = 0

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
        }

        constructor(width: Int, height: Int) : super(width, height) {
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {
        }

        constructor(w: Int, h: Int, viewType: Int) : super(w, h) {
            this.viewType = viewType
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.i(TAG + ": Fling", "Fling with $velocityX, $velocityY")
            val topCard = mTopCard
            val dx = e2.x - e1.x
            if (Math.abs(dx) > mTouchSlop &&
                    Math.abs(velocityX) > Math.abs(velocityY) &&
                    Math.abs(velocityX) > mFlingSlop * 3) {
                var targetX = topCard!!.getX()
                var targetY = topCard!!.getY()
                var duration: Long = 0

                boundsRect.set(0 - topCard.getWidth() - 100, 0 - topCard.getHeight() - 100, width + 100, height + 100)

                while (boundsRect.contains(targetX.toInt(), targetY.toInt())) {
                    targetX += velocityX / 10
                    targetY += velocityY / 10
                    duration += 100
                }

//                duration = Math.min(500, duration)

                mTopCard = getChildAt(childCount - 2)
                val cardModel = adapter.getItem(childCount - 1) as CardModel

                if (mTopCard != null)
                    mTopCard!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                if (cardModel.onCardDismissedListener != null) {
                    if (targetX > 0) {
                        cardModel.onCardDismissedListener?.onLike(object : OnLikeListener {
                            override fun choose() {
                                val arbitraryVelocityX = 100f
                                val arbitraryDuration: Long = 50
                                Log.i(TAG, " duration:" + duration + " targetX:" + targetX + " targetY:" + targetY + " velocityX:" + velocityX)
                                cardAnimate(topCard, arbitraryDuration, targetX, targetY, arbitraryVelocityX)
                            }

                            override fun unchoose() {
                                cancelAnimation(topCard)
                                mTopCard = getChildAt(childCount - 1)
                                if (mSwipeListener != null) {
                                    mSwipeListener!!.onSwipe(0f)
                                }
                            }
                        })
                    } else {
                        cardModel.onCardDismissedListener?.onDislike()
                        Log.i(TAG, " duration:" + duration + " targetX:" + targetX + " targetY:" + targetY + " velocityX:" + velocityX)
                        cardAnimate(topCard, duration, targetX, targetY, velocityX)
                    }
                } else {
                    cardAnimate(topCard, duration, targetX, targetY, velocityX)
                }
                return true
            } else
                return false
        }
    }

    private fun cardAnimate(topCard: View, duration: Long, targetX: Float, targetY: Float, velocityX: Float) {
        topCard.animate()
                .setDuration(duration)
                .alpha(.75f)
                .setInterpolator(LinearInterpolator())
                .x(targetX)
                .y(targetY)
                .rotation(Math.copySign(45f, velocityX))
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        removeViewInLayout(topCard)
                        ensureFull()
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        onAnimationEnd(animation)
                    }
                })
    }

    interface onSwipeListener {
        fun onSwipe(scrollProgressPercent: Float)
    }

    interface OnLikeListener {
        fun choose()

        fun unchoose()
    }
}
