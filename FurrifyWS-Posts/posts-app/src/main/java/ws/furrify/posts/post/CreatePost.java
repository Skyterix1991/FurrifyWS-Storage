package ws.furrify.posts.post;

import ws.furrify.posts.post.dto.PostDTO;

import java.util.UUID;

interface CreatePost {

    UUID createPost(UUID userId, PostDTO postDTO);
}
