package postpc2021.android.socialar.favoritesComponents
import java.io.Serializable
import java.util.*

class FavoriteItem: Serializable{
    private var content_summary = "Example"
    private var uuid: UUID = UUID.randomUUID()
    private var user_name = "user_name"

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
}