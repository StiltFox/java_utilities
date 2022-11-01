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

    def "hashBinaryValue will return the hash of any value passed in"(String value, String algorythm, String expected)
    {
        when: "We hash the value"
        def actual = miscOps.hashBinaryValue(value.bytes, algorythm)

        then: "We get back the expected hash"
        actual == expected

        where:
        value << ["scp-346", "scp-346", "scp-682", "scp-682"]
        algorythm << ["SHA-256", "MD5", "SHA-256", "MD5"]
        expected << ["994b4a557c6ef1326c43ddcb71316136e01b35350089a1c6f34c6f0006fa62a5","3a0227504313d18a3f1be8ba5c69bf5f","bba768f74e595b33cffe98ac803f62d125c4f063638554b775f39e0c5acebe3d","46678140a11e43d1d6aa0e33931f96b9"]

    }
}