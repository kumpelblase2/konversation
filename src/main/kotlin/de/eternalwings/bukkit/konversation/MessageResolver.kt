package de.eternalwings.bukkit.konversation

import org.bukkit.conversations.ConversationContext

/**
 * Resolves the prompt message to display give a certain conversation context.
 */
@FunctionalInterface
interface MessageResolver {
    fun getMessageInContext(context: ConversationContext): String
}

/**
 * Resolves a constant message for a prompt
 */
class ConstantMessageResolver(private val message: String): MessageResolver {
    override fun getMessageInContext(context: ConversationContext): String {
        return this.message
    }
}
