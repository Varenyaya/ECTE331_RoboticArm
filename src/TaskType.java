/**
 * Defines fixed identity tokens for the concurrent tasks in the robotic arm system.
 * Replaces loose string checking with strong types, allowing the resource monitors 
 * to identify critical tasks safely and handle metrics tracking accurately.
 */
public enum TaskType {
    // Represents the background logging thread (Low Priority)
    LOGGER,

    // Represents the independent trajectory tracking thread (Medium Priority)
    MOTION_PLANNER,

    // Represents the critical emergency monitoring thread (High Priority)
    SAFETY_MONITOR
}