package io.aethibo.features.comments.presentation

import io.aethibo.features.comments.domain.controller.CommentsController
import io.aethibo.features.comments.domain.model.CommentDTO
import io.aethibo.features.comments.domain.model.CommentsDTO
import io.aethibo.features.comments.domain.service.CommentsService
import io.aethibo.features.users.domain.model.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CommentsControllerImpl(private val commentService: CommentsService) : CommentsController {
    override suspend fun add(call: ApplicationCall) {
        val email = call.authentication.principal<User>()?.email
        require(!email.isNullOrBlank()) { "User not logged in." }

        val slug = call.parameters["slug"]
        require(!slug.isNullOrBlank()) { "Slug must not be empty" }

        val commentDto = call.receive<CommentDTO>()
        require(!commentDto.comment?.body.isNullOrBlank()) { "Body is null" }

        val comment = commentService.add(slug, email, commentDto.comment!!)
        call.respond(CommentDTO(comment))
    }

    override suspend fun findBySlug(call: ApplicationCall) {
        val slug = call.parameters["slug"]
        require(!slug.isNullOrBlank()) { "Slug must not be empty" }

        val comments = commentService.findBySlug(slug)
        call.respond(CommentsDTO(comments))
    }

    override suspend fun delete(call: ApplicationCall) {
        val slug = call.parameters["slug"]
        require(!slug.isNullOrBlank()) { "Slug must not be empty" }

        val id = call.parameters["commentId"]
        require(!id.isNullOrBlank()) { "Comment id must not be empty." }

        commentService.delete(id.toLong(), slug)
    }
}
