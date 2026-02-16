package com.kidstube.core.data.repository

object LanguageRegionMap {
    private val languageToRegion = mapOf(
        "en" to "US",
        "es" to "ES",
        "fr" to "FR",
        "de" to "DE",
        "pt" to "BR",
        "it" to "IT",
        "nl" to "NL",
        "ru" to "RU",
        "ja" to "JP",
        "ko" to "KR",
        "zh" to "CN",
        "ar" to "SA",
        "hi" to "IN",
        "tr" to "TR",
        "pl" to "PL",
        "sv" to "SE",
        "da" to "DK",
        "no" to "NO",
        "fi" to "FI",
        "el" to "GR",
        "cs" to "CZ",
        "ro" to "RO",
        "hu" to "HU",
        "th" to "TH",
        "vi" to "VN",
        "id" to "ID",
        "ms" to "MY",
        "uk" to "UA",
        "bg" to "BG",
        "hr" to "HR",
        "sk" to "SK",
        "he" to "IL",
        "ca" to "ES",
    )

    fun getRegion(languageCode: String): String? =
        languageToRegion[languageCode.take(2).lowercase()]
}
