package com.todoapp.uikit.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDFamilyGroupCard(
    name: String,
    role: String,
    description: String,
    memberCount: Int,
    pendingTaskCount: Int,
    createdDate: String,
    @DrawableRes membersIcon: Int,
    @DrawableRes tasksIcon: Int,
    modifier: Modifier = Modifier,
    onViewDetailsClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    val initials = name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val roleColor = when (role.uppercase()) {
        "ADMIN" -> TDTheme.colors.orange
        else -> TDTheme.colors.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TDTheme.colors.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TDTheme.colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = TDTheme.colors.white,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TDTheme.colors.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .border(1.dp, roleColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = role.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = roleColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "\"$description\"",
                    fontSize = 15.sp,
                    color = TDTheme.colors.statusCardGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBox(
                        iconRes = membersIcon,
                        iconTint = TDTheme.colors.primary,
                        value = "$memberCount",
                        label = "Members",
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        iconRes = tasksIcon,
                        iconTint = TDTheme.colors.primary,
                        value = "$pendingTaskCount",
                        label = "Pending",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Created $createdDate",
                        fontSize = 13.sp,
                        color = TDTheme.colors.statusCardGray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = onViewDetailsClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TDTheme.colors.primary,
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = SolidColor(TDTheme.colors.primary)
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "View Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (role.equals("Admin", ignoreCase = true)) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete group",
                        tint = TDTheme.colors.crossRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    @DrawableRes iconRes: Int,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = TDTheme.colors.bgColorPurple,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TDTheme.colors.onBackground
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TDTheme.colors.statusCardGray
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F5)
@Composable
private fun FamilyGroupCardPreview() {
    TDTheme {
        TDFamilyGroupCard(
            name = "The Smith Family",
            role = "Admin",
            description = "Daily chores, grocery lists, and vacation planning for 2024.",
            memberCount = 5,
            pendingTaskCount = 9,
            createdDate = "Jan 12, 2023",
            membersIcon = R.drawable.ic_members,
            tasksIcon = R.drawable.ic_tasks_done,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun FamilyGroupCardDarkPreview() {
    TDTheme {
        TDFamilyGroupCard(
            name = "Extended Cousins",
            role = "Member",
            description = "Planning the annual reunion, potluck coordination, and secret santa gifts.",
            memberCount = 14,
            pendingTaskCount = 5,
            createdDate = "Mar 05, 2023",
            membersIcon = R.drawable.ic_members,
            tasksIcon = R.drawable.ic_tasks_done,
            modifier = Modifier.padding(16.dp),
        )
    }
}
