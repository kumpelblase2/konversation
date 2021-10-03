package de.eternalwings.bukkit.konversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock

class PromptBuilderTest {
    @Test
    fun testEmptyBuilder() {
        assertThrows<IllegalStateException> {
            buildPrompts {}
        }
    }

    @Test
    fun testCompilesSimplePrompts() {
        val pluginMock = mock<Plugin>()
        buildPrompts {
            message("Test")
            text("Message") { _, _ ->
                retry()
            }
            message({ "test" })
            select("Message", "A", "B", "C") { _, _ ->
            }
            select("Message", setOf("A", "B", "C")) { _, _ ->
            }
            regex("Message", "[abc]+".toPattern()) { _, _ ->
            }
            choosePlayer("Message", pluginMock) { _, _ ->
            }
            confirm("Message") { _, _ ->
            }
            custom(
                { "Message" },
                { text -> text.isNotEmpty() },
                Integer::valueOf
            ) { _, _ ->
            }
        }
    }

    @Test
    fun testNoStrayRetry() {
        assertThrows<IllegalStateException> {
            buildPrompts {
                retry()
            }
        }
    }

    @Test
    fun testRunsContext() {
        val context = ConversationContext(null, mock(), HashMap())
        val prompt = buildPrompts {
            message("Message") { context ->
                context["test"] = 1
            }
        }
        Assertions.assertEquals("Message", prompt.getPromptText(context))
        Assertions.assertEquals(Prompt.END_OF_CONVERSATION, prompt.acceptInput(context, null))
        Assertions.assertEquals(1, context.getSessionData("test"))
    }

    @Test
    fun testChainOfPrompts() {
        val context = ConversationContext(null, mock(), HashMap())
        val firstPrompt = buildPrompts {
            message("Message")
            message("Message 2")
        }

        Assertions.assertEquals("Message", firstPrompt.getPromptText(context))
        val secondPrompt = firstPrompt.acceptInput(context, null)
        Assertions.assertNotNull(secondPrompt)
        Assertions.assertEquals("Message 2", secondPrompt!!.getPromptText(context))
    }

    @Test
    fun testValidateInputs() {
        val context = ConversationContext(null, mock(), HashMap())
        val prompt = buildPrompts {
            number("Message") { _, _ -> }
        }

        Assertions.assertEquals("Message", prompt.getPromptText(context))
        Assertions.assertEquals(prompt, prompt.acceptInput(context, null))
        Assertions.assertEquals(Prompt.END_OF_CONVERSATION, prompt.acceptInput(context, "5"))
    }
}
