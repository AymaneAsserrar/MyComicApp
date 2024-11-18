import com.project.controller.RecommendationController;
import com.project.model.Comic;

import java.util.List;

public class AppMain {
    public static void main(String[] args) {
        RecommendationController recommendationController = new RecommendationController();
        // this is done by mohamed to test you can remove it

        // Définir la limite des comics à récupérer
        int limit = 20; // Par exemple, récupère 5 comics
        List<Comic> popularComics = recommendationController.getPopularComics(limit);

        // Affichage des résultats pour vérifier l'intégration
        if (popularComics.isEmpty()) {
            System.out.println("Aucune recommandation de comic trouvée.");
        } else {
            for (Comic comic : popularComics) {
                System.out.println(comic);
            }
        }
    }
}
