package newbie.place_review.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    private final ValueOperations<String, Object> valueOperations;

    public CacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        valueOperations.set(key, value, timeout, timeUnit);
    }

    public Object get(String key) {
        return valueOperations.get(key);
    }

    public Object getAndDelete(String key) {
        return valueOperations.getAndDelete(key);
    }
}
