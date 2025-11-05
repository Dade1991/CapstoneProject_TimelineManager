package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Comment_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.entities.Comment;

import java.time.LocalDate;

public record CommentResponse(Long commentId,
                              String commentText,
                              Long userId,
                              String userName,
                              Long taskId,
                              LocalDate createdAt,
                              LocalDate updatedAt
) {

    // converte la "commentEntity" in "commentResponse"

    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getCommentText(),
                comment.getUser().getUserId(),
                comment.getUser().getFullName(),
                comment.getTask().getTaskId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}