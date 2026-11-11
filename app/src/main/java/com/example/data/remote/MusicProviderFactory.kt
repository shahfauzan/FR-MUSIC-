package com.example.data.remote

enum class ProviderType {
    YOUTUBE_DATA_API,
    LEGAL_API_PREVIEWS,
    CURATED_DEMO_OFFLINE
}

class MusicProviderFactory {
    private val demoProvider = DemoMusicProvider()
    private val legalProvider = LegalPreviewMusicProvider(demoProvider)
    private val youtubeProvider = YouTubeMusicProvider(legalProvider)

    fun getProvider(type: ProviderType): MusicProvider {
        return when (type) {
            ProviderType.YOUTUBE_DATA_API -> youtubeProvider
            ProviderType.LEGAL_API_PREVIEWS -> legalProvider
            ProviderType.CURATED_DEMO_OFFLINE -> demoProvider
        }
    }
}
