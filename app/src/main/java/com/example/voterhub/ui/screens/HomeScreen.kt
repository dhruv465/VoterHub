package com.example.voterhub.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.voterhub.R
import com.example.voterhub.domain.model.Prabhag
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.ui.components.StaggeredAnimatedItem
import com.example.voterhub.ui.state.VoterListUiState
import com.example.voterhub.ui.theme.AmberGlow
import com.example.voterhub.ui.theme.AquaTeal
import com.example.voterhub.ui.theme.BackgroundWhite
import com.example.voterhub.ui.theme.DeepNavy
import com.example.voterhub.ui.theme.GentleLavender
import com.example.voterhub.ui.theme.IndiaGreen
import com.example.voterhub.ui.theme.PromotionBlue
import com.example.voterhub.ui.theme.PromotionRed
import com.example.voterhub.ui.theme.PromotionYellow
import com.example.voterhub.ui.theme.SoftCream
import com.example.voterhub.ui.viewmodel.HomeUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onSectionSelected: (String) -> Unit,
    onVillageSelected: (String, String) -> Unit, // sectionId, prabhagId
    onPullToRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = onPullToRefresh
    )

    val backdrop = remember {
        Brush.verticalGradient(
            listOf(
                SoftCream,
                BackgroundWhite
            )
        )
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                onRefresh = onPullToRefresh
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(backdrop)
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (state.isLoading && state.sections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.sections.isNotEmpty()) {
                    item {
                        StaggeredAnimatedItem(index = 0) {
                            PromotionBanner()
                        }
                    }
                    item {
                        StaggeredAnimatedItem(index = 1) {
                            HeroHeader(
                                sections = state.sections,
                                selectedSectionId = state.selectedSectionId,
                                total = state.sections.find { it.id == state.selectedSectionId }?.totalVoters ?: 0,
                                demographics = state.sectionDemographics,
                                onSectionSelected = onSectionSelected
                            )
                        }
                    }
                    
                    // Village Grid
                    state.selectedSectionId?.let { sectionId ->
                        val selectedSection = state.sections.find { it.id == sectionId }
                        selectedSection?.prabhags?.let { prabhags ->
                            if (prabhags.isNotEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.select_village), // You might need to add this string resource
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DeepNavy,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                                item {
                                    VillageGrid(
                                        prabhags = prabhags,
                                        onVillageClick = { prabhagId ->
                                            onVillageSelected(sectionId, prabhagId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onRefresh: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(id = R.string.voter_hub_title), style = MaterialTheme.typography.titleLarge, color = DeepNavy)
                Text(
                    text = stringResource(id = R.string.voter_hub_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(id = R.string.refresh),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VillageGrid(
    prabhags: List<Prabhag>,
    onVillageClick: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 2
    ) {
        prabhags.take(12).forEachIndexed { index, prabhag ->
            StaggeredAnimatedItem(index = index + 2) { // Start index after header
                VillageCard(
                    prabhag = prabhag,
                    onClick = { onVillageClick(prabhag.id) },
                    modifier = Modifier.fillMaxWidth(0.48f) // Roughly half width minus spacing
                )
            }
        }
    }
}

@Composable
private fun VillageCard(
    prabhag: Prabhag,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = prabhag.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepNavy,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${prabhag.totalVoters} Voters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun HeroHeader(
    sections: List<Section>,
    selectedSectionId: String?,
    total: Int,
    demographics: SectionDemographics?,
    onSectionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(stringResource(id = R.string.voter_overview), style = MaterialTheme.typography.labelLarge, color = DeepNavy)
            Text(
                text = "%,d".format(total),
                style = MaterialTheme.typography.displayLarge,
                color = DeepNavy
            )
            Spacer(Modifier.height(12.dp))
            if (demographics != null) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    QuickStatChip(stringResource(id = R.string.male), "${demographics.malePercentage}%", AmberGlow)
                    QuickStatChip(stringResource(id = R.string.female), "${demographics.femalePercentage}%", GentleLavender)
                    QuickStatChip(stringResource(id = R.string.avg_age), "${demographics.avgAge}", AquaTeal)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Sections tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    val isSelected = section.id == selectedSectionId
                    Card(
                        onClick = {
                            onSectionSelected(section.id)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) IndiaGreen else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 8.dp else 2.dp,
                            pressedElevation = 4.dp
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                section.name,
                                color = if (isSelected) Color.White else DeepNavy,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromotionBanner() {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier
        .height(200.dp)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val color = when (page) {
                0 -> PromotionRed
                1 -> PromotionBlue
                else -> PromotionYellow
            }
            val text = when (page) {
                0 -> stringResource(id = R.string.promotion_banner_1)
                1 -> stringResource(id = R.string.promotion_banner_2)
                else -> stringResource(id = R.string.promotion_banner_3)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Pager Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickStatChip(label: String, value: String, chipColor: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Surface(
            color = chipColor,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 0.dp
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = DeepNavy
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = DeepNavy
        )
    }
}
