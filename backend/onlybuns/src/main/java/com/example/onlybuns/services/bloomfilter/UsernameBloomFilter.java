package com.example.onlybuns.services.bloomfilter;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.example.onlybuns.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
                Math.max(userCount * 2, 1000),
                0.01
        );

        userRepository.findAllUsernames().forEach(bloomFilter::put);
    }

    public boolean mightContain(String username) {
        return bloomFilter != null
                && bloomFilter.mightContain(username);
    }

    public void add(String username) {
        bloomFilter.put(username);
    }

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // every 12h
    public void scheduledRebuild() {
        rebuildBloomFilter();
    }

    private synchronized void rebuildBloomFilter() {
        long userCount = userRepository.count();
        BloomFilter<String> newFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                Math.max(userCount * 2, 1000),
                0.01
        );
        userRepository.findAllUsernames().forEach(newFilter::put);
        this.bloomFilter = newFilter;
    }
}
