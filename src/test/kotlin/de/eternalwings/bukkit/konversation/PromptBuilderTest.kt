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
    fun testCompileReadmeExample() {
        val collectEntry: (String, String, Int) -> Unit = { _, _, _ -> }

        buildPrompts {
            message("Welcome to this survey!")
            message("This dialog will guide your through.")
            text("Please enter your name:") { input, context ->
                if (input.isEmpty()) {
                    retry()
                } else {
                    context["name"] = input
                }
            }
            select("Please specify your minecraft gaming experience:", "Beginner", "Advanced", "Expert") { input, context ->
                context["experience"] = input
            }
            custom(
                { "Rate your experience between 1 and 10:" },
                { input ->
                    try {
                        val number = Integer.valueOf(input)
                        number >= 1 && number <= 10
                    } catch (ex: NumberFormatException) {
                        false
                    }
                },
                Integer::valueOf
            ) { value, context ->
                context["rating"] = value
            }
            message("Thank you for taking this survey!") { context ->
                val name: String = context["name"]!!
                val experience: String = context["experience"]!!
                val rating: Int = context["rating"]!!
                collectEntry(name, experience, rating)
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
    fun testNoPromptsAfterRetry() {
        val context = ConversationContext(null, mock(), HashMap())
        val prompt = buildPrompts {
            text("Message") { _, _ ->
                retry()
                message("This will never happen.")
            }
        }

        assertThrows<IllegalStateException> {
            prompt.acceptInput(context, "")
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

    @Test
    fun testJumpBack() {
        val context = ConversationContext(null, mock(), HashMap())
        val prompt = buildPrompts {
            val numberInput = number("Message") { _, _ -> }
            message("Between")
            continueWith(numberInput)
        }

        Assertions.assertEquals("Message", prompt.getPromptText(context))
        val next = prompt.acceptInput(context, "1")!!
        Assertions.assertEquals("Between", next.getPromptText(context))
        val restart = next.acceptInput(context, null)
        Assertions.assertEquals(prompt, restart)
    }

    @Test
    fun testJumpOutFromInput() {
        val context = ConversationContext(null, mock(), HashMap())
        val prompt = buildPrompts {
            val start = message("Between")
            text("Input") { _, _ ->
                continueWith(start)
            }
        }
    }
}
