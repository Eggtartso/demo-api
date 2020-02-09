package web_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import web_demo.entity.PostComment;
import web_demo.entity.PostLike;
import web_demo.repository.PostCommentRepository;
import web_demo.repository.PostLikeRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/post")
public class PostAuxController {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @PostMapping("/{id}/isLiked")
    public ResponseEntity<String> isLike (@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PostLike result = postLikeRepository.findFirstByUsernameAndPostId(username, id);

        if (result == null){
            return new ResponseEntity<String>("false", null,HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<String>("true", null,HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike (@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PostLike result = postLikeRepository.findFirstByUsernameAndPostId(username, id);

        if (result == null){
            PostLike postLike = new PostLike(username, (short) 1,id);
            postLikeRepository.save(postLike);
            return new ResponseEntity<String>("liked", null,HttpStatus.ACCEPTED);
        } else {
            postLikeRepository.deleteById(result.getId());
            return new ResponseEntity<String>("unliked", null,HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<String> newComment (@PathVariable Long id, String commentText){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
            PostComment postComment = new PostComment(username, commentText, LocalDateTime.now(), id);
            postCommentRepository.save(postComment);

            return new ResponseEntity<String>("Comment Success", HttpStatus.ACCEPTED);
    }
}
