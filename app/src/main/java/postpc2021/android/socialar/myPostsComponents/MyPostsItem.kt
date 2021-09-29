package postpc2021.android.socialar.myPostsComponents
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalTime
import java.util.*
import kotlin.random.Random.Default.nextDouble

class MyPostsItem: Serializable{
    private var content_summary = "Example"
    private var uuid: UUID = UUID.randomUUID()
    private var user_name = "user_name"
    private val long: Double = nextDouble()
    private val lat: Double = nextDouble()
    @RequiresApi(Build.VERSION_CODES.O)
    private val ttl: LocalTime = LocalTime.now()
    private var likes: Int = 0

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

    fun setLike(likes: Int)
    {
        this.likes = likes
    }

    fun getLikes(): Int{
        return this.likes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTTL(): LocalTime
    {
        return this.ttl
    }
}