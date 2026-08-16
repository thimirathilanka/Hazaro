package com.example.hazaro.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hazaro.R
import com.example.hazaro.data.model.Report
import com.example.hazaro.ui.util.formatReportTime

@Composable
fun ReportDetailSheet(report: Report) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text(report.type.label) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatReportTime(report.createdAt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (report.photoUrl.isNotBlank()) {
            AsyncImage(
                model = report.photoUrl,
                contentDescription = report.type.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(18.dp)),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = report.description.ifBlank { stringResource(R.string.no_description) },
            style = MaterialTheme.typography.bodyLarge,
        )
        if (report.reporterEmail.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.reported_by, report.reporterEmail),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
