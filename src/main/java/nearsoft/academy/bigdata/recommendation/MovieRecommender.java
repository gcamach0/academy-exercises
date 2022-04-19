package nearsoft.academy.bigdata.recommendation;

import java.io.*;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.List;
import java.util.ArrayList;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

public class MovieRecommender {
    String dataPath;
    int totalUsers;
    int totalProducts;
    int totalReviews;

    // We need to assign a numeric value to pair with the ID's of both users and products to be able to write them in the data model
    Hashtable<String, Integer> usersHash;
    Hashtable<String, Integer> productsHash;
    // We need to be able to retrieve product id in its original format for the recommendations list
    Hashtable<Integer, String> productsIds;

    DataModel model;
    UserSimilarity similarity;
    UserNeighborhood neighborhood;
    UserBasedRecommender recommender;


    MovieRecommender(String dataPath) throws IOException, TasteException {
        this.dataPath = dataPath;
        this.totalUsers = 0;
        this.totalProducts = 0;
        this.totalReviews = 0;

        this.usersHash = new Hashtable<String, Integer>();
        this.productsHash = new Hashtable<String, Integer>();
        this.productsIds = new Hashtable<Integer, String>();

        processDataset();
        buildRecommender();
    }

    public int getTotalReviews() {
        return this.totalReviews;
    }

    public int getTotalUsers() {
        return this.totalUsers;
    }

    public int getTotalProducts() {
        return this.totalProducts;
    }

    public List<String> getRecommendationsForUser(String user) throws TasteException {
        List<String> recommendations = new ArrayList<String>();
        int userId = usersHash.get(user);
        int amountOfRecommendations = 3;

        for (RecommendedItem recommendation : this.recommender.recommend(userId, amountOfRecommendations)) {
            int recommendationID = (int) recommendation.getItemID();
            String productId = this.productsIds.get(recommendationID);
            recommendations.add(productId);
        }
        return recommendations;
    }

    private void processDataset() throws IOException {
        BufferedReader textSource = this.unzipDataset();
        BufferedWriter csvFile = new BufferedWriter(new FileWriter("data/movies.csv"));

        String productId = "";
        String score = "";
        String userId = "";
        String line = textSource.readLine();

        // Traverse the text source
        while (line != null) {
            if (line.contains("product/productId:")) {
                productId = line.split(" ")[1];
                if (this.productsHash.get(productId) == null) {
                    this.totalProducts++;
                    this.productsIds.put(this.totalProducts, productId);
                    this.productsHash.put(productId, this.totalProducts);
                }
            } else if (line.contains("review/userId:")) {
                userId = line.split(" ")[1];
                if (this.usersHash.get(userId) == null) {
                    this.totalUsers++;
                    this.usersHash.put(userId, this.totalUsers);
                }
            } else if (line.contains("review/score:")) {
                score = line.split(" ")[1];
                this.totalReviews++;
            }
            // Write the csv data model once we have all the needed values
            if ((userId != "") && (productId != "") && (score != "")) {
                csvFile.write(this.usersHash.get(userId) + "," + this.productsHash.get(productId) + "," + score + "\n");
                productId = "";
                score = "";
                userId = "";
            }
            line = textSource.readLine();
        }

        textSource.close();
        csvFile.close();
    }

    private void buildRecommender() throws IOException, TasteException {
        this.model = new FileDataModel(new File("data/movies.csv"));
        this.similarity = new PearsonCorrelationSimilarity(this.model);
        this.neighborhood = new ThresholdUserNeighborhood(0.1, this.similarity, this.model);
        this.recommender = new GenericUserBasedRecommender(this.model, this.neighborhood, this.similarity);
    }

    private BufferedReader unzipDataset() throws IOException {
        InputStream inputStream = new FileInputStream(this.dataPath);
        InputStream gzipStream = new GZIPInputStream(inputStream);
        Reader reader = new InputStreamReader(gzipStream);
        return new BufferedReader(reader);
    }

}