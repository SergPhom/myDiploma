package ru.netology.nework.entity


import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Job
import javax.persistence.*

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
    fun toDto(myId: Long) = Job(id, name, position, start, finish, link)

    companion object {
        fun fromDto(dto: Job, myId: Long) = JobEntity(
            dto.id,
            dto.name,
            dto.position,
            dto.start,
            dto.finish,
            dto.link,
        )
    }
}
