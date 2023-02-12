package com.example.majorproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate kafkaTemplate;

    public String addUser(UserRequest userRequest) {

        User user = User.builder().userName(userRequest.getUserName()).age(userRequest.getAge()).mobile(userRequest.getMobile()).email(userRequest.getEmail()).build();


        //Save it to the db
        userRepository.save(user);
        //Save it in the cache
        saveInCache(user);

        kafkaTemplate.send("create_wallet",user.getUserName());

        return "User Added successfully";


    }

    public void saveInCache(User user){

        Map map = objectMapper.convertValue(user,Map.class);

        String key = "User"+user.getUserName();
        System.out.println("The user key is "+key);
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(key, Duration.ofHours(12));
    }


    public User findUserByUserName(String userName) {

        //logic
        //1. find in the redis cache
        Map map = redisTemplate.opsForHash().entries(userName);

        User user = null;
        //If not found in the redis/map
        if(map==null){

            //Find the userObject from the userRepo
            user = userRepository.findByUserName(userName);
            //Save that found user in the cache
            saveInCache(user);
            return user;
        }else{
            //We found the User object
            user = objectMapper.convertValue(map, User.class);
            return user;
        }
    }

    public UserResponseDto findEmailAndNameDto(String userName) {

        //H.W try changing the code : and integrate this part with redis
        Map map = redisTemplate.opsForHash().entries(userName);

        User user = null;
        //If not found in the redis/map
        if(map==null){

            //Find the userObject from the userRepo
            user = userRepository.findByUserName(userName);
            //Save that found user in the cache
            saveInCache(user);
        }else{
            //We found the User object
            user = objectMapper.convertValue(map, User.class);
        }

        UserResponseDto userResponseDto = UserResponseDto.builder().email(user.getEmail()).name(user.getName()).build();

        return userResponseDto;
    }
}
