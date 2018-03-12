package com.dogoodapps.com

import com.dogoodapps.studybot.StudyBot

class StudyBotTest {

    @org.junit.Test
    fun parse() {
        val studyBot = StudyBot("files/westra.lua")
        studyBot.parse()
    }
}