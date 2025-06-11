package com.example.onlybuns.services.bloomfilter;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.example.onlybuns.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class UsernameBloomFilter {

    private BloomFilter<String> bloomFilter;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        long userCount = userRepository.count();
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                userCount == 0 ? 1000 : userCount,
                0.01
        );

        userRepository.findAllUsernames().forEach(bloomFilter::put);
    }

    public boolean mightContain(String username) {
        return bloomFilter.mightContain(username);
    }

    public void add(String username) {
        bloomFilter.put(username);
    }
}
