package com.getstream.sdk.chat.utils.strings

import android.content.Context

class ChatStringsImpl(private val appContext: Context) : ChatStrings {
    override fun get(resId: Int): String {
        return appContext.getString(resId)
    }

    override fun get(resId: Int, vararg formatArgs: Any?): String {
        return appContext.getString(resId, *formatArgs)
    }
}
