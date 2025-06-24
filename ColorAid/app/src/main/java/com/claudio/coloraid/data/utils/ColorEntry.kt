package com.claudio.coloraid.data.utils

data class ColorEntry(
    val name: String,
    val r: Int,
    val g: Int,
    val b: Int
) {
    val lab: Triple<Double, Double, Double> by lazy {
        rgbToLab(r, g, b)
    }

    companion object {
        public fun rgbToLab(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
            // Conversão RGB -> XYZ
            fun pivot(c: Double): Double {
                val v = c / 255.0
                return if (v <= 0.04045) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
            }

            val rLin = pivot(r.toDouble())
            val gLin = pivot(g.toDouble())
            val bLin = pivot(b.toDouble())

            val x = (rLin * 0.4124 + gLin * 0.3576 + bLin * 0.1805) / 0.95047
            val y = (rLin * 0.2126 + gLin * 0.7152 + bLin * 0.0722) / 1.00000
            val z = (rLin * 0.0193 + gLin * 0.1192 + bLin * 0.9505) / 1.08883

            // Conversão XYZ -> LAB
            fun f(t: Double): Double =
                if (t > 0.008856) Math.cbrt(t) else (7.787 * t + 16.0 / 116.0)

            val fx = f(x)
            val fy = f(y)
            val fz = f(z)

            val l = 116.0 * fy - 16.0
            val a = 500.0 * (fx - fy)
            val b = 200.0 * (fy - fz)

            return Triple(l, a, b)
        }
    }
}