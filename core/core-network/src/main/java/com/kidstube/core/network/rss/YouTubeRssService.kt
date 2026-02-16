package com.kidstube.core.network.rss

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeRssService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchChannelFeed(channelId: String): List<RssVideoEntry> {
        return try {
            val url = "https://www.youtube.com/feeds/videos.xml?channel_id=$channelId"
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string() ?: return emptyList()
            parseAtomFeed(body, channelId)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch RSS for channel $channelId", e)
            emptyList()
        }
    }

    private fun parseAtomFeed(xml: String, channelId: String): List<RssVideoEntry> {
        val entries = mutableListOf<RssVideoEntry>()
        var feedTitle = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var insideEntry = false
            var videoId = ""
            var title = ""
            var description = ""
            var thumbnailUrl = ""
            var publishedAt = ""
            var currentTag = ""
            var currentNamespace = ""

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        currentNamespace = parser.namespace ?: ""

                        if (currentTag == "entry") {
                            insideEntry = true
                            videoId = ""
                            title = ""
                            description = ""
                            thumbnailUrl = ""
                            publishedAt = ""
                        }

                        if (insideEntry) {
                            // <yt:videoId>
                            if (currentTag == "videoId" && currentNamespace.contains("youtube")) {
                                // text will be read in TEXT event
                            }
                            // <media:thumbnail url="..."/>
                            if (currentTag == "thumbnail" && currentNamespace.contains("search.yahoo")) {
                                thumbnailUrl = parser.getAttributeValue(null, "url") ?: ""
                            }
                        }
                    }

                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            if (insideEntry) {
                                when {
                                    currentTag == "videoId" && currentNamespace.contains("youtube") -> videoId = text
                                    currentTag == "title" -> title = text
                                    currentTag == "description" && currentNamespace.contains("search.yahoo") -> description = text
                                    currentTag == "published" -> publishedAt = text
                                }
                            } else {
                                if (currentTag == "title" && feedTitle.isEmpty()) {
                                    feedTitle = text
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "entry" && insideEntry) {
                            if (videoId.isNotEmpty()) {
                                if (thumbnailUrl.isEmpty()) {
                                    thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                                }
                                entries.add(
                                    RssVideoEntry(
                                        videoId = videoId,
                                        title = title,
                                        description = description,
                                        thumbnailUrl = thumbnailUrl,
                                        channelId = channelId,
                                        channelTitle = feedTitle,
                                        publishedAt = publishedAt
                                    )
                                )
                            }
                            insideEntry = false
                        }
                        currentTag = ""
                        currentNamespace = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse RSS XML for channel $channelId", e)
        }

        return entries
    }

    companion object {
        private const val TAG = "YouTubeRssService"
    }
}
