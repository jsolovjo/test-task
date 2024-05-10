package com.qa.assignment.teams;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.List;

import io.restassured.path.json.JsonPath;

public class TeamsTest {
    final int COUNT_OF_TEAMS = 32;
    final String THE_OLDEST_TEAM = "Montreal Canadiens";
    final List<String> TEAMS_IN_MD = Arrays.asList("Carolina Hurricanes", "Columbus Blue Jackets", "New Jersey Devils", "New York Islanders",
            "New York Rangers", "Philadelphia Flyers", "Pittsburgh Penguins", "Washington Capitals");
    final String TEST_URI = "https://qa-assignment.dev1.whalebone.io/api/teams";
    final JsonPath JSON = get(TEST_URI).then().assertThat().statusCode(200).extract().jsonPath();
    final int oldestYear = JSON.getInt("teams.founded.min()");

    @BeforeClass
    static void selenideSetup() {
        // if Chrome browser is used, force not to check for a weak password by disabling password manager
        if (Configuration.browser.equals("chrome")) {
            final ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", ImmutableMap.of("credentials_enable_service", false, "profile.password_manager_enabled", false));
            Configuration.browserCapabilities = options;
//            Configuration.headless = true;
        }
    }

    @Test
    public void testCountOfTeams() {
        assertThat(JSON.getInt("teams.size()"), is(COUNT_OF_TEAMS));
    }

    @Test
    public void isMontrealCanadiensTheOldest() {
        assertThat(JSON.param("oldestYear", oldestYear).get("teams.find { it.founded == oldestYear }.name"), is(THE_OLDEST_TEAM));
    }

    @Test
    public void testCityHasMoreThanOneTeams() {
        final String city = "New York";
        final List<String> teamsInNY = Arrays.asList("New York Islanders", "New York Rangers");

        assertThat(JSON.param("location", city).getInt("teams.findAll { it.location == location }.size()"), is(teamsInNY.size()));
        assertThat(JSON.param("location", city).getList("teams.findAll { it.location == location }.name"), equalTo(teamsInNY));
    }

    @Test
    public void testCountOfTeamsInMD() {
        assertThat(JSON.getInt("teams.findAll { it.division.name == 'Metropolitan' }.size()"), is(TEAMS_IN_MD.size()));
        assertThat(JSON.getList("teams.findAll { it.division.name == 'Metropolitan' }.name"), equalTo(TEAMS_IN_MD));
    }

    @Test
    public void testTheOldestTeamHasMoreCanadians() {
        final String teamUri = JSON.param("oldestYear", oldestYear).get("teams.find { it.founded == oldestYear }.officialSiteUrl") + "roster";

        open(teamUri);
        $(byText("Attaquants")).shouldBe(visible);
        final ElementsCollection countOfCanadians = $$(byText("CAN"));
        final ElementsCollection countOfUS = $$(byText("USA"));
        countOfCanadians.shouldHave(sizeGreaterThan(countOfUS.size()));
    }
}
