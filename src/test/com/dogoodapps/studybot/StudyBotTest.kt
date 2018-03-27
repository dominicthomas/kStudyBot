package com.dogoodapps.com

import com.dogoodapps.studybot.StudyBot

class StudyBotTest {

    @org.junit.Test
    fun parse() {
        val studyBot = StudyBot()
        studyBot.parse("files/westra.lua")
    }
}