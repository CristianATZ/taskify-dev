package com.devtorres.taskalarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.DateFilter
import com.devtorres.taskalarm.data.model.Filters
import com.devtorres.taskalarm.data.model.StatusFilter
import com.devtorres.taskalarm.data.model.TypeFilter

@Composable
fun FiltersColumn(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Column {
        // tipos
        FilterTypeRow(filters, updateFilters)

        // estatus
        FilterStatusRow(filters, updateFilters)

        // fecha
        FilterDateRow(filters, updateFilters)
    }
}

@Composable
fun FilterTypeRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // todas
        item { FilterChip(
            selected = filters.type == TypeFilter.ALL,
            onClick = {
                updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
            },
            label = { Text(text = stringResource(id = R.string.fchAll)) },
            leadingIcon = {
                if (filters.type == TypeFilter.ALL) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        ) }

        // sin fecha
        item {
            FilterChip(
                selected = filters.type == TypeFilter.NOREMINDER,
                onClick = {
                    if (filters.type == TypeFilter.NOREMINDER) {
                        updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                    } else {
                        updateFilters(TypeFilter.NOREMINDER, StatusFilter.NONE, DateFilter.NONE)
                    }
                },
                label = { Text(text = stringResource(id = R.string.fchNoReminder)) },
                leadingIcon = {
                    if (filters.type == TypeFilter.NOREMINDER) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                    }
                }
            )
        }

        // con fecha
        item {
            FilterChip(
                selected = filters.type == TypeFilter.REMINDER,
                onClick = {
                    if (filters.type == TypeFilter.REMINDER) {
                        updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                    } else {
                        updateFilters(TypeFilter.REMINDER, StatusFilter.NONE, DateFilter.NONE)
                    }
                },
                label = { Text(text = stringResource(id = R.string.fchReminder)) },
                leadingIcon = {
                    if (filters.type == TypeFilter.REMINDER) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                    }
                }
            )
        }

        item {
            FilterChip(
                selected = filters.type == TypeFilter.EXPIRED,
                onClick = {
                    if (filters.type == TypeFilter.EXPIRED) {
                        updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                    } else {
                        updateFilters(TypeFilter.EXPIRED, StatusFilter.NONE, DateFilter.NONE)
                    }
                },
                label = { Text(text = stringResource(id = R.string.fchExpired)) },
                leadingIcon = {
                    if (filters.type == TypeFilter.EXPIRED) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Composable
fun FilterStatusRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // completadas
        FilterChip(
            selected = filters.status == StatusFilter.COMPLETED,
            onClick = {
                if (filters.status == StatusFilter.COMPLETED) {
                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, filters.date)
                } else {
                    updateFilters(TypeFilter.ALL, StatusFilter.COMPLETED, filters.date)
                }
            },
            label = { Text(text = stringResource(id = R.string.fchCompleted)) },
            leadingIcon = {
                if (filters.status == StatusFilter.COMPLETED) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        )

        // sin completar
        FilterChip(
            selected = filters.status == StatusFilter.UNCOMPLETED,
            onClick = {
                if (filters.status == StatusFilter.UNCOMPLETED) {
                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, filters.date)
                } else {
                    updateFilters(TypeFilter.ALL, StatusFilter.UNCOMPLETED, filters.date)
                }
            },
            label = { Text(text = stringResource(id = R.string.fchUncompleted)) },
            leadingIcon = {
                if (filters.status == StatusFilter.UNCOMPLETED) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        )
    }
}

@Composable
fun FilterDateRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // hoy
        FilterChip(
            selected = filters.date == DateFilter.TODAY,
            onClick = {
                if (filters.date == DateFilter.TODAY) {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                } else {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.TODAY)
                }
            },
            label = { Text(text = stringResource(id = R.string.fchToday)) },
            leadingIcon = {
                if (filters.date == DateFilter.TODAY) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        )

        // semana
        FilterChip(
            selected = filters.date == DateFilter.WEEK,
            onClick = {
                if (filters.date == DateFilter.WEEK) {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                } else {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.WEEK)
                }
            },
            label = { Text(text = stringResource(id = R.string.fchWeek)) },
            leadingIcon = {
                if (filters.date == DateFilter.WEEK) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        )

        // mes
        FilterChip(
            selected = filters.date == DateFilter.MONTH,
            onClick = {
                if (filters.date == DateFilter.MONTH) {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                } else {
                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.MONTH)
                }
            },
            label = { Text(text = stringResource(id = R.string.fchMonth)) },
            leadingIcon = {
                if (filters.date == DateFilter.MONTH) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        )
    }
}