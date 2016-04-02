package com.kidach1.tinderswipe.model

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.kidach1.tinderswipe.view.CardContainer

class CardModel(var name: String, var tagline: String, var description: String, private var cardImageUrl: String) {
    lateinit var cardLikeImageDrawable: Drawable
	lateinit var cardDislikeImageDrawable: Drawable
	var flg: Int = 0
	var onCardDismissedListener: OnCardDismissedListener? = null
	var onClickListener: OnClickListener? = null

    interface OnCardDismissedListener {
        fun onLike(callback: CardContainer.OnLikeListener)
        fun onDislike()
    }

    interface OnClickListener {
        fun OnClickListener()
    }

    fun getcardImageUrl(): String {
        return cardImageUrl
    }

    fun setcardImageUrl(cardImageUrl: String) {
        this.cardImageUrl = cardImageUrl
    }
}
