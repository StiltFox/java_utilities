package com.stiltfox.utilities

import com.stiltfox.utilities.test_tools.StiltFoxTest

class MiscOpsTest extends StiltFoxTest
{
    MiscOps miscOps

    def before()
    {
        miscOps = []
    }

    def "Union returns the union of multiple lists"()
    {
        given: "We have some lists we want to take the union of"
        def list1 = [2,6,7,5]
        def list2 = [4,5,6,20,10]
        def list3 = [0,2,7,5,10]
        def list4 = [5,33,44,66]

        when: "We take the union of multiple lists"
        def actual = miscOps.getUnion(list1, list2, list3, list4)

        then: "We get the union of the lists"
        actual == [5]
    }
}