package edu.Dmitry.geodownloader.api;

import edu.Dmitry.geodownloader.InternetAccess;
import edu.Dmitry.geodownloader.datamodel.InstagramPost;
import edu.Dmitry.geodownloader.exeption.InstagramApiExeption;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

public class InstagramApi {
    private Logger logger = LogManager.getRootLogger();
    final private String instagramPosts = "https://www.instagram.com/p/";
    final private String instagramLocations = "https://www.instagram.com/explore/locations/";

    public Map<Long, String> getLastPostsLinks(long locationId, long postMaxId, long lastPostMaxId, StringBuilder nextPage) throws InstagramApiExeption {
        Map<Long, String> postsLinks = new LinkedHashMap<>();
        int pagesCount = 0;

        try {
            String pageLink;
            if (nextPage.toString().equals("")) {
                pageLink = instagramLocations + locationId;
            } else {
                logger.info("Download more");
                pageLink = nextPage.toString();
                postMaxId = lastPostMaxId;
            }

            if (postMaxId != 0) {
                logger.info("Get last posts links: locationId: " + locationId + ", postMaxId: " + postMaxId);
            } else {
                logger.info("Get last posts links: locationId: " + locationId + ", time: today");
            }

            boolean hasNextPage;
            JSONObject pageInfo;

            do {
                pagesCount++;
                String page = InternetAccess.downloadPage(pageLink);

                String jString = page.split("<script type=\"text/javascript\">window._sharedData = ")[1].split(";</script>")[0];
                JSONObject jObject = new JSONObject(jString);
                JSONObject entryData = jObject.getJSONObject("entry_data");
                JSONArray array = entryData.getJSONArray("LocationsPage");
                JSONObject location = array.getJSONObject(0).getJSONObject("location");
                JSONObject media = location.getJSONObject("media");
                pageInfo = media.getJSONObject("page_info");
                hasNextPage = pageInfo.getBoolean("has_next_page");
                JSONArray nodes = media.getJSONArray("nodes");

                for (int i = 0; i < nodes.length(); i++) {
                    JSONObject post = nodes.getJSONObject(i);

                    long id = post.getLong("id");
                    long date = post.getLong("date") * 1000;
                    LocalDate postDate = new DateTime(date).toLocalDate();
                    LocalDate today = new DateTime().toLocalDate();

                    boolean save;
                    if (postMaxId != 0) {
                        save = id > postMaxId && postDate.compareTo(today) < 2; // id поста больше последнего поста за сегодня или вчера
                    } else {
                        save = postDate.isEqual(today); // пост за сегодня
                    }

                    if (save) {
                        String link = instagramPosts + post.getString("code");
                        logger.info("Add post link: " + link);
                        postsLinks.put(id, link);
                    } else {
                        nextPage.delete(0, nextPage.length());
                        return postsLinks;
                    }
                }

                if (hasNextPage) {
                    pageLink = instagramLocations + locationId + "/?max_id=" + pageInfo.getLong("end_cursor");
                    logger.info("Next page link: " + pageLink);
                }
            } while (hasNextPage && pagesCount != 3);

            nextPage.delete(0, nextPage.length());
            if (hasNextPage) {
                nextPage.append(instagramLocations + locationId + "/?max_id=" + pageInfo.getLong("end_cursor"));
            }

            return postsLinks;
        } catch (HttpResponseException exeption1) {
            logger.error("Downloading error " + exeption1.getMessage());
            throw new InstagramApiExeption("Downloading error", exeption1);
        } catch (UnknownHostException exeption2) {
            logger.error("Internet connection error " + exeption2.getMessage());
            throw new InstagramApiExeption("Internet connection error", exeption2);
        } catch (Exception exeption3) {
            logger.error(exeption3.getMessage());
            throw new InstagramApiExeption("Unknown error", exeption3);
        }
    }

    public InstagramPost getInstagramPost(String postLink) throws InstagramApiExeption {
        logger.info("Get post for link: " + postLink);
        InstagramPost post = new InstagramPost();

        JSONObject media;
        try {
            String page = InternetAccess.downloadPage(postLink);
            String jString = page.split("<script type=\"text/javascript\">window._sharedData = ")[1].split(";</script>")[0];
            JSONObject jObject = new JSONObject(jString);
            JSONObject entryData = jObject.getJSONObject("entry_data");
            JSONArray array = entryData.getJSONArray("PostPage");
            media = array.getJSONObject(0).getJSONObject("media");
        } catch (SocketTimeoutException timeoutException) {
            logger.error("Socket timeout error " + timeoutException.getMessage());
            throw new InstagramApiExeption("Socket timeout error", timeoutException);
        } catch (HttpResponseException exeption1) {
            logger.error("Downloading error " + exeption1.getMessage());
            throw new InstagramApiExeption("Downloading error", exeption1);
        } catch (UnknownHostException exeption2) {
            logger.error("Internet connection error " + exeption2.getMessage());
            throw new InstagramApiExeption("Internet connection error", exeption2);
        } catch (Exception exeption3) {
            logger.error("Error " + exeption3.getMessage());
            throw new InstagramApiExeption("Parsing error", exeption3);
        }

        try {
            post.id = media.getLong("id");
            post.createdTime = new DateTime(media.getLong("date") * 1000);
        } catch (Exception exeption) {
            logger.error(exeption.getMessage());
            throw new InstagramApiExeption("Parsing error", exeption);
        }

        try {
            JSONObject location = media.getJSONObject("location");
            post.locationId = location.getLong("id");
            post.locationName = location.getString("name");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        try {
            JSONObject likes = media.getJSONObject("likes");
            post.likesCount = likes.getInt("count");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        Set<String> usersList = new HashSet<>();

        try {
            JSONObject owner = media.getJSONObject("owner");
            usersList.add(owner.getString("username"));
        } catch (Exception exeption) {
            logger.error(exeption.getMessage());
            throw new InstagramApiExeption("Parsing error", exeption);
        }

        try {
            JSONArray userTags = media.getJSONObject("usertags").getJSONArray("nodes");

            for (int i = 0; i < userTags.length(); i++) {
                usersList.add(userTags.getJSONObject(i).getJSONObject("user").getString("username"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        post.usertags = usersList;

        Set<String> hashTagsList = new HashSet<>();

        try {
            String caption = media.getString("caption");
            Set<String> hashtags = getHashtags(caption);
            if (hashtags != null) {
                hashTagsList.addAll(hashtags);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        try {
            JSONArray comments = media.getJSONObject("comments").getJSONArray("nodes");
            for (int i = 0; i < comments.length(); i++) {
                String comment = comments.getJSONObject(i).getString("text");
                Set<String> hashtags = getHashtags(comment);
                if (hashtags != null) {
                    hashTagsList.addAll(hashtags);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        post.hashtags = hashTagsList;

        return post;
    }

    //Regular expression
    /*
    Matcher matcher = Pattern.compile("(#\\w+|#[а-яА-Я0-9_]+)").matcher(str);
    while(matcher.find()) {
        results.add(matcher.group());
    */
    private Set<String> getHashtags(String str) {
        if (str != null && !str.isEmpty()) {
            if (!str.contains("#")) {
                return null;
            }

            Set<String> results = new HashSet<>();
            String[] tmpStrArgs = str.split(" ");
            for (int i = 0; i < tmpStrArgs.length; i++) {
                String[] tmpArgs = tmpStrArgs[i].split("#");
                for (int j = 1; j < tmpArgs.length; j++) {
                    if (!tmpArgs[j].isEmpty()) {
                        results.add("#" + tmpArgs[j]);
                    }
                }
            }

            if (results.size() == 0) {
                return null;
            }

            return results;
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            new InstagramApi().getInstagramPost("https://www.instagram.com/p/BEMNV_soTfI");
        } catch (InstagramApiExeption instagramApiExeption) {
            instagramApiExeption.printStackTrace();
        }
        //new InstagramApi().getLocationPosts(789292339, -1);
    }
}
