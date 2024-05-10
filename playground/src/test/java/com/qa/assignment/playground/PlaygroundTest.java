package com.qa.assignment.playground;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.google.common.collect.ImmutableMap;

public class PlaygroundTest {
    final String TEST_URI = "http://uitestingplayground.com/";

    @BeforeClass
    static void selenideSetup() {
        // if Chrome browser is used, force not to check for a weak password by disabling password manager
        if (Configuration.browser.equals("chrome")) {
            final ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", ImmutableMap.of("credentials_enable_service", false, "profile.password_manager_enabled", false));
            Configuration.browserCapabilities = options;
            Configuration.timeout = 40000;
//            Configuration.headless = true;
        }
    }

    @BeforeMethod
    public void openWebPage() {
        open(TEST_URI);
        $(byId("title")).shouldHave(text("UI Test Automation Playground"));
    }

    @Test
    public void testSampleApp() {
        final String username = "user";
        final String password = "pwd";

        // Navigate to Sample App page
        $(byLinkText("Sample App")).shouldBe(clickable).click();

        // Set credentials
        $(byName("UserName")).shouldBe(editable).setValue(username);
        $(byName("Password")).shouldBe(editable).setValue(password);

        // Login
        $(byId("login")).shouldBe(enabled).click();

        // Verify whether the login is succefful
        $(byId("loginstatus")).shouldHave(exactText("Welcome, " + username + "!"));
    }

    @Test
    public void testLoadDelay() {
        // Navigate to Load Delay page
        $(byLinkText("Load Delay")).shouldBe(clickable).click();

        // Verify the button is visible and enabled
        $(byText("Button Appearing After Delay")).shouldBe(visible).shouldBe(clickable).click();
    }

    @Test
    public void testProgressBar() {
        // Define Progress Bar and its limit
        final String PROGRESS_BAR_LIMIT = "75%";
        final SelenideElement PROGRESS_BAR = $(byId("progressBar"));

        // Navigate to Progress Bar page
        $(byLinkText("Progress Bar")).shouldBe(clickable).click();

        // Define Start and Stop buttons, and Result
        final SelenideElement START_BUTTON = $(byId("startButton")).shouldBe(enabled);
        final SelenideElement STOP_BUTTON = $(byId("startButton")).shouldBe(enabled);
        final SelenideElement RESULT = $(byId("result")).shouldBe(visible);

        // CLick on Start button
        START_BUTTON.click();

        // Wait for the progress bar to reach 75%
        // Flakiness may appear when the progress bar is reset during loading - no capacity to fix it now
        while (!PROGRESS_BAR.getText().equals(PROGRESS_BAR_LIMIT)) {
            if (PROGRESS_BAR.has(exactText(PROGRESS_BAR_LIMIT))) {

                // Click on Stop button
                STOP_BUTTON.click();

                // Check the Result
                RESULT.shouldHave(exactText("Result: 0"));
            }
        }
    }
}
