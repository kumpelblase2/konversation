package de.eternalwings.bukkit.konversation

import org.bukkit.conversations.ConversationContext
import java.util.function.Function

/**
 * Resolves the prompt message to display give a certain conversation context.
 */
typealias MessageResolver = Function<ConversationContext, String>

/**
 * Resolves a constant message for a prompt
 */
class ConstantMessageResolver(private val message: String): Function<ConversationContext, String> {
    override fun apply(p0: ConversationContext): String {
        return message
    }
}
