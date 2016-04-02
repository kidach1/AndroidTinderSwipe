package com.kidach1.tinderswipe.model

class Orientations {
    enum class Orientation {
        Ordered, Disordered;

        companion object {
            fun fromIndex(index: Int): Orientation {
                val values = Orientation.values()
                if (index < 0 || index >= values.size) {
                    throw IndexOutOfBoundsException()
                }
                return values[index]
            }
        }
    }
}
