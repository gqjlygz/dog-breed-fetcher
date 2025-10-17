package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private int callsMade = 0;
    private final Map<String, List<String>> cache = new HashMap<>();
    private final BreedFetcher fetcher;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = (breed == null) ? null : breed.trim().toLowerCase(Locale.ROOT);

        if (key != null && cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        try {
            callsMade++;
            List<String> fetched = fetcher.getSubBreeds(breed);
            if (key != null) {
                cache.put(key, Collections.unmodifiableList(new ArrayList<>(fetched)));
            }
            return new ArrayList<>(fetched);
        } catch (BreedNotFoundException e) {
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}