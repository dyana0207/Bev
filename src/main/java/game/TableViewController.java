package game;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import result.GameResult;
import result.JsonGameResultManager;
import util.DurationUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TableViewController {
    @FXML
    private TableView<GameResult> tableView;

    @FXML
    private TableColumn<GameResult, String> playerName;

    @FXML
    private TableColumn<GameResult, Integer> steps;

    @FXML
    private TableColumn<GameResult, String> duration;

    @FXML
    private TableColumn<GameResult, String> created;

    @FXML
    private void initialize() throws IOException {
        playerName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        steps.setCellValueFactory(new PropertyValueFactory<>("steps"));
        duration.setCellValueFactory(
                cellData -> {
                    var duration = cellData.getValue().getDuration();
                    return new ReadOnlyStringWrapper(DurationUtil.formatDuration(duration));
                });
        created.setCellValueFactory(
                cellData -> {
                    var dateTime = cellData.getValue().getCreated();
                    var formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
                    return new ReadOnlyStringWrapper(formatter.format(dateTime));
                }
        );
        ObservableList<GameResult> observableList = FXCollections.observableArrayList();
        observableList.addAll(new JsonGameResultManager(Path.of("results.json")).getBest(10));
        tableView.setItems(observableList);
    }
}
