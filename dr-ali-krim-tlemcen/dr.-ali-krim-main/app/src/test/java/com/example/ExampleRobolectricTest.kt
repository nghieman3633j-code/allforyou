package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.game.GameApp
import com.example.game.GameViewModel
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("العربية بتلمسان", appName)
  }

  @Test
  fun testGameAppRendersWithoutCrash() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = GameViewModel(application)
    composeTestRule.setContent {
      MyApplicationTheme {
        GameApp(viewModel = viewModel)
      }
    }
    composeTestRule.waitForIdle()
  }
}
