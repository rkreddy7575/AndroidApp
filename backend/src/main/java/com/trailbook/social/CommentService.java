package com.trailbook.social;

import com.trailbook.common.BadRequestException;
import com.trailbook.common.ResourceNotFoundException;
import com.trailbook.experience.Experience;
import com.trailbook.experience.ExperienceRepository;
import com.trailbook.user.User;
import com.trailbook.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            ExperienceRepository experienceRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    public Page<CommentResponse> getComments(UUID experienceId, Pageable pageable) {
        return commentRepository.findByExperienceIdOrderByCreatedAtDesc(experienceId, pageable)
                .map(CommentResponse::from);
    }

    @Transactional
    public CommentResponse addComment(UUID userId, UUID experienceId, String content) {
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Comment cannot be empty");
        }
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setExperienceId(experienceId);
        comment.setUser(user);
        comment.setContent(content.trim());
        commentRepository.save(comment);

        experience.setCommentCount(experience.getCommentCount() + 1);
        experienceRepository.save(experience);

        return CommentResponse.from(comment);
    }
}
