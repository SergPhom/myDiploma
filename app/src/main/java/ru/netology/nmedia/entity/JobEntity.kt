package ru.netology.nework.entity


import androidx.room.*
import androidx.room.Entity
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.UserJob

@Entity
data class JobEntity(
    @PrimaryKey
    var id: Long,
    var name: String,
    var position: String,
    /**
     * Дата и время начала работы
     */
    var start: Long,
    /**
     * Дата и время окончания работы
     */
    var finish: Long? = null,
    /**
     * Ссылка на веб-сайт организации
     */
    var link: String? = null,
) {
    fun toDto() = UserJob(id, name, position, start, finish, link)

    companion object {
        fun fromDto(dto: UserJob): JobEntity{
            val jobEntity = JobEntity(
                dto.id,
                dto.name,
                dto.position,
                dto.start,
                dto.finish,
                dto.link,
            )
            println("job entity from dto is $jobEntity")
            return jobEntity
        }
    }
}
fun List<JobEntity>.toDto(myId: Long): List<UserJob> = map { it.toDto() }

fun List<UserJob>.fromDto(): List<JobEntity> = map(JobEntity::fromDto)