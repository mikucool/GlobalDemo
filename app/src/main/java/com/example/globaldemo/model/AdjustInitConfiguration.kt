package com.example.globaldemo.model

/**
 * Configuration for initializing Adjust SDK.
 *
 * This sealed class defines various scenarios for when the Adjust SDK should be initialized.
 *
 * Initialization Triggers:
 * - NONE: Do not initialize Adjust.
 * - ON_APP_START: Initialize Adjust when the app starts.
 * - ON_SMALL_WITHDRAWAL_TASK_COMPLETION: Initialize Adjust after a small withdrawal task is completed.
 * - ON_WITHDRAWAL_SCREEN_AFTER_TASK: Initialize Adjust when the user navigates to the withdrawal screen after completing a small withdrawal task.
 * - ON_WITHDRAWAL_BUTTON_CLICK_AFTER_TASK: Initialize Adjust when the user clicks the withdrawal button after completing a small withdrawal task.
 * - ON_WITHDRAWAL_INITIATED: Initialize Adjust when a small withdrawal is initiated.
 * - AFTER_SPECIFIC_AD_VIEWS: Initialize Adjust after a specific number of video ad views.
 *   - If the configuration value is N > 10, Adjust will be initialized after (N - 10) video ad views.
 */
sealed class AdjustInitConfiguration(open val initProbability: Float) {


    /** Do not initialize Adjust. */
    data class None(override val initProbability: Float = 0.0f) :
        AdjustInitConfiguration(initProbability)

    /** Initialize Adjust when the app starts. */
    data class OnAppStart(override val initProbability: Float) :
        AdjustInitConfiguration(initProbability)

    /** Initialize Adjust after a small withdrawal task is completed. */
    data class OnSmallWithdrawalTaskCompletion(override val initProbability: Float) :
        AdjustInitConfiguration(initProbability)

    /** Initialize Adjust when the user navigates to the withdrawal screen after completing a small withdrawal task. */
    data class OnWithdrawalScreenAfterTask(override val initProbability: Float) :
        AdjustInitConfiguration(initProbability)

    /** Initialize Adjust when the user clicks the withdrawal button after completing a small withdrawal task. */
    data class OnWithdrawalButtonClickAfterTask(override val initProbability: Float) :
        AdjustInitConfiguration(initProbability)

    /** Initialize Adjust when a small withdrawal is initiated. */
    data class OnWithdrawalInitiated(override val initProbability: Float) :
        AdjustInitConfiguration(initProbability)

    /**
     * Initialize Adjust after a specific number of video ad views.
     *
     * @property adViewCount The number of ad views required before initialization.
     * @property initProbability The probability (0.0 to 1.0) of initializing Adjust after the specified number of ad views.
     */
    data class AfterSpecificAdViews(val adViewCount: Int, override val initProbability: Float) :
        AdjustInitConfiguration(initProbability) {
        init {
            require(adViewCount >= 0) { "adViewCount must be non-negative" }
            require(initProbability in 0.0..1.0) { "initProbability must be between 0.0 and 1.0" }
        }
    }
}