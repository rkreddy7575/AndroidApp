package com.trailbook.social;

import com.trailbook.common.ResourceNotFoundException;
import com.trailbook.experience.Experience;
import com.trailbook.experience.ExperienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final ExperienceRepository experienceRepository;

    public LikeService(LikeRepository likeRepository, ExperienceRepository experienceRepository) {
        this.likeRepository = likeRepository;
        this.experienceRepository = experienceRepository;
    }

    @Transactional
    public void like(UUID userId, UUID experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        if (likeRepository.existsByUserIdAndExperienceId(userId, experienceId)) {
            return;
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setExperienceId(experienceId);
        likeRepository.save(like);
        experience.setLikeCount(experience.getLikeCount() + 1);
        experienceRepository.save(experience);
    }

    @Transactional
    public void unlike(UUID userId, UUID experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        if (!likeRepository.existsByUserIdAndExperienceId(userId, experienceId)) {
            return;
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setExperienceId(experienceId);
        likeRepository.delete(like);
        experience.setLikeCount(Math.max(0, experience.getLikeCount() - 1));
        experienceRepository.save(experience);
    }
}
