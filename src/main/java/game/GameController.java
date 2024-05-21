package solver;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.Direction;
import model.Position;
import model.SoldierState;
import org.tinylog.Logger;

import java.time.Instant;
import java.util.Optional;

public class GameController {
    @FXML
    private GridPane grid;
    @FXML
    private GridPane col_grid;
    @FXML
    private GridPane row_grid;

    private ImageView soldierImage;
    private SoldierState state;

    @FXML
    private void initialize() {
        loadCannon();
        populateGrid();
        resetGame();
    }
    private void loadCannon(){
        int rows = 16;
        int columns = 16;

        Image white = new Image("/images/white.png");
        Image black = new Image("/images/black.png");
        Image white_rotated = new Image("/images/white-rotated.png");
        Image black_rotated = new Image("/images/black-rotated.png");
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                ImageView triangle;
                if(row==0 && (column==2 || column==5 || column==9 || column==11 || column==13)){
                    triangle = new ImageView(black);
                    row_grid.add(triangle, column, row);
                }
                else if(column==0 &&(row==2 || row==6 || row==8 || row==6 || row==10 || row==12)){
                    triangle = new ImageView(black_rotated);
                    col_grid.add(triangle, column, row);
                }
                else if(row==0 && (column==3 || column==8 )){
                    triangle = new ImageView(white);
                    row_grid.add(triangle, column, row);
                }
                else if(column==0 &&(row==1 || row==4 || row==7 || row==11)){
                    triangle = new ImageView(white_rotated);
                    col_grid.add(triangle, column, row);
                }
                else {
                    continue;
                }

                triangle.setFitWidth(32);
                triangle.setFitHeight(32);
                triangle.setTranslateX((32 - triangle.getBoundsInParent().getWidth()) / 2);
                triangle.setTranslateY((32 - triangle.getBoundsInParent().getHeight()) / 2);
            }
        }
    }
    public void initializeSoldier(){
        String imagePath = "/images/soldier.png";
        //Logger.debug("Loading image resource {}", imagePath);
        Image soldier = new Image(imagePath);
        soldierImage = new ImageView(soldier);
        soldierImage.setFitWidth(32);
        soldierImage.setFitHeight(32);
        System.out.println("Katona");
    }
    private void resetGame() {
        state = new SoldierState();
        //clearGrid();
        initializeSoldier();
        showStateOnGrid();
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
                var cell = new StackPane();
                cell.setOnMouseClicked(this::handleMouseClick);
                grid.add(cell, col, row);
            }
        }
    }
    @FXML
    private void handleMouseClick(MouseEvent event) {
        final var source = (Node) event.getSource();
        final var row = GridPane.getRowIndex(source);
        final var col = GridPane.getColumnIndex(source);
       // Logger.debug("Click on square ({},{})", row, col);
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
    private void showStateOnGrid() {
        var pos = state.getPosition(0);
        var pieceView = soldierImage;
        getGridNodeAtPosition(grid, pos)
                .ifPresent(node -> ((StackPane) node).getChildren().add(pieceView));

    }
    private void performMove(Direction direction) {
        if (state.isLegalMove(direction)) {
           /* Logger.info("Move: {}", direction);
            var oldState = state.cloneState();

            state.move(direction);
            Logger.trace("New state: {}", state);
            updateStateOnGrid(oldState, state);
            numberOfMoves.set(numberOfMoves.get() + 1);
            if(numberOfMoves.get()%2==0){
                state.ACTIVE.set((1));
            }else {
                state.ACTIVE.set((2));
            }
            state.numberOfMovesForMove(numberOfMoves);
            if (state.isGoal()) {
                isSolved.set(true);
            }
        } else {
            Logger.warn("Invalid move: {}", direction);
        }*/
    }}
    private static Optional<Node> getGridNodeAtPosition(GridPane gridPane, int row, int col) {
        return gridPane.getChildren().stream()
                .filter(child -> {
                    Integer rowIndex = GridPane.getRowIndex(child);
                    Integer colIndex = GridPane.getColumnIndex(child);
                    return rowIndex != null && colIndex != null && rowIndex == row && colIndex == col;
                })
                .findFirst();
    }
    private static Optional<Node> getGridNodeAtPosition(GridPane gridPane, Position pos) {
        return getGridNodeAtPosition(gridPane, pos.row(), pos.col());
    }




}
