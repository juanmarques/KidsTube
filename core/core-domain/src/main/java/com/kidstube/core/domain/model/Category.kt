package com.kidstube.core.domain.model

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val searchQueries: Map<String, String>,
    val isPriority: Boolean = false
) {
    fun getSearchQuery(languageCode: String?): String {
        // Always prefer Dutch queries
        return searchQueries["nl"] ?: searchQueries["en"]!!
    }
}

object Categories {
    // PRIORITY: Real kids channels he loves (Dutch versions)
    val priority = listOf(
        Category(
            id = "steven_maggie",
            name = "Steve and Maggie",
            emoji = "\uD83E\uDDD9",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Steve and Maggie Nederlands kinderen",
                "en" to "Steve and Maggie Wow English TV kids",
            )
        ),
        Category(
            id = "vlad_niki",
            name = "Vlad & Niki",
            emoji = "\uD83D\uDC66",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Vlad en Niki Nederlands kinderen speelgoed",
                "en" to "Vlad and Niki Nederlands Dutch kids",
            )
        ),
        Category(
            id = "diana_roma",
            name = "Diana & Roma",
            emoji = "\uD83D\uDC67",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Diana en Roma Nederlands kinderen spelen",
                "en" to "Diana and Roma Nederlands Dutch kids",
            )
        ),
        Category(
            id = "nastya",
            name = "Like Nastya",
            emoji = "\uD83D\uDC78",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Like Nastya Nederlands kinderen spelen",
                "en" to "Like Nastya Nederlands Dutch kids",
            )
        ),
        Category(
            id = "ryan_world",
            name = "Ryan's World",
            emoji = "\uD83C\uDF1F",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Ryan's World Nederlands kinderen speelgoed",
                "en" to "Ryan's World Nederlands Dutch kids",
            )
        ),
        Category(
            id = "kids_diana_show",
            name = "Kids Diana Show",
            emoji = "\uD83C\uDF80",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "Kids Diana Show Nederlands kinderen",
                "en" to "Kids Diana Show Nederlands Dutch",
            )
        ),
        Category(
            id = "a_for_adley",
            name = "A for Adley",
            emoji = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66",
            isPriority = true,
            searchQueries = mapOf(
                "nl" to "A for Adley kinderen familie",
                "en" to "A for Adley kids family",
            )
        ),
    )

    // Dutch kids shows and channels
    val dutch = listOf(
        Category(
            id = "juf_roos",
            name = "Juf Roos",
            emoji = "\uD83C\uDFB6",
            searchQueries = mapOf(
                "nl" to "Juf Roos kinderliedjes Nederlands peuters kleuters",
                "en" to "Juf Roos Dutch kids songs",
            )
        ),
        Category(
            id = "bumba",
            name = "Bumba",
            emoji = "\uD83E\uDD21",
            searchQueries = mapOf(
                "nl" to "Bumba Studio 100 clown kinderen Nederlands",
                "en" to "Bumba Dutch clown kids",
            )
        ),
        Category(
            id = "kabouter_plop",
            name = "Kabouter Plop",
            emoji = "\uD83C\uDF44",
            searchQueries = mapOf(
                "nl" to "Kabouter Plop Studio 100 Nederlands kinderen",
                "en" to "Kabouter Plop Dutch gnome kids",
            )
        ),
        Category(
            id = "woezel_pip",
            name = "Woezel & Pip",
            emoji = "\uD83D\uDC15",
            searchQueries = mapOf(
                "nl" to "Woezel en Pip Nederlands kinderen liedjes",
                "en" to "Woezel and Pip Dutch kids",
            )
        ),
        Category(
            id = "nijntje",
            name = "Nijntje",
            emoji = "\uD83D\uDC30",
            searchQueries = mapOf(
                "nl" to "Nijntje Nederlands kinderen konijn",
                "en" to "Nijntje Miffy Dutch kids",
            )
        ),
        Category(
            id = "masha_beer",
            name = "Masha en de Beer",
            emoji = "\uD83D\uDC3B",
            searchQueries = mapOf(
                "nl" to "Masha en de Beer Nederlands kinderen afleveringen",
                "en" to "Masha and the Bear Dutch Nederlands",
            )
        ),
        Category(
            id = "peppa_pig",
            name = "Peppa Pig",
            emoji = "\uD83D\uDC37",
            searchQueries = mapOf(
                "nl" to "Peppa Pig Nederlands hele afleveringen kinderen",
                "en" to "Peppa Pig Dutch Nederlands episodes",
            )
        ),
        Category(
            id = "paw_patrol",
            name = "Paw Patrol",
            emoji = "\uD83D\uDC36",
            searchQueries = mapOf(
                "nl" to "Paw Patrol Nederlands hele afleveringen kinderen",
                "en" to "Paw Patrol Dutch Nederlands episodes",
            )
        ),
        Category(
            id = "bing",
            name = "Bing",
            emoji = "\uD83D\uDC30",
            searchQueries = mapOf(
                "nl" to "Bing Nederlands kinderen konijn afleveringen",
                "en" to "Bing Dutch Nederlands bunny kids",
            )
        ),
        Category(
            id = "tik_tak",
            name = "Tik Tak",
            emoji = "\u23F0",
            searchQueries = mapOf(
                "nl" to "Tik Tak Ketnet kinderen peuters kleuters",
                "en" to "Tik Tak Belgian Dutch kids",
            )
        ),
        Category(
            id = "sesamstraat",
            name = "Sesamstraat",
            emoji = "\uD83D\uDFE2",
            searchQueries = mapOf(
                "nl" to "Sesamstraat Nederlands Elmo Pino kinderen",
                "en" to "Sesamstraat Dutch Sesame Street",
            )
        ),
        Category(
            id = "zandkasteel",
            name = "Het Zandkasteel",
            emoji = "\uD83C\uDFF0",
            searchQueries = mapOf(
                "nl" to "Het Zandkasteel NTR kinderen Sassa Tull",
                "en" to "Het Zandkasteel Dutch kids",
            )
        ),
        Category(
            id = "maya_bij",
            name = "Maya de Bij",
            emoji = "\uD83D\uDC1D",
            searchQueries = mapOf(
                "nl" to "Maya de Bij Nederlands kinderen Studio 100",
                "en" to "Maya the Bee Dutch Nederlands",
            )
        ),
        Category(
            id = "mini_disco",
            name = "Mini Disco",
            emoji = "\uD83D\uDD7A",
            searchQueries = mapOf(
                "nl" to "Mini Disco Nederlands kinderliedjes dansen",
                "en" to "Mini Disco Dutch kids dance",
            )
        ),
        Category(
            id = "disney_nl",
            name = "Disney NL",
            emoji = "\uD83C\uDFF0",
            searchQueries = mapOf(
                "nl" to "Disney Junior Nederlands kinderen",
                "en" to "Disney Junior Dutch Nederlands",
            )
        ),
        Category(
            id = "cocomelon",
            name = "CoComelon",
            emoji = "\uD83C\uDF49",
            searchQueries = mapOf(
                "nl" to "CoComelon Nederlands kinderliedjes",
                "en" to "CoComelon Dutch Nederlands",
            )
        ),
        Category(
            id = "blippi",
            name = "Blippi",
            emoji = "\uD83D\uDC68",
            searchQueries = mapOf(
                "nl" to "Blippi Nederlands kinderen leren",
                "en" to "Blippi Dutch Nederlands kids",
            )
        ),
        Category(
            id = "efteling",
            name = "Efteling",
            emoji = "\uD83C\uDFA0",
            searchQueries = mapOf(
                "nl" to "Efteling sprookjes kinderen attractiepark",
                "en" to "Efteling Dutch theme park kids",
            )
        ),
        Category(
            id = "kinderliedjes",
            name = "Kinderliedjes",
            emoji = "\uD83C\uDFB5",
            searchQueries = mapOf(
                "nl" to "Nederlandse kinderliedjes peuters kleuters liedjes",
                "en" to "Dutch kids songs kinderliedjes",
            )
        ),
        Category(
            id = "k3",
            name = "K3",
            emoji = "\uD83C\uDF1F",
            searchQueries = mapOf(
                "nl" to "K3 Studio 100 kinderen liedjes dans",
                "en" to "K3 Dutch Belgian kids songs",
            )
        ),
    )

    // General Dutch content
    val general = listOf(
        Category(
            id = "tekenfilms",
            name = "Tekenfilms",
            emoji = "\uD83D\uDCFA",
            searchQueries = mapOf(
                "nl" to "tekenfilm voor kinderen Nederlands kinderserie",
                "en" to "Dutch cartoons kids Nederlands",
            )
        ),
        Category(
            id = "leren",
            name = "Leren",
            emoji = "\uD83D\uDCDA",
            searchQueries = mapOf(
                "nl" to "educatieve video kinderen Nederlands kleuren leren tellen",
                "en" to "Dutch educational kids learning Nederlands",
            )
        ),
        Category(
            id = "dieren",
            name = "Dieren",
            emoji = "\uD83D\uDC3B",
            searchQueries = mapOf(
                "nl" to "dieren voor kinderen Nederlands dierengeluiden",
                "en" to "animals for kids Dutch Nederlands",
            )
        ),
        Category(
            id = "speelgoed",
            name = "Speelgoed",
            emoji = "\uD83E\uDDF8",
            searchQueries = mapOf(
                "nl" to "speelgoed uitpakken Nederlands kinderen",
                "en" to "toys unboxing Dutch kids Nederlands",
            )
        ),
        Category(
            id = "dansen",
            name = "Dansen",
            emoji = "\uD83D\uDC83",
            searchQueries = mapOf(
                "nl" to "dansen voor kinderen Nederlands dansliedjes",
                "en" to "dance for kids Dutch Nederlands",
            )
        ),
        Category(
            id = "voertuigen",
            name = "Voertuigen",
            emoji = "\uD83D\uDE97",
            searchQueries = mapOf(
                "nl" to "voertuigen voor kinderen Nederlands auto's treinen",
                "en" to "vehicles for kids Dutch Nederlands",
            )
        ),
    )

    // Combined list: priority first, then dutch, then general
    val all = priority + dutch + general
}
