import androidx.room.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val location: String,
    val category: String,
    val repeat: String,
    val reminder: String
)

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): List<Event>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)
}

@Database(entities = [Event::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}