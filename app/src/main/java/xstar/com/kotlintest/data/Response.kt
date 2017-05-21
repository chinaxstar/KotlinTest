package xstar.com.kotlintest.data

/**
 * @author xstar
 * @since 5/21/17.
 */
class Response <T>{
    open val error_code:String?=null
    open val reason:String?=null
    open val data:T?=null
}