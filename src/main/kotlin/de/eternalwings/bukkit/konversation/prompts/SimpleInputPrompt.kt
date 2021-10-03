package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.MessageResolver
import de.eternalwings.bukkit.konversation.PromptBuilder
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt

class SimpleInputPrompt(
    private val messageResolver: MessageResolver,
    private val callback: PromptBuilder.(String, ConversationContext) -> Unit
) : StringPrompt(), ChainablePrompt {
    private var next: ChainablePrompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.apply(context)
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        val builder = PromptBuilder(this)
        builder.callback(input!!, context)
        return builder.injectChain(next)
    }

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }
}
