package com.ferid.app.classroom.interfaces;

/**
 * Created by ferid.cafer on 3/9/2015.
 */
public interface BackNavigationListener {
    /**
     * Date
     * @param dayOfMonth
     * @param month
     * @param year
     */
    void OnPress(int dayOfMonth, int month, int year);

    /**
     * Time
     * @param minute
     * @param hour
     */
    void OnPress(int minute, int hour);
}
