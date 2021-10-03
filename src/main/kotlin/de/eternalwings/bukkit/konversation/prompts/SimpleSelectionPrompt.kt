package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.MessageResolver
import de.eternalwings.bukkit.konversation.PromptBuilder
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.FixedSetPrompt
import org.bukkit.conversations.Prompt

class SimpleSelectionPrompt(
    private val messageResolver: MessageResolver,
    items: Array<out String>,
    private val callback: PromptBuilder.(String, ConversationContext) -> Unit
) : FixedSetPrompt(*items), ChainablePrompt {
    private var next: ChainablePrompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.getMessageInContext(context)
    }

    override fun acceptValidatedInput(context: ConversationContext, input: String): Prompt? {
        val builder = PromptBuilder(this)
        builder.callback(input, context)
        return builder.injectChain(next)
    }

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }
}
