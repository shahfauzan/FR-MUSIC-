package com.example.data.remote

enum class ProviderType {
    AUDIUS_FULL_STREAM,
    YOUTUBE_DATA_API,
    LEGAL_API_PREVIEWS,
    CURATED_DEMO_OFFLINE
}

class MusicProviderFactory {
    private val demoProvider = DemoMusicProvider()
    private val audiusProvider = AudiusMusicProvider(demoProvider)
    private val legalProvider = LegalPreviewMusicProvider(demoProvider)
    private val youtubeProvider = YouTubeMusicProvider(audiusProvider)

    fun getProvider(type: ProviderType): MusicProvider {
        return when (type) {
            ProviderType.AUDIUS_FULL_STREAM -> audiusProvider
            ProviderType.YOUTUBE_DATA_API -> youtubeProvider
            ProviderType.LEGAL_API_PREVIEWS -> legalProvider
            ProviderType.CURATED_DEMO_OFFLINE -> demoProvider
        }
    }
}
