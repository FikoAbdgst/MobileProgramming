package com.example.pertemuan4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertemuan4.ui.theme.Pertemuan4Theme

val dummyImages = listOf(
    R.drawable.kikuri,
    R.drawable.oke,
    R.drawable.otw
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PostCard(
                        modifier = Modifier.padding(innerPadding),
                        onPostClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun PostImageCarousel(
    images: List<Int>,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Post Image $page",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .clickable(onClick = onImageClick)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(6.dp)
                )
            }
        }
    }
}


@Composable
fun PostCard(modifier: Modifier = Modifier, onPostClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = modifier
                .wrapContentHeight()
                .padding(12.dp)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = modifier
                        .weight(1f)
                        .clickable { onPostClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avv),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Fiko",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "20 minutes ago",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                    }
                }
                var isFollowing by remember { mutableStateOf(false) }
                Button(
                    onClick = {!isFollowing},
                    shape = RoundedCornerShape(20.dp),
                    modifier = modifier
                        .padding(start = 8.dp)
                        .width(120.dp)
                        .height(40.dp)
                ) {
                    Text(text = if (isFollowing) "Following" else "Follow")
                }
            }

            Text(
                text = "Hai semuanya, jgn lupa dateng ke konser aku ya",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(vertical = 5.dp, horizontal = 2.dp)
            )
            Spacer(modifier = modifier.height((8.dp)))

            PostImageCarousel(
                images = dummyImages,
                onImageClick = onPostClick
            )

            // Image(
            //                painter = painterResource(id = R.drawable.kikuri),
            //                contentDescription = "Post Image",
            //                contentScale = ContentScale.Crop,
            //                modifier = Modifier
            //                    .fillMaxWidth()
            //                    .wrapContentWidth()
            //                    .clip(RoundedCornerShape(12.dp))
            //                    .height(220.dp)
            //                    .clickable(onClick = onPostClick)
            //            )

            Spacer(modifier = modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconLike()
                Spacer(modifier = Modifier.width(8.dp))
                CommentButton()
                Spacer(modifier = Modifier.width(8.dp))
                ShareButton()
            }
            Spacer(modifier = modifier.height(12.dp))
        }
    }
}


@Composable
fun IconLike(modifier: Modifier = Modifier) {
    var isLiked by remember { mutableStateOf(false) }

    Button(
        onClick = { !isLiked },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLiked) Color.Red else MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Like",
            modifier = modifier.padding(start = 4.dp),
            tint = Color.White
        )
        Text(
            text = if (isLiked) "Disukai" else "Suka",
            modifier = modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun CommentButton() {
    IconButton(onClick = {  }) {
        Icon(
            imageVector = Icons.Default.MailOutline,
            contentDescription = "Comment",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ShareButton() {
    IconButton(onClick = {  }) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    Pertemuan4Theme {
        PostCard(onPostClick = { })
    }
}