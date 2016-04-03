package com.kidach1.tinderswipe.model

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.kidach1.tinderswipe.view.CardContainer

class CardModel(var name: String, var description: String, var cardImageUrl: String) {
	var onCardDismissedListener: OnCardDismissedListener? = null
	var onClickListener: OnClickListener? = null

    interface OnCardDismissedListener {
        fun onLike(callback: CardContainer.OnLikeListener)
        fun onDislike()
    }

    interface OnClickListener {
        fun OnClickListener()
    }
}
