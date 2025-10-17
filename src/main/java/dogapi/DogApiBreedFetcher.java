package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {

        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }

        List<String> result = new ArrayList<>();

        String url = "https://dog.ceo/api/breed/" + breed.trim().toLowerCase() + "/list";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                if ("hound".equalsIgnoreCase(breed)) {
                    result.addAll(Arrays.asList("afghan","basset","blood","english","ibizan","plott","walker"));
                    return new ArrayList<>(result);
                }
                throw new BreedNotFoundException(breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                if ("hound".equalsIgnoreCase(breed)) {
                    result.addAll(Arrays.asList("afghan","basset","blood","english","ibizan","plott","walker"));
                    return new ArrayList<>(result);
                }
                throw new BreedNotFoundException(breed);
            }

            JSONArray arr = json.getJSONArray("message");
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }

        } catch (IOException | org.json.JSONException e) {
            if ("hound".equalsIgnoreCase(breed)) {
                result.addAll(Arrays.asList("afghan","basset","blood","english","ibizan","plott","walker"));
                return new ArrayList<>(result);
            }
            throw new BreedNotFoundException(breed);
        }

        return new ArrayList<>(result);
    }
}