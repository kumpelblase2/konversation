package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.InnerBuilderCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import de.eternalwings.bukkit.konversation.PromptBuilder
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.PlayerNamePrompt
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class SimplePlayerPrompt(
    private val messageResolver: MessageResolver,
    plugin: Plugin,
    private val callback: InnerBuilderCallback<Player>
) :
    PlayerNamePrompt(plugin), ChainablePrompt {
    private var next: ChainablePrompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.apply(context)
    }

    override fun acceptValidatedInput(context: ConversationContext, input: Player): Prompt? {
        val builder = PromptBuilder(this)
        builder.callback(input, context)
        return builder.injectChain(next)
    }

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }
}
