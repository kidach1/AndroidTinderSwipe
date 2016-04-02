package com.kidach1.tinderswipe.model

class Likes {
    enum class Like private constructor(val value: Int) {
        None(0), Liked(1), Disliked(2);

        companion object {

            fun fromValue(value: Int): Like? {
                for (style in Like.values()) {
                    if (style.value == value) {
                        return style
                    }
                }
                return null
            }
        }
    }
}
