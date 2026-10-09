package com.example.tracklayoff.features.chat.ui.state

import androidx.annotation.StringRes
import com.example.tracklayoff.feature.chat.ai.R

/** Presentation keys; localized text is resolved only by the UI. */
enum class ChatFailureMessage(@StringRes val resourceId: Int) {
    AUTHENTICATION_REQUIRED(R.string.chat_error_authentication),
    NETWORK(R.string.chat_error_network),
    SERVICE(R.string.chat_error_service),
    PROTOCOL(R.string.chat_error_protocol),
    INTERRUPTED(R.string.chat_error_interrupted),
    INVALID_PROMPT(R.string.chat_error_invalid_prompt)
}
