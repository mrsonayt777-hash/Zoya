package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.remote.MasterSystemPrompt

data class WorkflowTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val mode: MasterSystemPrompt.Mode,
    val promptTemplate: String,
    val icon: ImageVector
)

@Composable
fun WorkflowsScreen(
    onSelectWorkflow: (prompt: String, mode: MasterSystemPrompt.Mode) -> Unit,
    modifier: Modifier = Modifier
) {
    val workflows = listOf(
        WorkflowTemplate(
            id = "code_arch",
            title = "Architectural Code Review",
            subtitle = "Evaluate correctness, security, maintainability, and clean patterns.",
            mode = MasterSystemPrompt.Mode.CODING,
            promptTemplate = "Review this codebase architecture for security, maintainability, and concurrency safety:\n\n",
            icon = Icons.Default.Code
        ),
        WorkflowTemplate(
            id = "bug_diag",
            title = "Root Cause Bug Diagnostic",
            subtitle = "Isolate failure facts vs assumptions and propose smallest effective fix.",
            mode = MasterSystemPrompt.Mode.TROUBLESHOOTING,
            promptTemplate = "Diagnose the following issue, isolating likely cause from assumptions and providing the minimal fix:\n\n",
            icon = Icons.Default.Build
        ),
        WorkflowTemplate(
            id = "project_decomp",
            title = "Autonomous Project Decomposer",
            subtitle = "Decompose any objective into Requirements, Architecture, Implementation, and Testing phases.",
            mode = MasterSystemPrompt.Mode.PROJECT,
            promptTemplate = "Decompose this project goal across all lifecycle phases: ",
            icon = Icons.Default.AccountTree
        ),
        WorkflowTemplate(
            id = "deep_concept",
            title = "First-Principles Conceptual Breakdown",
            subtitle = "Explain complex mechanisms step-by-step with clear analogies.",
            mode = MasterSystemPrompt.Mode.EXPLANATION,
            promptTemplate = "Explain the fundamental first principles and mechanics behind: ",
            icon = Icons.Default.School
        ),
        WorkflowTemplate(
            id = "research_tradeoffs",
            title = "Analytical Research & Tradeoffs",
            subtitle = "Distinguish verified facts from estimates with structured comparison.",
            mode = MasterSystemPrompt.Mode.RESEARCH,
            promptTemplate = "Compare the architectural tradeoffs, costs, and risks between: ",
            icon = Icons.Default.Search
        ),
        WorkflowTemplate(
            id = "automation_pipeline",
            title = "Automation Pipeline Generator",
            subtitle = "Design repeatable multi-stage ingestion, transformation, and verification pipelines.",
            mode = MasterSystemPrompt.Mode.AUTOMATION,
            promptTemplate = "Design an automated execution pipeline for: ",
            icon = Icons.Default.AltRoute
        ),
        WorkflowTemplate(
            id = "creative_pitch",
            title = "Creative Vision & Pitch",
            subtitle = "Craft compelling positioning, messaging, and narrative concepts.",
            mode = MasterSystemPrompt.Mode.CREATIVE,
            promptTemplate = "Develop a high-impact narrative and value proposition for: ",
            icon = Icons.Default.AutoAwesome
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Automated Workflows (Section 28)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pre-configured multi-step intelligence templates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(workflows, key = { it.id }) { wf ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectWorkflow(wf.promptTemplate, wf.mode) }
                        .testTag("workflow_card_${wf.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = wf.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = wf.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = wf.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Run",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
