/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
import java.io.File
import java.lang.RuntimeException
import java.util.*

fun getLocalProperty(propertyName: String): String = with(File("local.properties")) {
    when{
        exists() -> Properties().run {
            load(inputStream())
            getProperty(propertyName)
        } ?: throw RuntimeException("Property \"$propertyName\" is missing from \'local.properties\'.")
        else -> throw RuntimeException("\'local.properties\' file is not found.")
    }
}