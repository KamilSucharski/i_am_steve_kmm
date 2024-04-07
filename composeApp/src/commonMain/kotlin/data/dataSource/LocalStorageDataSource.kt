package data.dataSource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.Consts

class LocalStorageDataSource {

    val settings = Settings()
    private val filesDirectory = SystemTemporaryDirectory //todo this should be permanent storage instead

    fun containsEntry(key: String): Boolean {
        return settings.contains(key)
    }

    fun containsFile(key: String): Boolean {
        val path = Path(filesDirectory, key)
        return SystemFileSystem.exists(path)
    }

    fun removeEntry(key: String) {
        settings.remove(key)
    }

    fun removeFile(key: String) {
        val path = Path(filesDirectory, key)
        SystemFileSystem.delete(path = path, mustExist = false)
    }

    fun putBoolean(key: String, boolean: Boolean): Boolean {
        settings.putBoolean(key, boolean)
        return boolean
    }

    fun putInt(key: String, int: Int): Int {
        settings.putInt(key, int)
        return int
    }

    fun putLong(key: String, long: Long): Long {
        settings.putLong(key, long)
        return long
    }

    fun putString(key: String, string: String): String {
        settings.putString(key, string)
        return string
    }

    inline fun <reified T> putSerializable(key: String, serializable: T): String {
        val serializedValue = Json.encodeToString(serializable)
        settings.putString(key, serializedValue)
        return serializedValue
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun putFile(key: String, byteArray: ByteArray) {
        val path = Path(filesDirectory, key)

        SystemFileSystem.delete(path = path, mustExist = false)

        SystemFileSystem.sink(path).use { sink ->
            val buffer = Buffer()
            buffer.write(byteArray)
            sink.write(buffer, buffer.size)
        }
    }

    fun getBoolean(key: String): Boolean? {
        return if (settings.contains(key)) {
            settings.getBoolean(key, false)
        } else {
            null
        }
    }

    fun getInt(key: String): Int? {
        return if (settings.contains(key)) {
            settings.getInt(key, 0)
        } else {
            null
        }
    }

    fun getLong(key: String): Long? {
        return if (settings.contains(key)) {
            settings.getLong(key, 0)
        } else {
            null
        }
    }

    fun getString(key: String): String? {
        return if (settings.contains(key)) {
            settings.getString(key, Consts.EMPTY)
        } else {
            null
        }
    }

    inline fun <reified T> getSerializable(key: String): T? {
        return if (settings.contains(key)) {
            val serializedValue = settings.getString(key, Consts.EMPTY)
            Json.decodeFromString<T>(serializedValue)
        } else {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getFile(key: String): ByteArray? {
        val path = Path(filesDirectory, key)

        if (!SystemFileSystem.exists(path)) {
            return null
        }

        SystemFileSystem.source(path).use { source ->
            return source.buffered().readByteArray()
        }
    }

}