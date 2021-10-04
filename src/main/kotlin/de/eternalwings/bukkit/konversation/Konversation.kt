@file:JvmName("Konversation")

package de.eternalwings.bukkit.konversation

/**
 * Builder for creating a chain of prompts.
 *
 * ```
 * val prompt = buildPrompts {
 *     message("Display a message")
 *     text("Accept text input") { input, context ->
 *         message("The input was: $input")
 *     }
 * }
 * ```
 *
 * This builds up a chain of prompts that can then be passed
 * to a conversation factory for creating an actual
 * conversation with a player.
 *
 * @see PromptBuilder for possible prompts to configure.
 */
fun buildPrompts(configure: PromptBuilder.() -> Unit): ChainablePrompt {
    val builder = PromptBuilder()
    builder.configure()
    return builder.finish()
}
