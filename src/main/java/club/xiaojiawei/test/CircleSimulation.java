package club.xiaojiawei.test;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/28 9:31
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CircleSimulation extends Application {

    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 30;
    private static final int CIRCLE_RADIUS = 4;

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        // Center coordinates of the circle
        double centerX = GRID_SIZE / 2.0;
        double centerY = GRID_SIZE / 2.0;

        // Draw the grid with rectangles colored based on circle simulation
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE);

                // Check if the cell is inside the simulated circle
                if (isInsideCircle(col, row, centerX, centerY, CIRCLE_RADIUS)) {
                    rectangle.setFill(Color.BLUE);
                } else {
                    rectangle.setFill(Color.WHITE);
                }

                gridPane.add(rectangle, col, row);
            }
        }

        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Circle Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Check if a given cell is inside the simulated circle
    private boolean isInsideCircle(int x, int y, double centerX, double centerY, double radius) {
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return distance <= radius;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
