package com.example.voterhub.ui.screens


import com.example.voterhub.R
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.voterhub.domain.model.AgeRange
import com.example.voterhub.domain.model.Gender
import com.example.voterhub.domain.model.GenderFilter
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.model.Voter
import com.example.voterhub.ui.components.StaggeredAnimatedItem
import com.example.voterhub.ui.components.VoterDetailBottomSheet
import com.example.voterhub.ui.state.VoterListUiState
import com.example.voterhub.ui.theme.AmberGlow
import com.example.voterhub.ui.theme.AquaTeal
import com.example.voterhub.ui.theme.BackgroundWhite
import com.example.voterhub.ui.theme.DarkGray
import com.example.voterhub.ui.theme.DeepNavy
import com.example.voterhub.ui.theme.GentleLavender
import com.example.voterhub.ui.theme.IndiaGreen
import com.example.voterhub.ui.theme.LightGray
import com.example.voterhub.ui.theme.MediumGray
import com.example.voterhub.ui.theme.NavyBlue
import com.example.voterhub.ui.theme.PromotionBlue
import com.example.voterhub.ui.theme.PromotionRed
import com.example.voterhub.ui.theme.PromotionYellow
import com.example.voterhub.ui.theme.Saffron
import com.example.voterhub.ui.theme.SoftCream
import com.example.voterhub.ui.theme.SurfaceVariant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun VoterListScreen(
    state: VoterListUiState,
    onSearchQueryChange: (String) -> Unit,
    onRecentSearchSelected: (String) -> Unit,
    onAgeRangeSelected: (AgeRange?) -> Unit,
    onGenderFilterSelected: (GenderFilter) -> Unit,
    onClearFilters: () -> Unit,
    onToggleFilterSheet: (Boolean) -> Unit,
    onLoadNextPage: () -> Unit,
    onPullToRefresh: () -> Unit,
    onCustomAgeRangeSelected: (String, String) -> Unit,
    onClearCustomAgeRangeError: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = onPullToRefresh
    )
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            state.hasMore && lastVisible >= layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) onLoadNextPage()
    }

    var selectedVoter by remember { mutableStateOf<Voter?>(null) }

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
            VoterListTopBar(
                hasActiveFilters = state.filterState.activeFilterCount > 0,
                onRefresh = onPullToRefresh,
                onOpenFilters = { onToggleFilterSheet(true) },
                onBackClick = onBackClick
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
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .background(backdrop)
                    ) {
                        SearchBar(
                            query = state.filterState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onClear = { onSearchQueryChange("") },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
                
                if (state.isLoading && state.voters.isEmpty()) {
                    // Show loading spinner while voters are being fetched
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
                } else {
                    itemsIndexed(state.voters, key = { _, voter -> voter.id }) { index, voter ->
                        if (!state.isRefreshing) {
                            StaggeredAnimatedItem(index = index) {
                                VoterCard(
                                    voter = voter,
                                    onClick = { selectedVoter = voter }
                                )
                            }
                        }
                    }
                    if (state.isLoading || state.isRefreshing) {
                        item { LoadingState() }
                    }
                    if (!state.isLoading && !state.isRefreshing && state.voters.isEmpty()) {
                        item { EmptyState() }
                    }
                    if (!state.isLoading && state.errorMessage != null) {
                        item { ErrorState(message = state.errorMessage, onRetry = onPullToRefresh) }
                    }
                }
            }
            
            // Back to Top Button
            val showBackToTop by remember {
                derivedStateOf { listState.firstVisibleItemIndex > 2 }
            }
            
            
            AnimatedVisibility(
                visible = showBackToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                val coroutineScope = rememberCoroutineScope()
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = stringResource(id = R.string.back_to_top)
                    )
                }
            }
        }
    }

    if (state.isFilterSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onToggleFilterSheet(false) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            FilterSheetContent(
                filterState = state.filterState,
                onAgeRangeSelected = onAgeRangeSelected,
                onGenderSelected = onGenderFilterSelected,
                onApply = { onToggleFilterSheet(false) },
                onClear = onClearFilters,
                onCustomAgeRangeSelected = onCustomAgeRangeSelected,
                customAgeRangeError = state.customAgeRangeError,
                onClearCustomAgeRangeError = onClearCustomAgeRangeError
            )
        }
    }

    selectedVoter?.let { voter ->
        VoterDetailBottomSheet(voter = voter, onDismiss = { selectedVoter = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoterListTopBar(
    hasActiveFilters: Boolean,
    onRefresh: () -> Unit,
    onOpenFilters: () -> Unit,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.voter_list_title), style = MaterialTheme.typography.titleLarge, color = DeepNavy)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = DeepNavy
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
            Box {
                IconButton(onClick = onOpenFilters) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = stringResource(id = R.string.filters),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .background(MaterialTheme.colorScheme.error, CircleShape)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}

@Composable
private fun SearchSection(
    query: String,
    recentSearches: List<String>,
    filterState: com.example.voterhub.domain.model.FilterState,
    onQueryChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onClearSearch: () -> Unit,
    onClearAge: () -> Unit,
    onClearGender: () -> Unit,
    onRecentSearchSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(8.dp, MaterialTheme.shapes.extraLarge, clip = false),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(20.dp)) {
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onClear = onClearSearch
            )
            if (recentSearches.isNotEmpty()) {
                RecentSearchesRow(
                    searches = recentSearches,
                    onSearchSelected = onRecentSearchSelected
                )
            }
            ActiveFiltersRow(
                filterState = filterState,
                onClearFilters = onClearFilters,
                onClearSearch = onClearSearch,
                onClearAge = onClearAge,
                onClearGender = onClearGender
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, MaterialTheme.shapes.extraLarge, clip = false),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(id = R.string.search_here)) },
            leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(id = R.string.search_here),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = stringResource(id = R.string.clear_search)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { }),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentSearchesRow(
    searches: List<String>,
    onSearchSelected: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        searches.forEach { query ->
            AssistChip(
                onClick = { onSearchSelected(query) },
                label = { Text(query) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_recent_history),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFiltersRow(
    filterState: com.example.voterhub.domain.model.FilterState,
    onClearFilters: () -> Unit,
    onClearSearch: () -> Unit,
    onClearAge: () -> Unit,
    onClearGender: () -> Unit
) {
    if (filterState.activeFilterCount == 0) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "${filterState.activeFilterCount} filters applied",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            if (filterState.searchQuery.isNotBlank()) {
                FilterBadge(label = stringResource(id = R.string.search_filter_label, filterState.searchQuery), onClear = onClearSearch)
            }
            filterState.ageRange?.let { range ->
                FilterBadge(label = stringResource(id = R.string.age_filter_label, range.label), onClear = onClearAge)
            }
            if (filterState.genderFilter is GenderFilter.Specific) {
                val genderLabel = (filterState.genderFilter as GenderFilter.Specific).gender.name
                FilterBadge(label = stringResource(id = R.string.gender_filter_label, genderLabel), onClear = onClearGender)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onClearFilters) {
            Text(stringResource(id = R.string.clear_all_filters))
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBadge(label: String, onClear: () -> Unit) {
    FilterChip(
        selected = true,
        onClick = onClear,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun VoterCard(
    voter: Voter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)) {
            // BJP Logo Watermark
            Image(
                painter = painterResource(R.drawable.bjp_logo),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.08f),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
            
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarBubble(initials = voter.fullName.firstOrNull()?.uppercaseChar()?.toString().orEmpty())
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(voter.fullName, style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(id = R.string.years_old, voter.age),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    GenderBadge(gender = voter.gender)
                }
                Spacer(Modifier.height(12.dp))
                InfoRow(icon = Icons.Outlined.Place, label = voter.area)
                voter.houseNumber?.let {
                    InfoRow(icon = Icons.Outlined.Home, label = stringResource(id = R.string.house_no_label, it))
                }
                voter.voterId?.let {
                    InfoRow(icon = Icons.Outlined.Badge, label = stringResource(id = R.string.voter_id_label, it))
                }
                voter.relativeName?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun AvatarBubble(initials: String) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                brush = Brush.linearGradient(listOf(AquaTeal, MaterialTheme.colorScheme.tertiary)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun GenderBadge(gender: Gender) {
    val (bg, text) = when (gender) {
        Gender.Male -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        Gender.Female -> AmberGlow to DeepNavy
        Gender.Other -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
    ) {
        Text(
            text = gender.name.uppercase(Locale.getDefault()),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = text
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        repeat(6) {
            SkeletonVoterCard()
        }
    }
}

@Composable
private fun SkeletonVoterCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar skeleton
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Name skeleton
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(Modifier.height(8.dp))
                    // Age skeleton
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
                // Gender badge skeleton
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect()
                )
            }
            Spacer(Modifier.height(12.dp))
            // Info rows skeletons
            repeat(3) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(if (it == 0) 180.dp else if (it == 1) 100.dp else 80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "Shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "ShimmerOffset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFEBEBEB),
                Color(0xFFF8F8F8),
                Color(0xFFEBEBEB),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(id = R.string.no_voters_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(id = R.string.try_adjusting_filters),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.error_message),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(id = R.string.retry))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterSheetContent(
    filterState: com.example.voterhub.domain.model.FilterState,
    onAgeRangeSelected: (AgeRange?) -> Unit,
    onGenderSelected: (GenderFilter) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onCustomAgeRangeSelected: (String, String) -> Unit,
    customAgeRangeError: Int?,
    onClearCustomAgeRangeError: () -> Unit
) {
    var customMin by rememberSaveable { mutableStateOf("") }
    var customMax by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(filterState.ageRange) {
        if (filterState.ageRange != null && filterState.ageRange.label.contains("Custom")) {
            customMin = filterState.ageRange.min?.toString().orEmpty()
            customMax = filterState.ageRange.max?.toString().orEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(stringResource(id = R.string.filter_by_age), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        val ageRanges = com.example.voterhub.ui.viewmodel.VoterListViewModel.AgeRanges
        FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            ageRanges.forEach { range ->
                ElevatedFilterChip(
                    selected = filterState.ageRange == range,
                    onClick = { onAgeRangeSelected(range) },
                    label = { Text(range.label) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            ElevatedFilterChip(
                selected = filterState.ageRange == null,
                onClick = { onAgeRangeSelected(null) },
                label = { Text(stringResource(id = R.string.all) + " ages") }
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(stringResource(id = R.string.custom_range), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = customMin,
                onValueChange = { 
                    customMin = it.filter { char -> char.isDigit() }
                    onClearCustomAgeRangeError()
                },
                label = { Text(stringResource(id = R.string.min_age)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = customMax,
                onValueChange = { 
                    customMax = it.filter { char -> char.isDigit() }
                    onClearCustomAgeRangeError()
                },
                label = { Text(stringResource(id = R.string.max_age)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        customAgeRangeError?.let {
            Text(stringResource(id = it), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.OutlinedButton(onClick = {
            onCustomAgeRangeSelected(customMin, customMax)
        }) {
            Text(stringResource(id = R.string.apply_custom_range))
        }
        Spacer(Modifier.height(24.dp))
        Text(stringResource(id = R.string.filter_by_gender), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            GenderChip(
                text = stringResource(id = R.string.all),
                selected = filterState.genderFilter is GenderFilter.All,
                onClick = { onGenderSelected(GenderFilter.All) }
            )
            GenderChip(
                text = stringResource(id = R.string.male),
                selected = filterState.genderFilter == GenderFilter.Specific(Gender.Male),
                onClick = { onGenderSelected(GenderFilter.Specific(Gender.Male)) }
            )
            GenderChip(
                text = stringResource(id = R.string.female),
                selected = filterState.genderFilter == GenderFilter.Specific(Gender.Female),
                onClick = { onGenderSelected(GenderFilter.Specific(Gender.Female)) }
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(id = R.string.reset))
            }
            androidx.compose.material3.Button(
                onClick = onApply,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(id = R.string.done))
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderChip(text: String, selected: Boolean, onClick: () -> Unit) {
    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}

