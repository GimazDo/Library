package ru.gimaz.library.ui.icons.libraryicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.gimaz.library.ui.icons.LibraryIcons

public val LibraryIcons.Writer: ImageVector
    get() {
        if (`_writer-svgrepo-com` != null) {
            return `_writer-svgrepo-com`!!
        }
        `_writer-svgrepo-com` = Builder(name = "Writer-svgrepo-com", defaultWidth = 800.0.dp,
                defaultHeight = 800.0.dp, viewportWidth = 511.999f, viewportHeight =
                511.999f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(489.194f, 474.529f)
                horizontalLineToRelative(-377.58f)
                lineToRelative(55.961f, -23.531f)
                curveToRelative(2.258f, -0.903f, 4.648f, -2.525f, 6.251f, -4.133f)
                lineToRelative(332.686f, -332.686f)
                curveToRelative(7.315f, -7.314f, 7.315f, -19.174f, 0.0f, -26.49f)
                lineTo(424.316f, 5.493f)
                curveToRelative(-7.314f, -7.314f, -19.175f, -7.314f, -26.49f, 0.0f)
                curveTo(391.77f, 11.549f, 74.956f, 328.364f, 65.14f, 338.179f)
                curveToRelative(-1.696f, 1.693f, -3.284f, 4.12f, -4.132f, 6.247f)
                lineTo(1.527f, 485.883f)
                curveToRelative(-5.281f, 12.299f, 3.787f, 26.109f, 17.218f, 26.109f)
                horizontalLineToRelative(470.449f)
                curveToRelative(10.345f, 0.0f, 18.731f, -8.387f, 18.731f, -18.731f)
                reflectiveCurveTo(499.538f, 474.529f, 489.194f, 474.529f)
                close()
                moveTo(411.071f, 45.226f)
                lineToRelative(55.707f, 55.707f)
                lineToRelative(-40.787f, 40.787f)
                lineToRelative(-55.707f, -55.707f)
                lineTo(411.071f, 45.226f)
                close()
                moveTo(343.795f, 112.503f)
                lineToRelative(55.707f, 55.707f)
                lineTo(160.582f, 407.13f)
                lineToRelative(-55.707f, -55.707f)
                lineTo(343.795f, 112.503f)
                close()
                moveTo(84.848f, 384.376f)
                lineToRelative(42.78f, 42.781f)
                lineToRelative(-73.82f, 31.04f)
                lineTo(84.848f, 384.376f)
                close()
            }
        }
        .build()
        return `_writer-svgrepo-com`!!
    }

private var `_writer-svgrepo-com`: ImageVector? = null
