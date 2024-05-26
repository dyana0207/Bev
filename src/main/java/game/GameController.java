package game;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Setter;
import model.Direction;
import model.SoldierState;
import org.tinylog.Logger;
import result.GameResult;
import result.JsonGameResultManager;
import util.DurationUtil;
import util.javafx.ImageStorage;
import util.OrdinalImageStorage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

public class GameController {
    private static final ImageStorage<Integer> imageStorage = new OrdinalImageStorage(GameController.class,
            "soldier.png",
            "black.png",
            "black-rotated.png",
            "white.png",
            "white-rotated.png");
    @FXML
    private GridPane grid;
    @FXML
    private GridPane col_grid;
    @FXML
    private GridPane row_grid;
    @FXML
    private TextField numberofSteps;
    @Setter
    private String playerName;
    @FXML
    private Label stopwatchLabel;

    private SoldierState state;

    private final IntegerProperty steps = new SimpleIntegerProperty(0);
    private Instant startTime;
    private AnimationTimer timer;

    @FXML
    private void initialize() {
        loadCannon();
        resetGame();
        registerKeyEventHandler();
        bindNumberOfMoves();
        startTimer();
    }

    private void bindNumberOfMoves() {
        numberofSteps.textProperty().bind(steps.asString());
    }
    private void loadCannon(){
        int rows = 16;
        int columns = 16;
        ImageView triangle;
        for (int row = 0; row < rows; row++){
            for (int column = 0; column < columns; column++) {

                if(row==0 && (column==2 || column==5 || column==9 || column==11 || column==13)) {
                    triangle = createImageViewForPieceOnPosition(1, row, column);
                    row_grid.add(triangle, column, row);                }
                else if(column==0 &&(row==2 || row==6 || row==8 || row==6 || row==10 || row==12)){
                    triangle = createImageViewForPieceOnPosition(2, row, column);
                    col_grid.add(triangle, column, row);                }
                else if(row==0 && (column==3 || column==8 )){
                    triangle = createImageViewForPieceOnPosition(3, row, column);
                    row_grid.add(triangle, column, row);                }
                else if(column==0 &&(row==1 || row==4 || row==7 || row==11)){
                    triangle = createImageViewForPieceOnPosition(4, row, column);
                    col_grid.add(triangle, column, row);                }
                else {
                    continue;
                }
                triangle.setFitWidth(32);
                triangle.setFitHeight(32);
            }
        }
    }

    private void resetGame() {
        clearGrid();
        createState();
        populateGrid();
        steps.set(0);
        startTimer();
    }
    private void createState() {
        state = new SoldierState();
        state.solvedProperty().addListener(this::handleSolved);
    }

    private void handleSolved(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            Platform.runLater(this::showSolvedAlert);
        }
    }

    private void showSolvedAlert() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("Congratulations, you have solved the game!");
        stopTimer();
        saveGameResult();
        alert.showAndWait();
        showHighScoreTable();
    }

    private void showHighScoreTable() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("High Score Table");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> resetGame());
            stage.show();
        } catch (IOException e) {
            Logger.error(e, "Failed to load high score table", e);
        }
    }

    private void saveGameResult() {
        Duration elapsed = Duration.between(startTime, Instant.now());
        var gameResult = GameResult.builder()
                .playerName(playerName)
                .solved(true)
                .steps(steps.get())
                .duration(elapsed)
                .created(ZonedDateTime.now())
                .build();

        try {
            var gameResultManager = new JsonGameResultManager(Path.of("results.json"));
            gameResultManager.add(gameResult);
            Logger.info("Game result saved: {}", gameResult);
        } catch (IOException e) {
            Logger.error(e, "Failed to save game result");
        }
    }


    private void startTimer() {
        startTime = Instant.now();
        if (timer != null) {
            timer.stop();
        }


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Duration elapsed = Duration.between(startTime, Instant.now());
                stopwatchLabel.setText(DurationUtil.formatDuration(elapsed));
            }
        };
        timer.start();
    }

    private void stopTimer() {
        timer.stop();
        Logger.info("Timer stopped.");
    }

    private void clearGrid() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                getGridNodeAtPosition(grid, row, col)
                        .ifPresent(node -> ((StackPane) node).getChildren().clear());
            }
        }
    }


    private void populateGrid() {
        for (var row = 0; row < grid.getRowCount(); row++) {
            for (var col = 0; col < grid.getColumnCount(); col++) {
                var square = createSquare(row, col);
                grid.add(square, col, row);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        if(row==13 && col==13) {
            square.setStyle("-fx-background-color: black;");
        }
        if(row==14 && col==6){
            square.setStyle("-fx-background-color: black;");
        }
        var imageView = createSoldierImageViewForPieceOnPosition(0, row, col);
        square.getChildren().add(imageView);

        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }


    private ImageView createImageViewForPieceOnPosition(int index, int row, int col) {
        var imageView = new ImageView(imageStorage.get(index).orElseThrow());
        return imageView;
    }

    private ImageView createSoldierImageViewForPieceOnPosition(int index, int row, int col) {
        var imageView = new ImageView(imageStorage.get(0).orElseThrow());
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.visibleProperty().bind(createBindingToCheckPieceIsOnPosition(index, row, col));
        return imageView;
    }
    private BooleanBinding createBindingToCheckPieceIsOnPosition(int index, int row, int col) {
        return new BooleanBinding() {
            {
                super.bind(state.positionProperty());
            }
            @Override
            protected boolean computeValue() {
                var position = state.getPosition(0);
                return position.row() == row && position.col() == col;
            }
        };
    }


    @FXML
    private void handleMouseClick(MouseEvent event) {
        final var source = (Node) event.getSource();
        final var row = GridPane.getRowIndex(source);
        final var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square ({},{})", row, col);
        var direction = getDirectionFromClickPosition(row, col);
        direction.ifPresentOrElse(this::performMove,
                () -> Logger.warn("Click does not correspond to any direction"));
    }

    private Optional<Direction> getDirectionFromClickPosition(int row, int col) {
        var soldierPos = state.getPosition(SoldierState.SOLDIER);
        Direction direction = null;
        try {
            direction = Direction.of(row - soldierPos.row(), col - soldierPos.col());
        } catch (IllegalArgumentException e) {
        }
        return Optional.ofNullable(direction);
    }

    private void performMove(Direction direction) {
        if (state.isLegalMove(direction)) {
            Logger.info("Moving {}", direction);
            state.makeMove(direction);
            Logger.trace("New state after move: {}", state);
            steps.set(steps.get() + 1);
        } else {
            Logger.warn("Illegal move: {}", direction);
        }
    }

    private void registerKeyEventHandler() {
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(this::handleKeyPress));
    }


    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        var restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        var quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        if (restartKeyCombination.match(keyEvent)) {
            Logger.debug("Restarting game");
            resetGame();
        } else if (quitKeyCombination.match(keyEvent)) {
            Logger.debug("Exiting");
            Platform.exit();
        } else if (keyEvent.getCode() == KeyCode.UP) {
            Logger.debug("UP pressed");
            performMove(Direction.UP);
        } else if (keyEvent.getCode() == KeyCode.RIGHT) {
            Logger.debug("RIGHT pressed");
            performMove(Direction.RIGHT);
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            Logger.debug("DOWN pressed");
            performMove(Direction.DOWN);
        } else if (keyEvent.getCode() == KeyCode.LEFT) {
            Logger.debug("LEFT pressed");
            performMove(Direction.LEFT);
        }
    }
    private static Optional<Node> getGridNodeAtPosition(GridPane gridPane, int row, int col) {
        return gridPane.getChildren().stream()
                .filter(child -> {
                    Integer rowIndex = GridPane.getRowIndex(child);
                    Integer colIndex = GridPane.getColumnIndex(child);
                    return rowIndex != null && colIndex != null && rowIndex == row && colIndex == col;
                })
                .findFirst();
    }




}
