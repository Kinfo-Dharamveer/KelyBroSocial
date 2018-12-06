package com.kelybro.android.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.kelybro.android.R


/**
 * Created by Krishna on 12-12-2017.
 */

class CustomTextView : android.support.v7.widget.AppCompatTextView {
    internal var capWords = false

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context) : super(context) {}

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.font)
        // The value 0 is a default, but shouldn't ever be used since the attr
        // is an enum
        val typeface = values.getInt(R.styleable.font_typeface, 0)

        when (typeface) {
            0 -> setTypeface(FontCache.getFont(context, "Montserrat-Regular.ttf"))
            1 -> setTypeface(FontCache.getFont(context, "Montserrat-Bold.ttf"))
            2 -> setTypeface(FontCache.getFont(context, "Montserrat-Medium.ttf"))
            3 -> setTypeface(FontCache.getFont(context, "Montserrat-ExtraBold.ttf"))
            else -> {
            }
        }
        values.recycle()
    }


    override fun setText(text: CharSequence, type: TextView.BufferType) {
        super.setText(text, type)

    }
}