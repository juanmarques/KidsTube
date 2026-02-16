package com.kidstube.core.data.repository

import javax.inject.Inject
import javax.inject.Singleton

data class RssChannel(
    val channelId: String,
    val languageCode: String,
    val isPriority: Boolean
)

@Singleton
class RssChannelRegistry @Inject constructor() {

    private val channels: List<RssChannel> = listOf(
        // English — Priority
        RssChannel("UCbCmjCuTUZos6Inko4u57UQ", "en", isPriority = true),  // Cocomelon
        RssChannel("UCkQO3QsgTpNTsOw6ujimT5Q", "en", isPriority = true),  // Super Simple Songs
        RssChannel("UCHcZV2bot1kkFBNtCK2IhgA", "en", isPriority = true),  // Little Baby Bum
        RssChannel("UChGJGhZ9SOOHvBB0Y4DOO_w", "en", isPriority = true),  // Ryan's World
        RssChannel("UCRijo3ddMTht_IHyNSNXpNQ", "en", isPriority = true),  // Pinkfong Baby Shark
        RssChannel("UCLfsGMeCqT0JBKJFB3cCjMg", "en", isPriority = true),  // Sesame Street
        RssChannel("UCWOA1-mjhKODCJLnGTGWz1A", "en", isPriority = true),  // PBS KIDS
        RssChannel("UCQ00zWTLrgRQJUb8MHQg21A", "en", isPriority = true),  // BabyBus
        RssChannel("UCu7IDy0y-ZA0c8V-xByCePg", "en", isPriority = true),  // Blippi
        RssChannel("UCcdwLMPsaU2ezNSJU1nFoBQ", "en", isPriority = true),  // Hey Bear Sensory
        RssChannel("UC0jJ-kkGBmhH7xyKGfYbJiw", "en", isPriority = true),  // Ms Rachel
        RssChannel("UCJnbFGu2j9I3KFLVA2tFpiA", "en", isPriority = true),  // Morphle TV

        // English — Non-priority
        RssChannel("UC_x5XG1OV2P6uZZ5FSM9Ttw", "en", isPriority = false), // YouTube Learning
        RssChannel("UCLsooMJoIpl_7ux2jvdPB-Q", "en", isPriority = false), // LooLoo Kids
        RssChannel("UCelMeixAOTs2OQAAi9wU8sQ", "en", isPriority = false), // National Geographic Kids
        RssChannel("UCvlVuntLjdURVRunfBMJjyg", "en", isPriority = false), // Art for Kids Hub
        RssChannel("UCBnZ16ahKA2DZ_IEB4n3qMg", "en", isPriority = false), // Cosmic Kids Yoga
        RssChannel("UC295-Dw_tDNtZXFeAPAQKEw", "en", isPriority = false), // SciShow Kids
        RssChannel("UCS4aHVPy84OCNHV6hSRnMHA", "en", isPriority = false), // Khan Academy Kids
        RssChannel("UC6n-7AQuk-WIqVELPsXNHgQ", "en", isPriority = false), // Peppa Pig Official
        RssChannel("UC4KiBA4bE1O0FqJhPbKSj8g", "en", isPriority = false), // Numberblocks

        // Spanish
        RssChannel("UCQMXGjzJsqOsPSPz6lAknBQ", "es", isPriority = true),  // El Reino Infantil
        RssChannel("UCS2wRRZszBsVP64ViVFZliQ", "es", isPriority = false), // Pocoyo Spanish
        RssChannel("UCTVLCj52tBqGjzNuMrjSPUw", "es", isPriority = false), // Cantoalegre
        RssChannel("UCz3JiVHbkrxAJvKdfhT88Aw", "es", isPriority = true),  // La Granja de Zenón

        // Portuguese
        RssChannel("UCVmOmpOWWrn39OnGYsLB_QA", "pt", isPriority = true),  // Galinha Pintadinha
        RssChannel("UC3MdVX6M-O94OBPxu6kGLOA", "pt", isPriority = true),  // Mundo Bita
        RssChannel("UCNye-wNBqNL5ZzHSJj3l8Bg", "pt", isPriority = false), // Turma da Mônica

        // French
        RssChannel("UCh1TPGWZV7Pz8SdXTlEJpRg", "fr", isPriority = true),  // Titounis
        RssChannel("UCyP7ZnfaHarJEQfYjFbMK5A", "fr", isPriority = false), // Comptines et Chansons

        // German
        RssChannel("UCkVVN2C89_4Z0Jp5C8y8M8A", "de", isPriority = true),  // KiKA

        // Russian
        RssChannel("UCczmFDGMFDWDAzBkg0F1D-g", "ru", isPriority = true),  // Маша и Медведь

        // Arabic
        RssChannel("UC6_6mhSYiiuWWvkFp0srUXQ", "ar", isPriority = true),  // Spacetoon

        // Japanese
        RssChannel("UCiY37aN_Xcm0wZFKqxpDcsg", "ja", isPriority = true),  // Kids Line

        // Korean
        RssChannel("UCPVeJykXOm-N0M2ZNqspmtQ", "ko", isPriority = true),  // Pinkfong Korean

        // Hindi
        RssChannel("UCJjLIlVL3gRMA4UhYjnXw3Q", "hi", isPriority = true),  // ChuChu TV Hindi

        // Turkish
        RssChannel("UC6XMuIAoKsfNYXMaLhZfNuQ", "tr", isPriority = true),  // TRT Çocuk

        // Multilingual (match any language)
        RssChannel("UCE08Mv66RJqUhUNbVAhnwWQ", "mul", isPriority = false), // Dave and Ava
        RssChannel("UCT-G02yklWcp3jN3C1xHISQ", "mul", isPriority = false), // Badanamu
    )

    fun getChannelsForLanguages(allowedLanguages: Set<String>): List<RssChannel> {
        val normalizedLanguages = allowedLanguages.map { it.take(2).lowercase() }.toSet()
        return channels.filter { channel ->
            channel.languageCode == "mul" || channel.languageCode in normalizedLanguages
        }
    }

    fun getLanguageForChannel(channelId: String): String? {
        return channels.find { it.channelId == channelId }?.languageCode
    }
}
