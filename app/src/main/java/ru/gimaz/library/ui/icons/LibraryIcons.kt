package ru.gimaz.library.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import ru.gimaz.library.ui.icons.libraryicons.Book
import ru.gimaz.library.ui.icons.libraryicons.Writer
import ru.gimaz.library.ui.icons.libraryicons.Publisher
import kotlin.collections.List as ____KtList

public object LibraryIcons

private var __AllIcons: ____KtList<ImageVector>? = null

public val LibraryIcons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Writer, Book, Publisher)
    return __AllIcons!!
  }
