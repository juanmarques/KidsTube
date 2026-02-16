package com.kidstube.core.domain.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

object SupportedLanguages {
    val all = listOf(
        Language("en", "English", "English"),
        Language("es", "Spanish", "Espa\u00f1ol"),
        Language("fr", "French", "Fran\u00e7ais"),
        Language("de", "German", "Deutsch"),
        Language("nl", "Dutch", "Nederlands"),
        Language("pt", "Portuguese", "Portugu\u00eas"),
        Language("it", "Italian", "Italiano"),
        Language("ja", "Japanese", "\u65E5\u672C\u8A9E"),
        Language("ko", "Korean", "\uD55C\uAD6D\uC5B4"),
        Language("zh", "Chinese", "\u4E2D\u6587"),
        Language("ar", "Arabic", "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"),
        Language("hi", "Hindi", "\u0939\u093F\u0928\u094D\u0926\u0940"),
        Language("ru", "Russian", "\u0420\u0443\u0441\u0441\u043A\u0438\u0439"),
        Language("tr", "Turkish", "T\u00FCrk\u00E7e"),
        Language("pl", "Polish", "Polski"),
        Language("sv", "Swedish", "Svenska"),
        Language("da", "Danish", "Dansk"),
        Language("no", "Norwegian", "Norsk"),
        Language("fi", "Finnish", "Suomi"),
    )
}
