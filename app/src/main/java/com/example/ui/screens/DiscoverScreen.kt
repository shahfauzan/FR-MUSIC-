package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.MainViewModel
import com.example.ui.theme.FRBackground
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hindi", "English", "Punjabi")

    val hindiCategories = listOf(
        Category("hi_bolly", "Bollywood", "Hindi", 0xFFE11D48, 0xFF9F1239, listOf("Item Hits", "Blockbusters")),
        Category("hi_pop", "Hindi Pop", "Hindi", 0xFF8B5CF6, 0xFF5B21B6, listOf("Urban Hindi", "Club Pop")),
        Category("hi_rom", "Romantic", "Hindi", 0xFFEC4899, 0xFFBE185D, listOf("Love Anthems", "Acoustic Love")),
        Category("hi_class", "Classical & Sufi", "Hindi", 0xFFF59E0B, 0xFFB45309, listOf("Ghazals", "Sufi Soul")),
        Category("hi_indie", "Indie Hindi", "Hindi", 0xFF10B981, 0xFF047857, listOf("Independent Bands", "Folk Fusion")),
        Category("hi_trend", "Trending Hindi", "Hindi", 0xFF06B6D4, 0xFF0E7490, listOf("Reel Anthems", "Viral Songs"))
    )

    val englishCategories = listOf(
        Category("en_pop", "Pop", "English", 0xFF3B82F6, 0xFF1E40AF, listOf("Top 40", "Dance Pop")),
        Category("en_hiphop", "Hip-Hop", "English", 0xFFF97316, 0xFFC2410C, listOf("Trap", "Old School")),
        Category("en_rnb", "R&B", "English", 0xFF6366F1, 0xFF4338CA, listOf("Modern Soul", "Chill R&B")),
        Category("en_rock", "Rock", "English", 0xFFEF4444, 0xFF991B1B, listOf("Classic Rock", "Alternative")),
        Category("en_electro", "Electronic", "English", 0xFF14B8A6, 0xFF0F766E, listOf("EDM", "House")),
        Category("en_lofi", "Lo-fi & Chill", "English", 0xFF8B5CF6, 0xFF4C1D95, listOf("Study Beats", "Rainy Moods"))
    )

    val punjabiCategories = listOf(
        Category("pb_pop", "Punjabi Pop", "Punjabi", 0xFFD946EF, 0xFF86198F, listOf("Bhangra Beats", "Chartbusters")),
        Category("pb_bhangra", "Bhangra", "Punjabi", 0xFFEAB308, 0xFFA16207, listOf("Dhol Anthems", "Wedding Hits")),
        Category("pb_hiphop", "Punjabi Hip-Hop", "Punjabi", 0xFFF43F5E, 0xFF9F1239, listOf("Moosetape", "Brown Wave")),
        Category("pb_trend", "Trending Punjabi", "Punjabi", 0xFF0EA5E9, 0xFF0369A1, listOf("Viral Swag", "Car Bass"))
    )

    val currentCategories = when (selectedTabIndex) {
        0 -> hindiCategories
        1 -> englishCategories
        else -> punjabiCategories
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("discover_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Discover & Genres",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 24.sp
                )
            )
            Text(
                text = "Explore deep curated vibes across Hindi, English, and Punjabi cultures",
                style = MaterialTheme.typography.bodySmall.copy(color = FRTextSecondary)
            )
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FRBackground,
            contentColor = FRCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = FRCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) FRCyan else FRTextMuted,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(currentCategories) { cat ->
                CategoryGradientCard(
                    category = cat,
                    onClick = { viewModel.loadCategoryTracks(cat.name) }
                )
            }
        }
    }
}

@Composable
fun CategoryGradientCard(
    category: Category,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(category.gradientStartHex),
                        Color(category.gradientEndHex)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("category_card_${category.id}"),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = category.subcategories.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                ),
                maxLines = 2
            )
        }
    }
}
