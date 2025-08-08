package org.thedroiddiv.corntex

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform