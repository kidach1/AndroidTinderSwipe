package com.kidach1.tinderswipe.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.kidach1.tinderswipe.R
import com.kidach1.tinderswipe.model.CardModel

class SimpleCardStackAdapter(mContext: Context) : CardStackAdapter(mContext) {
    override fun getCardView(position: Int, model: CardModel, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.std_card_inner, parent, false)
        }

        //		((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
        (convertView!!.findViewById(R.id.title) as TextView).text = model.tagline
        (convertView.findViewById(R.id.description) as TextView).text = model.description

        return convertView
    }
}
