/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto;

/**
 * FIXME: this should be in yearly.
 * 
 * @author coder65535
 */
public enum AutoStartPositionEnum {
    UNKNOWN(0, "unknown", "Unknown Position"),
    CENTER_BACK_PYRAMID(1, "insidePyramidBackCenter", "Inside pyramid, back center"),
    POSITION2(2, "unknown", "Unknown Position"),
    BACK_LEFT_PYRAMID_INSIDE(3, "insidePyramidBackLeft", "Inside pyramid, back left"),
    BACK_RIGHT_PYRAMID_INSIDE(4, "insidePyramidBackRight", "Inside Pyramid, back right"),
    BACK_RIGHT_PYRAMID_OUTSIDE(5, "outsidePyramidBackRight", "Outside Pyramid, back right"),
    BACK_LEFT_PYRAMID_OUTSIDE(6, "outsidePyramidBackLeft", "Outside Pyramid, back left"),
    POSITION7(7, "unknown", "Unknown Position"),
    POSITION8(8, "unknown", "Unknown Position"),
    POSITION9(9, "unknown", "Unknown Position");

    private int index;
    private String description, configName;

    private AutoStartPositionEnum(int index, String configName, String description) {
        this.configName = configName;
        this.index = index;
        this.description = description;
    }

    /**
     * Converts the enum type to a String.
     *
     * @return A string representing the enum.
     */
    @Override
    public String toString() {
        return description;
    }

    public String toConfigString() {
        return configName;
    }

    /**
     * Converts the enum type to a numeric value.
     *
     * @return An integer representing the enum.
     */
    public int toValue() {
        return index;
    }

    public static AutoStartPositionEnum getEnumFromValue(int i) {
        for (AutoStartPositionEnum position : AutoStartPositionEnum.values()) {
            if (position.toValue() == i) {
                return position;
            }
        }
        return null;
    }
}
