package postpc2021.android.socialar.favoritesComponents
import java.io.Serializable
import java.util.*
import kotlin.random.Random.Default.nextDouble


class FavoriteItem: Serializable{
    private var content_summary = "Example"
    private var uuid: UUID = UUID.randomUUID()
    private var user_name = "user_name"
    private val long: Double = nextDouble()
    private val lat: Double = nextDouble()

    fun setId(id: UUID) {
        this.uuid = id
    }

    fun getId(): UUID {
        return this.uuid
    }

    fun setContentSummary(contentSummary: String) {
        this.content_summary = contentSummary
    }

    fun setUsername(userName: String) {
        this.user_name = userName
    }

    fun getContent(): String {
        return this.content_summary
    }

    fun getLongitude(): Double
    {
        return this.long
    }

    fun getLatitude(): Double
    {
        return  this.lat
    }
}