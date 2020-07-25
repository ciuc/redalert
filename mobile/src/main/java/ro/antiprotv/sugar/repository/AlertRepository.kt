package ro.antiprotv.sugar.repository

import ro.antiprotv.sugar.repository.db.Alert
import ro.antiprotv.sugar.repository.db.AlertDao
import ro.antiprotv.sugar.repository.db.AlertType

/**
 * The ROOM alert repo
 */
class AlertRepository(private val alertDao: AlertDao) {

    fun insert(alert: Alert) = alertDao.insert(alert)

    fun getAll() = alertDao.allAlerts()

    fun update(alert: Alert, type: AlertType) {
        alert.type = type
        alertDao.updateAlerts(alert)
    }

    fun update(alert: Alert) = alertDao.updateAlerts(alert)

    fun removeAll() = alertDao.removeAll()
}