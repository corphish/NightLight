package com.corphish.nightlight.design.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LinkItem(@StringRes val captionRes: Int, @DrawableRes val imageRes: Int, val link: String)