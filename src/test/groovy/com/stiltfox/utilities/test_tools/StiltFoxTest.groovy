package com.stiltfox.utilities.test_tools

import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.sql.*
import java.util.function.Function

abstract class StiltFoxTest extends Specification
{
    @ClassRule
    TemporaryFolder tempFolder = new TemporaryFolder()

    def setup()
    {
        tempFolder.create()
        before()
    }

    abstract def before();
}
