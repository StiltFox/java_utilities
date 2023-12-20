package com.stiltfox.utilities.io

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.stiltfox.utilities.test_tools.StiltFoxTest
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.ClassRule

class DownloadableUrlTest extends StiltFoxTest
{
    @ClassRule
    public WireMockRule wireMock = new WireMockRule(1982)

    def before()
    {
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/data")).willReturn(WireMock.aResponse()
                .withBody(
                        "鬣狼はいいですよね。"
                )))
        wireMock.start()
    }

    def cleanup()
    {
        wireMock.stop()
    }

    def "DownloadableUrl will throw a MalformedURLException exception if it's created with a string that's not a url"()
    {
        given: "We have a string that's not a url"
        String badUrl = "not a url"

        when: "We try to make a DownloadableUrl"
        DownloadableUrl url = [badUrl]

        then: "We throw an exception"
        def e = thrown(Exception)
        e instanceof MalformedURLException || e.getCause() instanceof MalformedURLException
    }

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

    def "getNameWithoutExtension will return the entire url minus the extension of the file to grab"(String urlPath, String expected)
    {
        given: "We have a url pointing to a downloadable file"
        DownloadableUrl url = [urlPath]

        when: "We get the name of the url without the extension"
        String actual = url.getNameWithoutExtension()

        then: "We get back the name of the url without the extension"
        actual == expected

        where:
        urlPath << ["https://test.stiltfox.com", "https://test.stiltfox.com/somefile.txt", "https://test.stiltfox.com/somefile", "https://test.stiltfox.com/somefile?urlquery=value", "https://test.stiltfox.com/somefile.txt?urlquery=value"]
        expected << ["test.stiltfox.com", "test.stiltfox.com/somefile", "test.stiltfox.com/somefile", "test.stiltfox.com/somefile", "test.stiltfox.com/somefile"]
    }

    def "copyTo will read the contents of the url and write them into the provided file"()
    {
        given: "We have a url with some data"
        DownloadableUrl url = ["http://localhost:1982/data"]
        def copyLocation = tempFolder.newFile("location.txt")

        when: "We try to copy the url to a file"
        url.copyTo(copyLocation)

        then: "The data from the url is copied to the file"
        copyLocation.readBytes() == "鬣狼はいいですよね。".bytes
    }

    def "copyTo will overwrite the contents of an existing file"()
    {
        given: "We have a file with data already in it"
        DownloadableUrl url = ["http://localhost:1982/data"]
        def copyLocation = tempFolder.newFile("location.txt")
        copyLocation.write("何")

        when: "We try to copy the url to the file"
        url.copyTo(copyLocation)

        then: "The contents is overwritten"
        copyLocation.readBytes() == "鬣狼はいいですよね。".bytes
    }

    def "copyTo will throw an IOException if the url referred to cannot be accessed"()
    {
        given: "We have a url that does not exist"
        DownloadableUrl url = ["http://localhost:9999/non-existing/endpoint"]
        def copyLocation = tempFolder.newFile("location.txt")

        when: "We try to copy the url to a file"
        url.copyTo(copyLocation)

        then: "We throw an IOException and nothing is written to the file"
        thrown IOException
        copyLocation.readBytes() == [] as byte[]
    }

    def "getFullName will return the string used to create the url"(String creationString)
    {
        given: "We have a url"
        DownloadableUrl url = [creationString]

        when: "We try to get the full name of the url"
        def actual = url.getFullName()

        then: "We get back the name of the url"
        actual == creationString

        where:
        creationString << ["https://stiltfox.com", "file://320498230942349807.txt"]
    }

    def "sha256 will return the sha256 sum of the body of the response"()
    {
        given: "We have a url with some data"
        DownloadableUrl url = ["http://localhost:1982/data"]

        when: "We get the sha256 of the data"
        def actual = url.sha256()

        then: "We get back the proper hash value"
        actual == "923e9cd69ddc9aea7ed845413046c9fa1bb4504cf01b94f3338ec966dbdffc95"
    }

    def "md5 will return the md5 hash of the body of the response"()
    {
        given: "We have a url with some data"
        DownloadableUrl url = ["http://localhost:1982/data"]

        when: "We get the md5 hash of the data"
        def actual = url.md5()

        then: "WE get back the md5 hash of the data"
        actual == "f6cedb8e6b0a7c6561659f33eab1ecc0"
    }
}