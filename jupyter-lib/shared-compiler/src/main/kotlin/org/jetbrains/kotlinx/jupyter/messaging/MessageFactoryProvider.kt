package org.jetbrains.kotlinx.jupyter.messaging

import org.jetbrains.kotlinx.jupyter.protocol.api.RawMessage
import org.jetbrains.kotlinx.jupyter.util.UpdatableProvider

interface MessageFactoryProvider : UpdatableProvider<MessageFactory> {
    fun update(rawMessage: RawMessage)
}
