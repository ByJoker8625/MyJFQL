import de.byjoker.myjfql.util.Json.stringify
import java.util.*
import kotlin.collections.HashMap

fun main() {
    stringify("a")

    var l = System.currentTimeMillis()
    HashMap<String, String>()
    l = System.currentTimeMillis() - l
    println(l)
}
