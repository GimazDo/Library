package ru.gimaz.library.ui.icons.libraryicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.gimaz.library.ui.icons.LibraryIcons

public val LibraryIcons.Book: ImageVector
    get() {
        if (`_book-svgrepo-com` != null) {
            return `_book-svgrepo-com`!!
        }
        `_book-svgrepo-com` = Builder(name = "Book-svgrepo-com", defaultWidth = 800.0.dp,
                defaultHeight = 800.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.0f, 19.0f)
                verticalLineTo(6.2f)
                curveTo(4.0f, 5.0799f, 4.0f, 4.5198f, 4.218f, 4.092f)
                curveTo(4.4097f, 3.7157f, 4.7157f, 3.4097f, 5.092f, 3.218f)
                curveTo(5.5198f, 3.0f, 6.0799f, 3.0f, 7.2f, 3.0f)
                horizontalLineTo(16.8f)
                curveTo(17.9201f, 3.0f, 18.4802f, 3.0f, 18.908f, 3.218f)
                curveTo(19.2843f, 3.4097f, 19.5903f, 3.7157f, 19.782f, 4.092f)
                curveTo(20.0f, 4.5198f, 20.0f, 5.0799f, 20.0f, 6.2f)
                verticalLineTo(17.0f)
                horizontalLineTo(6.0f)
                curveTo(4.8954f, 17.0f, 4.0f, 17.8954f, 4.0f, 19.0f)
                close()
                moveTo(4.0f, 19.0f)
                curveTo(4.0f, 20.1046f, 4.8954f, 21.0f, 6.0f, 21.0f)
                horizontalLineTo(20.0f)
                moveTo(9.0f, 7.0f)
                horizontalLineTo(15.0f)
                moveTo(9.0f, 11.0f)
                horizontalLineTo(15.0f)
                moveTo(19.0f, 17.0f)
                verticalLineTo(21.0f)
            }
        }
        .build()
        return `_book-svgrepo-com`!!
    }

private var `_book-svgrepo-com`: ImageVector? = null
