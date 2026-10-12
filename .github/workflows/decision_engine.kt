package com.stealth.bot.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Random

class DecisionEngine {

    private val TAG = "StealthDecisionEngine"
    private val random = Random()
    private val handler = Handler(Looper.getMainLooper())

    fun processTarget(node: AccessibilityNodeInfo, text: String) {
        // Khud tay karne wala fluid pattern generator
        val patternType = selectDynamicPattern()
        executeActionWithPattern(node, patternType, text)
    }

    private fun selectDynamicPattern(): Int {
        // Probability distribution for human-like behavior:
        // Pattern 1: Fast (2-4 sec)
        // Pattern 2: Medium (5-8 sec)
        // Pattern 3: Slow (9-12 sec)
        // Pattern 4: Miss/Skip (Human error / distraction)
        val choice = random.nextInt(100)
        return when {
            choice < 30 -> 1 // Fast
            choice < 70 -> 2 // Medium
            choice < 95 -> 3 // Slow
            else -> 4        // Miss / Skip
        }
    }

    private fun executeActionWithPattern(node: AccessibilityNodeInfo, pattern: Int, text: String) {
        when (pattern) {
            1 -> {
                val delay = 2000 + random.nextInt(2000) // 2 to 4 seconds
                delayedAction(node, delay, "Fast Pattern")
            }
            2 -> {
                val delay = 5000 + random.nextInt(3000) // 5 to 8 seconds
                delayedAction(node, delay, "Medium Pattern")
            }
            3 -> {
                val delay = 9000 + random.nextInt(3000) // 9 to 12 seconds
                delayedAction(node, delay, "Slow Pattern")
            }
            4 -> {
                // Insani galti ya dhyan bhatakna (Miss/Skip)
                Log.d(TAG, "Human Error Simulated: Skipped action naturally for target -> $text")
            }
        }
    }

    private fun delayedAction(node: AccessibilityNodeInfo, delayMillis: Int, patternName: String) {
        handler.postDelayed({
            try {
                // Natural click or interaction trigger
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d(TAG, "Executed: $patternName with delay ${delayMillis}ms")
            } catch (e: Exception) {
                Log.e(TAG, "Execution error: ${e.message}")
            }
        }, delayMillis.toLong())
    }
}
