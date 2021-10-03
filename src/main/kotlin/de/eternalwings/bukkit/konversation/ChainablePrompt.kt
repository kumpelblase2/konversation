package de.eternalwings.bukkit.konversation

import org.bukkit.conversations.Prompt

/**
 * A prompt which allows setting a next prompt, so chaining them together.
 */
interface ChainablePrompt : Prompt {
    /**
     * Sets the prompt that should follow this prompt. What follow means, may depend
     * on how the prompt works.
     */
    fun addNextPrompt(next: ChainablePrompt)
}
