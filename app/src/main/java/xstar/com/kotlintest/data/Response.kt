package xstar.com.kotlintest.data

/**
 * @author xstar
 * @since 5/21/17.
 */
data class Response<T>(val error_code: String,
                    val reason: String,
                    val resultcode: String,
                    val result: T)
