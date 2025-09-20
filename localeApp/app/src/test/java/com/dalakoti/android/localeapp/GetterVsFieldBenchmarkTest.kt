package com.dalakoti.android.localeapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetterVsFieldBenchmarkTest {

    interface Accessor { fun getX(): Int }

    class Base(var x: Int)

    private data class BenchmarkData(
        val bases: Array<Base>,
        val accessors: Array<Accessor>,
        val mask: Int
    )

    private data class Measurement(
        val timeNs: Long,
        val sum: Int
    )

    @Test
    fun benchmarkFieldAccessVsGetterCalls() {
        val data = createData(NUM_TYPES)

        val warm = warmup(data, ITERATIONS, WARMUP_ROUNDS, WARMUP_DIVISOR)
        blackhole = blackhole xor warm.toInt()

        val field = measureField(data, ITERATIONS)
        val getter = measureGetter(data, ITERATIONS)
        blackhole = blackhole xor field.sum
        blackhole = blackhole xor getter.sum

        assertEquals(field.sum, getter.sum)

        val ratio = computeRatio(getter.timeNs, field.timeNs)
        printResults(field.timeNs, getter.timeNs, ratio)
        println("\nratio is $ratio")
        assertGetterSlower(ratio)
    }

    private fun createData(numTypes: Int): BenchmarkData {
        val bases = Array(numTypes) { i -> Base(i) }
        @Suppress("UNCHECKED_CAST")
        val accessors = arrayOfNulls<Accessor>(numTypes).also { arr ->
            for (i in 0 until numTypes) {
                val b = bases[i]
                arr[i] = object : Accessor { override fun getX(): Int = b.x }
            }
        } as Array<Accessor>
        return BenchmarkData(bases = bases, accessors = accessors, mask = bases.size - 1)
    }

    private fun warmup(data: BenchmarkData, iterations: Int, rounds: Int, divisor: Int): Long {
        var warm = 0L
        for (r in 0 until rounds) {
            for (i in 0 until iterations / divisor) {
                val idx = i and data.mask
                warm += data.bases[idx].x
                warm += data.accessors[idx].getX()
            }
        }
        return warm
    }

    private fun measureField(data: BenchmarkData, iterations: Int): Measurement {
        val start = System.nanoTime()
        var sum = 0
        for (i in 0 until iterations) {
            val idx = i and data.mask
            sum += data.bases[idx].x
        }
        val time = System.nanoTime() - start
        return Measurement(timeNs = time, sum = sum)
    }

    private fun measureGetter(data: BenchmarkData, iterations: Int): Measurement {
        val start = System.nanoTime()
        var sum = 0
        for (i in 0 until iterations) {
            val idx = i and data.mask
            sum += data.accessors[idx].getX()
        }
        val time = System.nanoTime() - start
        return Measurement(timeNs = time, sum = sum)
    }

    private fun computeRatio(getterNs: Long, fieldNs: Long): Double = getterNs.toDouble() / fieldNs.toDouble()

    private fun printResults(fieldNs: Long, getterNs: Long, ratio: Double) {
        println("Field ns=$fieldNs, Getter ns=$getterNs, ratio=${ratio}x")
    }

    private fun assertGetterSlower(ratio: Double) {
        assertTrue(
            "Expected getter calls to be slower than direct field access, but ratio was ${ratio}x",
            ratio > 1.0
        )
    }

    companion object {
        private const val NUM_TYPES = 32
        private const val ITERATIONS = 20_000_000
        private const val WARMUP_ROUNDS = 5
        private const val WARMUP_DIVISOR = 10

        @Volatile
        private var blackhole: Int = 0
    }
}


