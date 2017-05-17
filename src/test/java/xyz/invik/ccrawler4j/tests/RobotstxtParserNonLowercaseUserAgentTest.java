package xyz.invik.ccrawler4j.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import xyz.invik.ccrawler4j.robotstxt.HostDirectives;
import xyz.invik.ccrawler4j.robotstxt.RobotstxtConfig;
import xyz.invik.ccrawler4j.robotstxt.RobotstxtParser;

public class RobotstxtParserNonLowercaseUserAgentTest {

    @Test
    public void testParseWithNonLowercaseUserAgent() {
        String userAgent = "testAgent";
        String content = "User-agent: " + userAgent + '\n' + "Disallow: /test/path/\n";

        final RobotstxtConfig robotsConfig = new RobotstxtConfig();
        robotsConfig.setUserAgentName(userAgent);

        HostDirectives hostDirectives = RobotstxtParser.parse(content, robotsConfig);
        assertNotNull("parsed HostDirectives is null", hostDirectives);
        assertFalse("HostDirectives should not allow path: '/test/path/'",
                    hostDirectives.allows("/test/path/"));
    }

}
