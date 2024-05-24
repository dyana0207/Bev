package model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterizedPositionTest {
    void assertPosition(int expectedRow, int expectedCol, Position position) {
        assertAll("position",
                () -> assertEquals(expectedRow, position.row()),
                () -> assertEquals(expectedCol, position.col())
        );
    }

    static Stream<Position> positionProvider() {
        return Stream.of(
                new Position(1, 1),
                new Position(3, 3),
                new Position(4, 4),
                new Position(5, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void move(Position position) {
        assertPosition(position.row() - 1, position.col(), position.move(Direction.UP));
        assertPosition(position.row(), position.col() + 1, position.move(Direction.RIGHT));
        assertPosition(position.row() + 1, position.col(), position.move(Direction.DOWN));
        assertPosition(position.row(), position.col() - 1, position.move(Direction.LEFT));
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void moveUp(Position position) {
        assertPosition(position.row() - 1, position.col(), position.moveUp());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void moveRight(Position position) {
        assertPosition(position.row(), position.col() + 1, position.moveRight());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void moveDown(Position position) {
        assertPosition(position.row() + 1, position.col(), position.moveDown());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void moveLeft(Position position) {
        assertPosition(position.row(), position.col() - 1, position.moveLeft());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void testToString(Position position) {
        assertEquals(String.format("(%d,%d)", position.row(), position.col()), position.toString());
    }
}
