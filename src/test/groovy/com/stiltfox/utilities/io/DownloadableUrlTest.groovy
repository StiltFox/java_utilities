package com.stiltfox.utilities.io

import com.stiltfox.utilities.test_tools.StiltFoxTest

class DownloadableUrlTest extends StiltFoxTest
{
    def before() {}

    def "getExtension will get the extension of a downloadable file"(String urlPath, String expected)
    {
        given: "We have a url pointing to a downloadable file"
        DownloadableUrl url = [urlPath]

        when: "We get the extension of the url"
        String actual = url.getExtension()

        then: "We get back the extension of the url"
        actual == expected

        where:
        urlPath << ["https://test.stiltfox.com", "https://test.stiltfox.com/somefile.txt", "https://test.stiltfox.com/somefile", "https://test.stiltfox.com/somefile?urlquery=value", "https://test.stiltfox.com/somefile.txt?urlquery=value"]
        expected << [".html", ".txt", ".html", ".html", ".txt"]
    }
}
