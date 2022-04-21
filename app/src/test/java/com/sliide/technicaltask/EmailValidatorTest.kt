package com.sliide.technicaltask

import com.sliide.technicaltask.utils.Utils.validateEmailAddress
import org.junit.Assert.*
import org.junit.Test

class EmailValidatorTest {

    @Test
    fun correctEmailSimple() {
        assertTrue(validateEmailAddress("name@email.com"))
    }

    @Test
    fun correctEmailSubDomain() {
        assertTrue(validateEmailAddress("name@email.co.uk"))
    }

    @Test
    fun invalidEmailNoTld() {
        assertFalse(validateEmailAddress("name@email"))
    }

    @Test
    fun invalidEmailDoubleDot() {
        assertFalse(validateEmailAddress("name@email..com"))
    }

    @Test
    fun invalidEmailNoUsername() {
        assertFalse(validateEmailAddress("@email.com"))
    }

    @Test
    fun emptyString() {
        assertFalse(validateEmailAddress(""))
    }

    @Test
    fun nullEmail() {
        assertFalse(validateEmailAddress(" "))
    }
}