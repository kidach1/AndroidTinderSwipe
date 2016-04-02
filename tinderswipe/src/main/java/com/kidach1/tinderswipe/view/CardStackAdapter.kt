package com.kidach1.tinderswipe.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.support.v4.content.ContextCompat

import com.kidach1.tinderswipe.R
import com.kidach1.tinderswipe.model.CardModel

import java.util.ArrayList

abstract class CardStackAdapter : BaseCardStackAdapter {
    val context: Context

    /**
     * Lock used to modify the content of [.mData]. Any write operation
     * performed on the deque should be synchronized on this lock.
     */
    lateinit private var mData: ArrayList<CardModel>

    private var mShouldFillCardBackground = false

    constructor(context: Context) {
        this.context = context
        mData = ArrayList<CardModel>()
    }

    constructor(context: Context, items: Collection<CardModel>) {
        this.context = context
        mData = ArrayList(items)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var wrapper: FrameLayout? = convertView as FrameLayout?
        val innerWrapper: FrameLayout
        val cardView: View
        val convertedCardView: View
        if (wrapper == null) {
            wrapper = FrameLayout(context)
            wrapper.setBackgroundResource(R.drawable.card_bg)
            if (shouldFillCardBackground()) {
                innerWrapper = FrameLayout(context)
                innerWrapper.setBackgroundColor(ContextCompat.getColor(context, R.color.card_bg))
                wrapper.addView(innerWrapper)
            } else {
                innerWrapper = wrapper
            }
            cardView = getCardView(position, getCardModel(position), null, parent)
            innerWrapper.addView(cardView)
        } else {
            if (shouldFillCardBackground()) {
                innerWrapper = wrapper.getChildAt(0) as FrameLayout
            } else {
                innerWrapper = wrapper
            }
            cardView = innerWrapper.getChildAt(0)
            convertedCardView = getCardView(position, getCardModel(position), cardView, parent)
            if (convertedCardView !== cardView) {
                wrapper.removeView(cardView)
                wrapper.addView(convertedCardView)
            }
        }

        return wrapper
    }

    protected abstract fun getCardView(position: Int, model: CardModel, convertView: View?, parent: ViewGroup): View

    fun shouldFillCardBackground(): Boolean {
        return mShouldFillCardBackground
    }

    fun add(item: CardModel) {
        mData.add(item)
        notifyDataSetChanged()
    }

    fun pop(): CardModel {
        val model: CardModel
        model = mData.removeAt(mData!!.size - 1)
        notifyDataSetChanged()
        return model
    }

    override fun getItem(position: Int): Any {
        return getCardModel(position)
    }

    fun getCardModel(position: Int): CardModel {
        return mData[mData.size - 1 - position]
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }
}
