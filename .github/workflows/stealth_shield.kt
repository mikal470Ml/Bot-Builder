package com.stealth.bot.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Random

class StealthShield {

    private val TAG = "StealthShield"

    // ढाल चालू है या नहीं
    private var isShieldActive = true

    // मैनेजर ने पूरी तरह बंद किया है या नहीं
    private var isManuallyLocked = false

    // गलती कितनी बार हुई (इतिहास)
    private var errorCount = 0

    // बेतरतीब संख्या
    private val random = Random()

    // Timer
    private val handler = Handler(Looper.getMainLooper())

    // निचली सीमा: 100 मिलीसेकंड
    private val MIN_DELAY = 100L

    // ऊपरी सीमा: 30 दिन (1 महीना)
    private val MAX_DELAY = 2592000000L


    // =========================================================
    // 1. जाँच: क्या सिस्टम सुरक्षित है?
    // =========================================================
    fun validateEnvironment(): Boolean {
        if (isManuallyLocked) {
            Log.w(TAG, "Shield is manually LOCKED. Waiting for manager to unlock.")
            return false
        }
        if (!isShieldActive) {
            Log.w(TAG, "Shield Status: Autonomous preservation active. Please wait.")
            return false
        }
        Log.d(TAG, "Environment Check Passed: System is stable.")
        return true
    }


    // =========================================================
    // 2. गलती आने पर: गंभीरता के साथ संभालना
    // =========================================================
    fun handleUnexpectedEvent(exception: Exception, severityLevel: Int) {
        Log.e(TAG, "Anomaly detected: ${exception.message} | Severity: $severityLevel")
        triggerAutonomousPause(severityLevel)
    }


    // =========================================================
    // 3. स्वायत्त रोक: समय खुद तय करना
    // =========================================================
    private fun triggerAutonomousPause(severity: Int) {
        isShieldActive = false
        errorCount++

        val waitTime = calculateAutonomousWaitTime(severity, errorCount)

        // अगर समय -1 है, तो मतलब पूरी तरह बंद (मैनेजर तक)
        if (waitTime == -1L) {
            isManuallyLocked = true
            Log.w(TAG, "Severity $severity exceeded limit. System LOCKED until manager unlocks.")
            return
        }

        Log.w(TAG, "Autonomous pause: Severity=$severity, ErrorCount=$errorCount, WaitTime=${waitTime / 1000}s")

        handler.postDelayed({
            isShieldActive = true
            Log.d(TAG, "Autonomous pause resolved after ${waitTime / 1000}s. Shield reactivated.")
        }, waitTime)
    }


    // =========================================================
    // 4. दिमाग: समय तय करने का फॉर्मूला
    // =========================================================
    private fun calculateAutonomousWaitTime(severity: Int, count: Int): Long {

        // चरण 1: गंभीरता से आधार समय चुनो
        val baseDelay: Long = when (severity) {
            1 -> 100L              // 100 मिलीसेकंड
            2 -> 1000L             // 1 सेकंड
            3 -> 10000L            // 10 सेकंड
            4 -> 60000L            // 1 मिनट
            5 -> 600000L           // 10 मिनट
            6 -> 3600000L          // 1 घंटा
            7 -> 86400000L         // 1 दिन
            8 -> 2592000000L       // 1 महीना
            else -> return -1L     // 9+ → पूरी तरह बंद (मैनेजर तक)
        }

        // चरण 2: बार-बार गलती पर दुगुना
        val multiplier = Math.pow(2.0, (count - 1).toDouble()).toLong()
        var finalDelay = baseDelay * multiplier

        // चरण 3: थोड़ा रैंडम (0 से 20%)
        val randomFactor = 1.0 + (random.nextInt(20) / 100.0)
        finalDelay = (finalDelay * randomFactor).toLong()

        // चरण 4: सीमा (100ms से 1 महीना)
        if (finalDelay < MIN_DELAY) finalDelay = MIN_DELAY
        if (finalDelay > MAX_DELAY) finalDelay = MAX_DELAY

        return finalDelay
    }


    // =========================================================
    // 5. सफलता पर: इतिहास रीसेट
    // =========================================================
    fun onSuccess() {
        if (errorCount > 0) {
            Log.d(TAG, "Success detected. Resetting error count from $errorCount to 0.")
        }
        errorCount = 0
    }


    // =========================================================
    // 6. मैनेजर के लिए: खुद चालू करना (Unlock)
    // =========================================================
    fun unlockByManager() {
        isManuallyLocked = false
        isShieldActive = true
        errorCount = 0
        Log.d(TAG, "Manager unlocked the system. Shield fully reactivated.")
    }

    fun lockByManager() {
        isManuallyLocked = true
        isShieldActive = false
        Log.w(TAG, "Manager locked the system manually.")
    }
}