package ruangrupakoding.spring.helper

import java.time.Instant

class BufferData<T>(
    private val key: String,
    private val type: Class<T>,
    private val loader: () -> T,
    private val ttlSeconds: Long = DEFAULT_TTL
) {
    companion object {
        const val DEFAULT_TTL = 300L
    }

    private var value: T? = null
    private var expireAt: Instant? = null

    fun load(): T {
        val now = Instant.now()
        if (value == null || expireAt == null || now.isAfter(expireAt)) {
            value = loader()
            expireAt = now.plusSeconds(ttlSeconds)
        }
        return value!!
    }

    fun clear() {
        value = null
        expireAt = null
    }

    fun isExpired(): Boolean {
        return expireAt?.let { Instant.now().isAfter(it) } ?: true
    }
}

class BufferManager(private val ttlSeconds: Long = 60L) {
    private val buffers = mutableMapOf<String, BufferData<*>>()

    fun <T> register(key: String, type: Class<T>, loader: () -> T): BufferData<T> {
        val buffer = BufferData(key, type, loader, ttlSeconds)
        buffers[key] = buffer
        return buffer
    }

    fun getBuffer(key: String): BufferData<*>? = buffers[key]

    fun clearAll() = buffers.values.forEach { it.clear() }
}