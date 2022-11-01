package com.stiltfox.utilities.datatypes

import spock.lang.Specification

class PaginatedListTest extends Specification
{
    def "Creating a new paginated list sets the page size as desired"()
    {
        given: "We have a page size we desire"
        def pageSize = 20

        when: "We create a new paginated list"
        PaginatedList actual = [pageSize]

        then: "The page size is set"
        actual.pageSize == pageSize
    }

    def "Creating a paginated list with a value of 0 or less results in a minimum page size of 1"()
    {
        when: "We create a new paginated list"
        PaginatedList actual = [setSize]

        then: "The page size is set to a value above 0"
        actual.pageSize == actualSize

        where:
        setSize << [0, -1, 20]
        actualSize << [1,1,20]
    }

    def "Creating a paginated list with an existing list will copy the contents of the existing list"()
    {
        given: "We have a pre-made list"
        def list = ["Bob", "Billy", "Jenny", "Cindy"]

        when: "We create a new paginated list"
        PaginatedList actual = [list, 10]

        then: "The lists have the same contents"
        actual == list
    }

    def "Creating a paginated list with an existing list will only allow a minimum page size of 1"()
    {
        given: "We have a pre-made list"
        def list = ["Bob", "Billy", "Jenny", "Cindy"]

        when: "We create a new paginated list"
        PaginatedList actual = [list, setSize]

        then: "The page size is set properly"
        actual.pageSize == actualSize

        where:
        setSize << [0, -1, 20]
        actualSize << [1,1,20]
    }

    def "Setting the size of a list will update the list page size and make sure the value is a minimum of 1"()
    {
        given: "We have a pre-made paginated list"
        PaginatedList list = [15]

        when: "We set the page size"
        list.setPageSize(setSize)

        then: "The page size is set to a value greater than zero"
        list.pageSize == actualSize

        where:
        setSize << [-20, 0, 1, 500]
        actualSize << [1, 1, 1, 500]
    }

    def "getNumberOfPages gets the correct amount of pages for the number of items in the list"()
    {
        given: "We have a paginated list that is populated with some items"
        PaginatedList list = [itemList, pageSize]

        when: "We get the number of pages"
        def actual = list.getNumberOfPages()

        then: "We get back the correct number of pages"
        actual == expected

        where:
        itemList = ["test", "test1", "test2", "test3", "test4", "test5", "test6"]
        pageSize << [1, 3, 4, 7, 10]
        expected << [7, 3, 2, 1, 1]
    }

    def "getPage gets the items in the list on the requested page"()
    {
        given: "We have a paginated list populated with some items"
        def items = ["test", "test1", "test2", "test3", "test4", "test5", "test6"]
        PaginatedList list = [items, 3]

        when: "We fetch the page"
        def actual = list.getPage(pageNumber)

        then: "We get back the proper items for the page"
        actual == expected

        where:
        pageNumber << [-1, 0, 1, 2, 3, 4]
        expected << [["test","test1","test2"],["test","test1","test2"],["test3","test4","test5"],["test6"],["test6"],["test6"]]
    }
}