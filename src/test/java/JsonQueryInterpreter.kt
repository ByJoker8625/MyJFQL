import com.fasterxml.jackson.databind.JsonNode
import de.byjoker.myjfql.util.Json
import java.util.*

fun main() {
    val interpreter = JsonQueryInterpreter(
        Json.parse(
            "{\"id\":\"bedwars_stats\",\"name\":\"bedwars_stats\",\"entries\":[{\"content\":{\"kills\":5,\"wins\":\"7\",\"loses\":\"0\",\"destroyed_beds\":\"6\",\"uuid\":\"a2fdeab2-3e78-4fea-bf96-c19b53b553cd\",\"deaths\":\"0\",\"points\":\"1280\"},\"creation\":1645554727330},{\"content\":{\"kills\":\"0\",\"wins\":\"1\",\"loses\":\"5\",\"destroyed_beds\":\"0\",\"uuid\":\"e08cba46-c439-4ee1-a5c2-77eb4b1d235a\",\"deaths\":\"5\",\"points\":\"400\"},\"creation\":1645554727337},{\"content\":{\"kills\":\"0\",\"wins\":\"0\",\"loses\":\"1\",\"destroyed_beds\":\"0\",\"uuid\":\"5e1440f9-986c-432f-829c-53680789c37e\",\"deaths\":\"1\",\"points\":\"50\"},\"creation\":1646576167112},{\"content\":{\"kills\":\"0\",\"wins\":\"1\",\"loses\":\"0\",\"destroyed_beds\":\"0\",\"uuid\":\"cec7cc02-3c69-4afb-a74b-e262017a39ef\",\"deaths\":\"0\",\"points\":\"150\"},\"creation\":1645561385291}],\"structure\":[\"uuid\",\"destroyed_beds\",\"points\",\"kills\",\"deaths\",\"wins\",\"loses\"],\"primary\":\"uuid\",\"type\":\"RELATIONAL\"}"
        )
    )
    val scanner = Scanner(System.`in`)

    fun loop() {
        try {
            val line = scanner.nextLine()

            println(interpreter.interpret(line, false, superior = false))
            println(interpreter.interpret(line, false, superior = false)!!.javaClass)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        loop()
    }

    loop()
}

//name -> bedwars_stats ($ -> base obj) entries.0.content.kills => entries 0 item of array content field of it and then kills value -> 5

class JsonQueryInterpreter(private val json: JsonNode) {

    fun interpret(query: String, stringify: Boolean, superior: Boolean): Any? {
        val fields = mutableListOf(*query.split(".").toTypedArray())

        if (fields.indexOf("$") == 0) {
            fields.removeAt(0)
        }

        if (superior && fields.isNotEmpty()) {
            fields.removeLast()
        }

        return searchObject(json, fields, stringify)
    }


    private fun searchArray(node: JsonNode, fields: List<String>, stringify: Boolean): Any? {
        return try {
            if (fields.isEmpty()) {
                return node.toString()
            }

            val index = fields[0].toInt()

            val o = node[index] ?: return null

            if (o.isObject && fields.size != 1) {
                return searchObject(o, fields.subList(1, fields.size), stringify)
            }

            if (o.isArray && fields.size != 1) searchArray(
                o,
                fields.subList(1, fields.size),
                stringify
            ) else if (stringify) o.toString() else o
        } catch (ex: Exception) {
            null
        }
    }

    private fun searchObject(node: JsonNode, fields: List<String>, stringify: Boolean): Any? {
        return try {
            if (fields.isEmpty()) {
                return node.toString()
            }

            val o = node[fields[0]] ?: return null

            if (o.isArray && fields.size != 1) {
                return searchArray(o, fields.subList(1, fields.size), stringify)
            }

            if (o.isObject) {
                return if (fields.size != 1) {
                    searchObject(o, fields.subList(1, fields.size), stringify)
                } else if (stringify) o.toString() else o
            }

            if (fields.size != 1) null else if (stringify) o.toString() else o
        } catch (ex: Exception) {
            null
        }
    }

}
